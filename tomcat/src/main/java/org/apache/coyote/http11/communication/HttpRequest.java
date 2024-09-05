package org.apache.coyote.http11.communication;

public interface HttpRequest {

    HttpMethod getHttpMethod();

    String getRequestUri();
}
