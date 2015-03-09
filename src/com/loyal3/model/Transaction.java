package com.loyal3.model;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/8/14
 * Time: 9:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class Transaction extends L3BaseColumns {
    public static final String TABLE_NAME = "transactions";

    public static final Uri CONTENT_URI =  Uri.parse("content://" + L3Contract.AUTHORITY + "/" + TABLE_NAME);

    public static final String CONTENT_TYPE = "com.loyal3.provider.model/com.loyal3.transaction";

    public static final String DATE = "date";

    public static final String PLAN_ID  = "plan_id";

    public static final String OFFER_ID = "offer_id";

    public static final String DESCRIPTION = "description";

    public static final String AMOUNT = "amount";

    public static final String SHARES = "shares";

    public static final String PRICE = "price";

    public static final String STATUS = "status";

    public enum TransactionStatus {
        POSTED("posted"), PENDING("pending");
        public final String type;
        TransactionStatus(String type) {
            this.type = type;
        }
    }
}
