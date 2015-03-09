package com.loyal3.rest;

import java.util.List;
import java.util.Map;

import android.content.UriMatcher;
import android.net.Uri;
import android.content.Context;
import com.loyal3.model.*;
import com.loyal3.rest.request.*;
import com.loyal3.rest.resource.RequestResource;
import org.json.JSONException;

public class RestMethodFactory {

    private static RestMethodFactory instance;
    private static Object lock = new Object();
    private UriMatcher uriMatcher;
    private Context mContext;

    private RestMethodFactory(Context context) {
        mContext = context;
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(L3Contract.AUTHORITY, Offer.TABLE_NAME, L3Contract.OFFERS);
        uriMatcher.addURI(L3Contract.AUTHORITY, Login.TABLE_NAME, L3Contract.LOGIN);
        uriMatcher.addURI(L3Contract.AUTHORITY, Account.TABLE_NAME, L3Contract.ACCOUNTS);
        uriMatcher.addURI(L3Contract.AUTHORITY, Plan.TABLE_NAME, L3Contract.PLANS);
        uriMatcher.addURI(L3Contract.AUTHORITY, Transaction.TABLE_NAME, L3Contract.TRANSACTIONS);
        uriMatcher.addURI(L3Contract.AUTHORITY, Payment.TABLE_NAME, L3Contract.PAYMENTS);
        uriMatcher.addURI(L3Contract.AUTHORITY, Logout.TABLE_NAME, L3Contract.LOGOUT);
        uriMatcher.addURI(L3Contract.AUTHORITY, Buy.TABLE_NAME + "/*", L3Contract.BUY);
        uriMatcher.addURI(L3Contract.AUTHORITY, Sell.TABLE_NAME + "/*", L3Contract.SELL);
        uriMatcher.addURI(L3Contract.AUTHORITY, Transfer.TABLE_NAME, L3Contract.TRANSFER);
    }

    public static RestMethodFactory getInstance(Context context) {
        synchronized (lock) {
            if (instance == null) {
                instance = new RestMethodFactory(context);
            }
        }

        return instance;
    }

    public RestMethod getRestMethod(Uri resourceUri, Method method, Map<String, List<String>> headers, RequestResource data) {

        switch (uriMatcher.match(resourceUri)) {
            case L3Contract.OFFERS:
                if (method == Method.GET) {
                    if (data != null && data.getRequestBody().has("offer_id")) {
                        try {
                            String offer_id =  data.getRequestBody().getString("offer_id");
                            return new GetOfferDetailRestMethod(mContext, offer_id);
                        } catch (JSONException e) {
                            throw new RuntimeException("Error for Offer Detail Request", e);
                        }
                    } else{
                      return new GetOffersRestMethod(mContext);
                    }
                }
                break;
            case L3Contract.LOGIN:
                if (method == Method.POST) {
                    return new PostLoginRequest(mContext, data);
                }
                break;
            case L3Contract.ACCOUNTS:
                if (method == Method.GET) {
                    return new GetAccountsRestMethod(mContext);
                }

            case L3Contract.PLANS:
                if (method == Method.GET) {
                    return new GetPlansRestMethod(mContext);
                }

            case L3Contract.TRANSACTIONS:
                if (method == Method.GET) {
                    return new GetTransactionRestMethod(mContext);
                }
            case L3Contract.LOGOUT:
                if (method == Method.GET) {
                    return new GetLogoutRequest(mContext);
                }

            case L3Contract.PAYMENTS:
                if (method == Method.GET) {
                    return new GetPaymentRestMethod(mContext);
                }
            case L3Contract.BUY:
                if (method == Method.POST) {
                    try {
                        String offer_id = resourceUri.getPathSegments().get(1);
                        return new PostBuyRequest(mContext, data, offer_id);
                    } catch (Exception e) {
                        throw new RuntimeException("wrong :: " + e.getMessage());
                    }
                }
                break;
            case L3Contract.SELL:
                if (method == Method.POST) {
                    try {
                        String planId = resourceUri.getPathSegments().get(1);
                        return new PostSellRequest(mContext, data, planId);
                    } catch (Exception e) {
                        throw new RuntimeException("wrong :: " + e.getMessage());
                    }
                }
                break;
            case L3Contract.TRANSFER: {
                if (method == Method.POST) {
                    return new PostTransferRequest(mContext, data);
                }
                break;
            }
        }

        return null;
    }

    public static enum Method {
        GET, POST, PUT, DELETE
    }

}
