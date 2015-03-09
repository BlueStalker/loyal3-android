package com.loyal3.model;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 */
public class Offer extends L3BaseColumns {
    public static final String TABLE_NAME = "offers";

    public static final Uri CONTENT_URI =  Uri.parse("content://" + L3Contract.AUTHORITY + "/" + TABLE_NAME);

    public static final String CONTENT_TYPE = "com.loyal3.provider.model/com.loyal3.offer";

    public static final String CONTENT_ITEM_TYPE = "com.loyal3.provider.model.item/com.loyal3.offer";

    public static final String OFFER_NAME = "name";
    public static final String OFFER_TYPE = "type";
    public static final String STOCK_SYMBOL = "stock_symbol";
    public static final String EXCHANGE_NAME = "exchange_name";
    public static final String ORGANIZATION_ID = "organization_id";
    public static final String ORGANIZATION_NAME = "organization_name";
    public static final String OFFER_ID = "offer_id";

    // Details
    public static final String ESTIMATED_PRICING_DATE = "estimated_pricing_date";
    public static final String PRICE_RANGE_LOW = "price_range_low";
    public static final String PRICE_RANGE_HIGH = "price_range_high";
    public static final String IPO_PRICE = "ipo_price";
    public static final String MUST_RECONFIRM = "must_reconfirm";
    public static final String MINIMUM_DOLLAR_AMOUNT = "minimum_dollar_amount";
    public static final String MEDIAN_DOLLAR_AMOUNT = "median_dollar_amount";
    public static final String MAXIMUM_DOLLAR_AMOUNT = "maximum_dollar_amount";
    public static final String START_AT = "start_at";
    public static final String END_AT = "end_at";
    public static final String IS_PRIVATE = "is_private";
    public static final String ALLOW_MONTHLY = "allow_monthly";
    public static final String ALLOW_ONETIME = "allow_onetime";
    public static final String SUITABILITY_REQUIRED = "suitability_required";
    public static final String IS_CLIENT = "is_client";
    public static final String MARKET_CLOSE_PRICE = "market_close_price";



    private Offer() {
        // kill the instantiation
    }

    public enum OfferType {
        DIRECT("Direct"), IPO("IPO");
        public final String type;
        OfferType(String type) {
            this.type = type;
        }
    }
}
