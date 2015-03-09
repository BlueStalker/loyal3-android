package com.loyal3.rest.resource;


import org.json.JSONException;
import org.json.JSONObject;

public class TransferRequest implements RequestResource {
    private Double amount;
    private String fromAccountId;
    private String toAccountId;

    public Double getAmount() {
        return amount;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    public TransferRequest(Double amount, String from_id, String to_id) {
        this.fromAccountId = from_id;
        this.toAccountId = to_id;
        this.amount = amount;
    }

    @Override
    public JSONObject getRequestBody() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("amount", amount);
            obj.put("from_account_id", fromAccountId);
            obj.put("to_account_id", toAccountId);
            return obj;
        } catch (JSONException e) {
            return null;
        }
    }
}