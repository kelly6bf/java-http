package org.apache.coyote.http11.message.request;

import java.util.Arrays;
import java.util.Optional;

import org.apache.coyote.http11.FileExtensionType;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpVersion;

public class HttpRequestLine {

    private static final String HTTP_REQUEST_LINE_DATA_SEPARATOR = " ";
    private static final int HTTP_REQUEST_LINE_DATA_COUNT = 3;
    private static final int HTTP_METHOD_ORDER = 0;
    private static final int HTTP_REQUEST_URI_ORDER = 1;
    private static final int HTTP_VERSION_ORDER = 2;

    private final HttpMethod httpMethod;
    private final HttpRequestUri httpRequestUri;
    private final HttpVersion httpVersion;

    public HttpRequestLine(final String httpRequestLineValue) {
        validateHttpRequestLineValue(httpRequestLineValue);
        String[] httpRequestLineData = parseHttpRequestLineFields(httpRequestLineValue);
        this.httpMethod = HttpMethod.valueOf(httpRequestLineData[HTTP_METHOD_ORDER]);
        this.httpRequestUri = new HttpRequestUri(httpRequestLineData[HTTP_REQUEST_URI_ORDER]);
        this.httpVersion = HttpVersion.getByValue(httpRequestLineData[HTTP_VERSION_ORDER]);
    }

    private void validateHttpRequestLineValue(final String httpRequestLineValue) {
        if (httpRequestLineValue == null || httpRequestLineValue.isBlank()) {
            throw new IllegalArgumentException("유효하지 않은 HTTP Request Line 값 입니다. - " + httpRequestLineValue);
        }
    }

    private String[] parseHttpRequestLineFields(final String httpRequestLineValue) {
        final String[] httpRequestLineFields = httpRequestLineValue.split(HTTP_REQUEST_LINE_DATA_SEPARATOR);
        validateParsedHttpRequestLineFieldsSize(httpRequestLineFields);

        return httpRequestLineFields;
    }

    private void validateParsedHttpRequestLineFieldsSize(final String[] httpRequestLineFields) {
        if (httpRequestLineFields.length != HTTP_REQUEST_LINE_DATA_COUNT) {
            System.out.println(Arrays.toString(httpRequestLineFields));
            throw new IllegalArgumentException("유효하지 않은 HTTP Request Line 필드 개수입니다. - " + httpRequestLineFields.length);
        }
    }

    public boolean matchRequestUri(final String uri) {
        return httpRequestUri.matchRequestUri(uri);
    }

    public boolean matchHttpMethod(final HttpMethod httpMethod) {
        return this.httpMethod == httpMethod;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public boolean isStaticResourceUri() {
        return httpRequestUri.isStaticResourceUri();
    }

    public HttpRequestUri getHttpRequestUri() {
        return httpRequestUri;
    }

    public Optional<FileExtensionType> getFileExtensionType() {
        return httpRequestUri.getFileExtensionType();
    }
}