package com.loyal3.rest.resource;

import org.json.JSONException;
import org.json.JSONObject;

public class SellRequest implements RequestResource {
    private Double shares;

    public Double getShares() {
        return shares;
    }

    public SellRequest(Double shares) {
        this.shares = shares;
    }

    @Override
    public JSONObject getRequestBody() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("shares", shares);
            return obj;
        } catch (JSONException e) {
            return null;
        }
    }
}
