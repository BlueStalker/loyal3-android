package com.loyal3.rest.resource;

import org.json.JSONException;
import org.json.JSONObject;

public class BuyResults implements ResponseResource {
    private Integer result_code;
    private String confirmationNumber;

    public static final Integer OK                    = 0  ;
    public static final Integer INSUFFICIENT_FUNDS    = 1  ;
    public static final Integer CLOSED_OFFER          = 2  ;
    public static final Integer DUPLICATE             = 3  ;
    public static final Integer INVALID_AMOUNT        = 4  ;
    public static final Integer INVALID_PAYMENT       = 5  ;
    public static final Integer NOT_ALLOWED           = 6  ;
    public static final Integer FAILED                = 7  ;
    public static final Integer EXCEED_MONTHLY_MAX    = 8  ;
    public static final Integer OUT_OF_PURCHASE_LIMIT = 9  ;
    public static final Integer INVALID_PAYMENT_TYPE  = 10 ;

    public BuyResults(JSONObject json) throws JSONException {
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