package com.loyal3.rest.resource;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 */
public class LoginResults implements ResponseResource {
    private String result_code;
    private String server_time;
    private int server_time_epoch;

    public LoginResults(JSONObject json) throws JSONException {
        this.result_code = json.getString("result_code");
        this.server_time = json.getString("server_time");
        this.server_time_epoch = json.getInt("server_time_epoch");
    }

    public String getResultCode() {
        return result_code;
    }

    public String getServerTime() {
        return server_time;
    }

    public int getServerTimeEpoch() {
        return server_time_epoch;
    }
}
