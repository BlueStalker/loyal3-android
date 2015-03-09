package com.loyal3.model;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 */
public class Account extends L3BaseColumns {
    public static final String TABLE_NAME = "accounts";

    public static final Uri CONTENT_URI =  Uri.parse("content://" + L3Contract.AUTHORITY + "/" + TABLE_NAME);

    public static final String CONTENT_TYPE = "com.loyal3.provider.model/com.loyal3.accounts";

    public static final String ACCOUNT_ID = "account_id";

    public static final String ACCOUNT_STATUS = "status";

    public static final String TOTAL_VALUE = "total_value";

    public static final String CASH_VALUE = "cash_value";
}
