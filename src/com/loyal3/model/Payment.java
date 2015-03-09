package com.loyal3.model;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/11/14
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class Payment extends L3BaseColumns {
    public static final String TABLE_NAME = "payments";

    public static final Uri CONTENT_URI =  Uri.parse("content://" + L3Contract.AUTHORITY + "/" + TABLE_NAME);

    public static final String CONTENT_TYPE = "com.loyal3.provider.model/com.loyal3.payment";

    public static final String PAYMENT_TYPE = "type";

    public static final String PAYMENT_ID = "payment_id";

    public static final String ACCOUNT_ID = "account_id";

    public static final String FIRST_NAME = "first_name";

    public static final String LAST_NAME = "last_name";

    public static final String ACCOUNT_NUMBER = "account_number";

    public static final String ROUTING_NUMBER = "routing_number";

    public static final String AMOUNT = "amount";

    private Payment() {
        // kill the instantiation
    }

    public enum PaymentType {
        CASH(0), CHECKING(1), CREDIT_CARD(2);
        public final int type;
        PaymentType(int type) {
            this.type = type;
        }

        public static PaymentType fromType(int type) {
            PaymentType[] allValues = PaymentType.values();
            for (PaymentType value : allValues) {
                if (type == value.type) return value;
            }
            return null;
        }
    }
}
