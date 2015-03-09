package com.loyal3.rest;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 8/25/14
 */
public class GenericResponse {
    /**
     * The HTTP status code
     */
    public int status;

    /**
     * The HTTP headers received in the response
     */
    public Map<String, List<String>> headers;

    /**
     * The response body, if any
     */
    public byte[] body;

    protected GenericResponse(int status, Map<String, List<String>> headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }
}
