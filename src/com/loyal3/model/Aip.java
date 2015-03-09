package com.loyal3.model;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/12/14
 * Time: 9:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class Aip extends L3BaseColumns {
    public static final String TABLE_NAME = "aips";

    public static final Uri CONTENT_URI =  Uri.parse("content://" + L3Contract.AUTHORITY + "/" + TABLE_NAME);

    public static final String CONTENT_TYPE = "com.loyal3.provider.model/com.loyal3.aip";

    public static final String AMOUNT = "amount";

    public static final String DAY = "day";

    public static final String PAYMENT_METHOD_ID  = "payment_method_id";

    public static final String OFFER_ID  = "offer_id";
}
