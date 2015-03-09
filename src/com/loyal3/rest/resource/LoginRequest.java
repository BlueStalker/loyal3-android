package com.loyal3.rest.resource;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 */
public class LoginRequest implements RequestResource {
    private String username;
    private String password;
    private String apiKey;

    public LoginRequest(String username, String password, String apiKey) {
        this.username = username;
        this.password = password;
        this.apiKey = apiKey;
    }

    @Override
    public JSONObject getRequestBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
            json.put("apiKey", apiKey);
            return json;
        } catch (JSONException e) {
            return null;
        }
    }
}
