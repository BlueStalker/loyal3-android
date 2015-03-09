package com.loyal3.rest.resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/4/14
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccountResults implements ResponseResource {
    private String account_id;
    private int status;
    private double total_value;
    private double cash_value;

    public AccountResults(JSONObject json) throws JSONException {
        JSONArray jsonArray =  json.getJSONArray("accounts");
        JSONObject first = (JSONObject)jsonArray.get(0);
        this.account_id = first.getString("account_id");
        this.status = first.getInt("status");

        this.total_value = new BigDecimal(first.getDouble("total_value")).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        this.cash_value = new BigDecimal(first.getDouble("available_cash")).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    public String getAccountId() {
        return account_id;
    }

    public int getStatus() {
        return status;
    }

    public double getTotalValue() {
        return total_value;
    }

    public double getCashValue() {
        return cash_value;
    }
}
