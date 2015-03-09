package com.loyal3.rest.resource;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/9/14
 * Time: 10:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class LogoutResults implements ResponseResource {
    private String result_code;

    public LogoutResults(JSONObject json) throws JSONException {
        this.result_code = json.getString("result_code");
    }

    public String getResultCode() {
        return result_code;
    }

}
