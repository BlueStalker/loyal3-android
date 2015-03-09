package com.loyal3.model;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 */
public class L3Contract {

    public static final String AUTHORITY = "com.loyal3.provider.model.L3Contract";

    public static final String SAVED_USERNAME = "UserName";

    public static final String SAVED_PASSWORD = "Password";

    public static final String COOKIE = "Cookie";

    public static final String APPUSER = "CurrentUser";

    public static final int COMMON_OK_RESULT_CODE = 930;

    public static final int LOGIN = 10000;

    public static final int OFFERS = 10001;

    public static final int OFFER = 10002;

    public static final int ACCOUNTS = 10003;

    public static final int PLANS = 10004;

    public static final int PLAN = 10005;

    public static final int TRANSACTIONS = 10006;

    public static final int PAYMENTS = 10007;

    public static final int AIP = 10008;

    public static final int BUY = 10009;

    public static final int SELL = 10010;

    public static final int TRANSFER = 10020;
    // This is a new life
    public static final int LOGOUT = 20000;

    private L3Contract() {}
}
