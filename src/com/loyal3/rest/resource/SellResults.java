package com.loyal3.rest.resource;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/18/14
 * Time: 4:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class SellResults implements ResponseResource {
    private Integer result_code;
    private String confirmationNumber;

    public static final int OK                   = 0;
    public static final int INSUFFICIENT_SHARES  = 1;  // generic failure for insufficient shares to sell
    public static final int TOO_FEW_SHARES       = 2;  // sell request is less than 0
    public static final int DUPLICATE            = 3;
    public static final int FAILED_OTHER         = 4; // really generic failure. GL is down or something else
    public static final int NOT_ALLOWED          = 5;  // user is not allowed to sell other user's shares
    public static final int FAILED_SHARES_LOCKED = 6;

    public SellResults(JSONObject json) throws JSONException {
        result_code = json.getInt("result_code");
        if (!json.isNull("confirmation_number")) confirmationNumber = json.getString("confirmation_number");
    }

    public Integer getResultCode() {
        return result_code;
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

}