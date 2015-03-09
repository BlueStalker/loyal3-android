package com.loyal3.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.loyal3.model.*;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 8/22/14
 */
public class Loyal3DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "Loyal3DatabaseHelper";


    private static Loyal3DatabaseHelper sSingleton = null;

    static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "loyal3.db";


    public Loyal3DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREATE offers table
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE " + Offer.TABLE_NAME + " (");
        builder.append(L3BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
        builder.append(Offer.OFFER_ID + " TEXT, ");
        builder.append(Offer.OFFER_NAME + " TEXT, ");
        builder.append(Offer.OFFER_TYPE + " TEXT, ");
        builder.append(Offer.STOCK_SYMBOL + " TEXT, ");
        builder.append(Offer.EXCHANGE_NAME + " TEXT, ");
        builder.append(Offer.ORGANIZATION_ID + " TEXT, ");
        builder.append(Offer.ORGANIZATION_NAME + " TEXT, ");
        builder.append(Offer.ESTIMATED_PRICING_DATE + " TEXT, ");
        builder.append(Offer.PRICE_RANGE_LOW + " REAL, ");
        builder.append(Offer.PRICE_RANGE_HIGH + " REAL, ");
        builder.append(Offer.IPO_PRICE + " REAL, ");
        builder.append(Offer.MUST_RECONFIRM + " TEXT, ");
        builder.append(Offer.MINIMUM_DOLLAR_AMOUNT + " REAL, ");
        builder.append(Offer.MEDIAN_DOLLAR_AMOUNT + " REAL, ");
        builder.append(Offer.MAXIMUM_DOLLAR_AMOUNT + " REAL, ");
        builder.append(Offer.START_AT + " TEXT, ");
        builder.append(Offer.END_AT + " TEXT, ");
        builder.append(Offer.IS_PRIVATE + " SMALLINT, ");
        builder.append(Offer.ALLOW_MONTHLY + " SMALLINT, ");
        builder.append(Offer.ALLOW_ONETIME + " SMALLINT, ");
        builder.append(Offer.SUITABILITY_REQUIRED + " SMALLINT, ");
        builder.append(Offer.IS_CLIENT + " SMALLINT, ");
        builder.append(Offer.MARKET_CLOSE_PRICE + " SMALLINT, ");
        builder.append(Offer.LAST_UPDATED + " TIMESTAMP");
        builder.append(");");
        db.execSQL(builder.toString());

        // CREATE accounts table
        builder = new StringBuilder();
        builder.append("CREATE TABLE " + Account.TABLE_NAME + " (");
        builder.append(L3BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
        builder.append(Account.ACCOUNT_ID + " TEXT, ");
        builder.append(Account.ACCOUNT_STATUS + " INT, ");
        builder.append(Account.TOTAL_VALUE + " REAL, ");
        builder.append(Account.CASH_VALUE + " REAL, ");
        builder.append(Account.LAST_UPDATED + " TIMESTAMP");
        builder.append(");");
        db.execSQL(builder.toString());

        // CREATE plans table
        builder = new StringBuilder();
        builder.append("CREATE TABLE " + Plan.TABLE_NAME + " (");
        builder.append(L3BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
        builder.append(Plan.PLAN_ID + " TEXT, ");
        builder.append(Plan.OFFER_ID + " TEXT, ");
        builder.append(Plan.ORGANIZATION_ID + " TEXT, ");
        builder.append(Plan.IPO_RESERVATION_AMOUNT + " REAL, ");
        builder.append(Plan.IPO_RESERVATION_STATE + " TEXT, ");
        builder.append(Plan.SHARES_OWNED + " REAL, ");
        builder.append(Plan.SHARES_PRICE + " REAL, ");
        builder.append(Plan.CURRENT_VALUE + " REAL, ");
        builder.append(Plan.LAST_UPDATED + " TIMESTAMP");
        builder.append(");");
        db.execSQL(builder.toString());

        // CREATE Aip table
        builder = new StringBuilder();
        builder.append("CREATE TABLE " + Aip.TABLE_NAME + " (");
        builder.append(L3BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
        builder.append(Aip.AMOUNT + " REAL, ");
        builder.append(Aip.PAYMENT_METHOD_ID + " TEXT, ");
        builder.append(Aip.OFFER_ID + " TEXT, ");
        builder.append(Aip.DAY + " INT, ");
        builder.append(Aip.LAST_UPDATED + " TIMESTAMP");
        builder.append(");");
        db.execSQL(builder.toString());


        // CREATE transactions table
        builder = new StringBuilder();
        builder.append("CREATE TABLE " + Transaction.TABLE_NAME + " (");
        builder.append(L3BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
        builder.append(Transaction.PLAN_ID + " TEXT, ");
        builder.append(Transaction.OFFER_ID + " TEXT, ");
        builder.append(Transaction.DESCRIPTION + " TEXT, ");
        builder.append(Transaction.AMOUNT + " REAL, ");
        builder.append(Transaction.SHARES + " REAL, ");
        builder.append(Transaction.PRICE + " REAL, ");
        builder.append(Transaction.STATUS + " TEXT, ");
        builder.append(Transaction.DATE + " TEXT, ");
        builder.append(Transaction.LAST_UPDATED + " TIMESTAMP");
        builder.append(");");
        db.execSQL(builder.toString());

        // CREATE payments table
        builder = new StringBuilder();
        builder.append("CREATE TABLE " + Payment.TABLE_NAME + " (");
        builder.append(L3BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
        builder.append(Payment.PAYMENT_TYPE + " INT, ");
        builder.append(Payment.PAYMENT_ID + " TEXT, ");
        builder.append(Payment.ACCOUNT_ID + " TEXT, ");
        builder.append(Payment.FIRST_NAME + " TEXT, ");
        builder.append(Payment.LAST_NAME + " TEXT, ");
        builder.append(Payment.ACCOUNT_NUMBER + " TEXT, ");
        builder.append(Payment.ROUTING_NUMBER + " TEXT, ");
        builder.append(Payment.AMOUNT + " REAL, ");
        builder.append(Payment.LAST_UPDATED + " TIMESTAMP");
        builder.append(");");
        db.execSQL(builder.toString());
    }

    public static synchronized Loyal3DatabaseHelper getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new Loyal3DatabaseHelper(context);
        }
        return sSingleton;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Gets called when the database is upgraded, i.e. the version number changes
    }
}
