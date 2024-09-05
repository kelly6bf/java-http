package org.apache.coyote.http11.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO : 전체적으로 유효하지 않은 값 파싱에 대한 검증과 예외 처리 및 테스트 보완
public class SimpleHttpRequest implements HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final String requestMessage;

    public SimpleHttpRequest(final Socket connection) {
        validateConnection(connection);
        this.requestMessage = parseRequestMessage(connection);
    }

    private void validateConnection(final Socket connection) {
        if (connection == null) {
            throw new IllegalArgumentException("유효하지 않은 Http 요청입니다.");
        }
    }

    private String parseRequestMessage(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return buildRequestMessage(reader);
        } catch (final IOException e) {
            log.warn(e.getMessage(), e);
        }

        return null;
    }

    private String buildRequestMessage(final BufferedReader reader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\r\n");
        }

        return stringBuilder.toString();
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    @Override
    public HttpMethod getHttpMethod() {
        final int httpMethodIndexNumber = 0;
        final String httpMethodValue = parseRequestLine().split(" ")[httpMethodIndexNumber];
        return HttpMethod.valueOf(httpMethodValue.toUpperCase());
    }

    @Override
    public String getRequestUri() {
        final int requestUriIndexNumber = 1;
        return parseRequestLine().split(" ")[requestUriIndexNumber];
    }

    private String parseRequestLine() {
        return requestMessage.lines()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("유효하지 않은 요청 메시지여서 처리할 수 없습니다."));
    }
}
