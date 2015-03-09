package com.loyal3.rest.resource;

import org.json.JSONException;
import org.json.JSONObject;

public class TransferResults implements ResponseResource {
    private Integer result_code;

    public static final int OK                  = 0;
    public static final int FAILED              = 1;
    public static final int INSUFFICIENT_FUNDS  = 2;
    public static final int INVALID_ACCOUNT     = 3;
    public static final int INCORRECT_AMOUNT    = 4;
    public static final int NOT_ALLOWED         = 5;
    public TransferResults(JSONObject json) throws JSONException {
        result_code = json.getInt("result_code");
    }

    public Integer getResultCode() {
        return result_code;
    }
}