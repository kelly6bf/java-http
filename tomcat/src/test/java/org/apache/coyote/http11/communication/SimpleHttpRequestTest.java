package org.apache.coyote.http11.communication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.StubSocket;

@DisplayName("HttpRequest 객체 테스트")
class SimpleHttpRequestTest {

    @DisplayName("Http 요청에 의해 생성된 Socket 객체를 생성자에 인수로 전달하면 해당 요청의 message값을 가진 HttpRequest인스턴스를 생성한다.")
    @Test
    void createSimpleHttpRequestInstance() {
        // Given
        final String request = "GET /index.html HTTP/1.1\r\nHost: localhost:8080\r\n\r\n";
        final StubSocket socket = new StubSocket(request);

        // When
        final SimpleHttpRequest httpRequest = new SimpleHttpRequest(socket);

        // Then
        assertSoftly(softAssertions -> {
            assertThat(httpRequest).isNotNull();
            assertThat(httpRequest.getRequestMessage()).isEqualTo(request);
        });
    }

    @DisplayName("생성자 인수로 null이 입력되면 예외를 발생시킨다.")
    @Test
    void throwExceptionWhenInputNull() {
        // When & Then
        assertThatThrownBy(() -> new SimpleHttpRequest(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 Http 요청입니다.");
    }

    @DisplayName("HttpRequest 메시지에서 Http Method 값을 찾아 반환한다.")
    @Test
    void getHttpMethod() {
        // Given
        final String request = "GET /index.html HTTP/1.1\r\nHost: localhost:8080\r\n\r\n";
        final StubSocket socket = new StubSocket(request);
        final SimpleHttpRequest httpRequest = new SimpleHttpRequest(socket);

        // When
        final HttpMethod httpMethod = httpRequest.getHttpMethod();

        // Then
        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @DisplayName("HttpRequest 메시지에서 Request URI 값을 찾아 반환한다.")
    @Test
    void getRequestUri() {
        // Given
        final String request = "GET /index.html HTTP/1.1\r\nHost: localhost:8080\r\n\r\n";
        final StubSocket socket = new StubSocket(request);
        final SimpleHttpRequest httpRequest = new SimpleHttpRequest(socket);

        // When
        final String requestUri = httpRequest.getRequestUri();

        // Then
        assertThat(requestUri).isEqualTo("/index.html");
    }
}
