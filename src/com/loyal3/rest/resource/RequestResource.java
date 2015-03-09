package com.loyal3.rest.resource;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 8/27/14
 */
public interface RequestResource extends Serializable {
    public JSONObject getRequestBody();
}
