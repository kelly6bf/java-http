package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            final String requestMessage = parseRequestMessage(reader);
//            String requestMessage = reader.readLine();
            if (requestMessage == null) {
                return;
            }
            System.out.println(requestMessage);
            responseIndexPage(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String parseRequestMessage(final BufferedReader reader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        String line = reader.readLine();
        if (line == null) {
            return null;
        }
//        stringBuilder.append(line);

        do {
            stringBuilder.append(line).append("\r\n");
            log.info("line = {}", line);
            line = reader.readLine();
        } while (line != null && line.isEmpty());

        return stringBuilder.toString();
    }

    private void responseIndexPage(final OutputStream outputStream) throws URISyntaxException, IOException {
        final File file = getFile("/static/index.html");
        final List<String> fileContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
        final String responseBody = String.join("", fileContent);
        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private File getFile(final String fileName) throws URISyntaxException {
        final URL resource = getClass().getResource(fileName);
        return Paths.get(resource.toURI()).toFile();
    }
}
