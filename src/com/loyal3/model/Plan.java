package com.loyal3.model;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/4/14
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class Plan extends L3BaseColumns {
    public static final String TABLE_NAME = "plans";

    public static final Uri CONTENT_URI =  Uri.parse("content://" + L3Contract.AUTHORITY + "/" + TABLE_NAME);

    public static final String CONTENT_TYPE = "com.loyal3.provider.model/com.loyal3.plan";

    public static final String CONTENT_ITEM_TYPE = "com.loyal3.provider.model.item/com.loyal3.plan";

    public static final String PLAN_ID = "plan_id";

    public static final String OFFER_ID = "offer_id";

    public static final String ORGANIZATION_ID = "org_id";

    public static final String IPO_RESERVATION_AMOUNT = "reservation_amount";

    public static final String IPO_RESERVATION_STATE = "reservation_state";

    public static final String SHARES_OWNED = "shares_owned";

    public static final String SHARES_PRICE = "shares_price";

    public static final String CURRENT_VALUE = "current_value";
}
