package com.loyal3.provider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.loyal3.model.*;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 */
public class Loyal3ContentProvider extends AbstractLoyal3Provider {

    private final ThreadLocal<Loyal3Transaction> mTransactionHolder = new ThreadLocal<Loyal3Transaction>();
    private final ThreadLocal<SQLiteDatabase> mActiveDb = new ThreadLocal<SQLiteDatabase>();

    private Loyal3DatabaseHelper mDbHelper;

    private final ContentValues mValues = new ContentValues();

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(L3Contract.AUTHORITY, Offer.TABLE_NAME, L3Contract.OFFERS);
        sURIMatcher.addURI(L3Contract.AUTHORITY, Offer.TABLE_NAME + "/*", L3Contract.OFFER);
        sURIMatcher.addURI(L3Contract.AUTHORITY, Account.TABLE_NAME, L3Contract.ACCOUNTS);
        sURIMatcher.addURI(L3Contract.AUTHORITY, Plan.TABLE_NAME, L3Contract.PLANS);
        sURIMatcher.addURI(L3Contract.AUTHORITY, Transaction.TABLE_NAME, L3Contract.TRANSACTIONS);
        sURIMatcher.addURI(L3Contract.AUTHORITY, Payment.TABLE_NAME, L3Contract.PAYMENTS);
        sURIMatcher.addURI(L3Contract.AUTHORITY, Aip.TABLE_NAME, L3Contract.AIP);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sURIMatcher.match(uri);
        String tableName;
        switch (match) {
            case L3Contract.OFFERS: {
                tableName = Offer.TABLE_NAME;
                break;
            }
            case L3Contract.OFFER: {
                tableName = Offer.TABLE_NAME;
                String offer_id = uri.getPathSegments().get(1);
                selection = appendRowId(selection, Offer.OFFER_ID, offer_id);
                break;
            }

            case L3Contract.ACCOUNTS: {
                tableName = Account.TABLE_NAME;
                break;
            }

            case L3Contract.PLANS: {
                tableName = Plan.TABLE_NAME;
                break;
            }

            case L3Contract.TRANSACTIONS: {
                tableName = Transaction.TABLE_NAME;
                break;
            }

            case L3Contract.PAYMENTS: {
                tableName = Payment.TABLE_NAME;
                break;
            }

            case L3Contract.AIP: {
                tableName = Aip.TABLE_NAME;
                break;
            }
            default:
                throw new RuntimeException("What r u deleting, man?");
        }
        return db.delete(tableName, selection, selectionArgs);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int ret = super.bulkInsert(uri, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }

    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case L3Contract.OFFERS:
                return Offer.CONTENT_TYPE;
            case L3Contract.OFFER:
                return Offer.CONTENT_ITEM_TYPE;
            case L3Contract.ACCOUNTS:
                return Account.CONTENT_TYPE;
            case L3Contract.PLANS:
                return Plan.CONTENT_TYPE;
            case L3Contract.PLAN:
                return Plan.CONTENT_ITEM_TYPE;
            case L3Contract.TRANSACTIONS:
                return Transaction.CONTENT_TYPE;
            case L3Contract.PAYMENTS:
                return Payment.CONTENT_TYPE;
            case L3Contract.AIP:
                return Aip.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri ret = super.insert(uri, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }

    @Override
    public boolean onCreate() {
        try {
            super.onCreate();
            return initialize();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private boolean initialize() {
        this.mDbHelper = new Loyal3DatabaseHelper(this.getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String orderBy) {

        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String tableName;

        final int match = sURIMatcher.match(uri);

        switch (match) {
            case L3Contract.OFFERS: {
                tableName = Offer.TABLE_NAME;
                break;
            }
            case L3Contract.OFFER: {
                tableName = Offer.TABLE_NAME;
                String offer_id = uri.getPathSegments().get(1);
                selection = appendRowId(selection, Offer.OFFER_ID, offer_id);
                break;
            }

            case L3Contract.ACCOUNTS: {
                tableName = Account.TABLE_NAME;
                break;
            }

            case L3Contract.PLANS: {
                return queryAllPlans(db, uri);
            }

            case L3Contract.TRANSACTIONS: {
                return queryAllTransactions(db, uri);
            }

            case L3Contract.PAYMENTS: {
                tableName = Payment.TABLE_NAME;
                break;
            }

            case L3Contract.AIP: {
                tableName = Aip.TABLE_NAME;
                break;
            }
            default: {
                Log.e("LOYAL3", "" + uri);
                throw new RuntimeException("What r u doing, man?");
            }
        }
        Cursor cursor = db.query(tableName, projection, selection,
                selectionArgs, null, null, orderBy);
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }


    private Cursor queryAllPlans(SQLiteDatabase db, Uri uri) {
        Cursor cursor = db.rawQuery("SELECT O.stock_symbol, P.offer_id, P.plan_id, P.shares_owned, P.shares_price, P.current_value FROM plans P INNER JOIN offers O ON p.offer_id = o.offer_id", null);
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    private Cursor queryAllTransactions(SQLiteDatabase db, Uri uri) {
        Cursor cursor = db.rawQuery("SELECT O.stock_symbol, O.organization_name, T.offer_id, T.plan_id, T.description, T.shares, T.amount, T.price, T.status, T.date FROM transactions T LEFT JOIN offers O ON T.offer_id = o.offer_id order by T.date DESC", null);
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    private String appendRowId(String selection, String column, String id) {
        return column
                + "="
                + "'" + id + "'"
                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')'
                : "");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    protected SQLiteOpenHelper getDatabaseHelper(Context context) {
        return Loyal3DatabaseHelper.getInstance(this.getContext());
    }

    @Override
    protected ThreadLocal<Loyal3Transaction> getTransactionHolder() {
        return mTransactionHolder;
    }

    @Override
    protected Uri insertInTransaction(Uri uri, ContentValues values) {
        final int match = sURIMatcher.match(uri);
        long id = 0;
        if (mActiveDb.get() == null) {
            mActiveDb.set(mDbHelper.getWritableDatabase());
        }

        switch (match) {
            case L3Contract.OFFERS:
            case L3Contract.OFFER: {
                id = insertOrUpdateOffer(values);
                break;
            }
            case L3Contract.ACCOUNTS: {
                id = insertOrUpdateAccount(values);
                break;
            }

            case L3Contract.PLANS:
            case L3Contract.PLAN: {
                id = insertOrUpdatePlan(values);
                break;
            }

            case L3Contract.TRANSACTIONS: {
                id = insertOrUpdateTransaction(values);
                break;
            }

            case L3Contract.PAYMENTS: {
                id = insertOrUpdatePayments(values);
                break;
            }

            case L3Contract.AIP: {
                id = insertOrUpdateAips(values);
                break;
            }
        }
        return ContentUris.withAppendedId(uri, id);
    }

    private long insertOrUpdateAips(ContentValues values) {
        final SQLiteDatabase db = mActiveDb.get();
        return doInsert(Aip.TABLE_NAME, values, db);
    }

    // Transactions doesn't have raw id, what we can do is delete everything and insert them again.
    private long insertOrUpdatePayments(ContentValues values) {
        final SQLiteDatabase db = mActiveDb.get();
        return doInsert(Payment.TABLE_NAME, values, db);
    }

    // Transactions doesn't have raw id, what we can do is delete everything and insert them again.
    private long insertOrUpdateTransaction(ContentValues values) {
        final SQLiteDatabase db = mActiveDb.get();
        return doInsert(Transaction.TABLE_NAME, values, db);
    }


    private long insertOrUpdateAccount(ContentValues values) {
        String accountId = values.getAsString(Account.ACCOUNT_ID);
        final SQLiteDatabase db = mActiveDb.get();
        long rawId = getDBRawId(Account.TABLE_NAME, Account.ACCOUNT_ID, accountId, db);
        if (rawId != -1) {
            updateTable(Account.TABLE_NAME, Account.ACCOUNT_ID, values, accountId, db);
            return rawId;
        } else {
            return doInsert(Account.TABLE_NAME, values, db);
        }
    }

    private long insertOrUpdatePlan(ContentValues values) {
        String planId = values.getAsString(Plan.PLAN_ID);
        final SQLiteDatabase db = mActiveDb.get();
        long rawId = getDBRawId(Plan.TABLE_NAME, Plan.PLAN_ID, planId, db);
        if (rawId != -1) {
            updateTable(Plan.TABLE_NAME, Plan.PLAN_ID, values, planId, db);
            return rawId;
        } else {
            return doInsert(Plan.TABLE_NAME, values, db);
        }
    }

    private long insertOrUpdateOffer(ContentValues values) {
        String offerId = values.getAsString(Offer.OFFER_ID);
        final SQLiteDatabase db = mActiveDb.get();
        long rawId = getDBRawId(Offer.TABLE_NAME, Offer.OFFER_ID, offerId, db);
        if (rawId != -1) {
            updateTable(Offer.TABLE_NAME, Offer.OFFER_ID, values, offerId, db);
            return rawId;
        } else {
            return doInsert(Offer.TABLE_NAME, values, db);
        }

    }

    private long getDBRawId(String table, String column, String id, SQLiteDatabase db) {
        String query = "SELECT _id from " + table + " where " + column + " = ?";
        long rawId = -1;
        Cursor c = db.rawQuery(query, new String[]{id});
        if (c.moveToFirst()) rawId = c.getLong(0);
        c.close();
        return rawId;
    }

    private long doInsert(String table, ContentValues values, SQLiteDatabase db) {
    /*    mValues.clear();
        mValues.putAll(values);
      */
        return db.insert(table, null, values);
    }

    private int updateTable(String table, String column, ContentValues values, String id, SQLiteDatabase db) {
        if (values.size() == 0) return 0;
    /*    mValues.clear();
        mValues.putAll(values);
    */
        return db.update(table, values, column + "= ?", new String[]{id});
    }

    @Override
    protected int deleteInTransaction(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    protected int updateInTransaction(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    protected boolean yield(Loyal3Transaction transaction) {
        return false;
    }

    @Override
    protected void notifyChange() {

    }

    @Override
    public void onBegin() {
        // Do nothing now
    }

    @Override
    public void onCommit() {

    }

    @Override
    public void onRollback() {

    }
}
