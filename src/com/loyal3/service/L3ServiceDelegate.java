package com.loyal3.service;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.util.Log;
import com.loyal3.model.*;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.resource.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created with IntelliJ IDEA.
 * User: curt
 */
public class L3ServiceDelegate {
    private static Object lock = new Object();

    private static L3ServiceDelegate instance;

    private static final String REQUEST_ID = "REQUEST_ID";
    private Map<String,Long> pendingRequests = new HashMap<String,Long>();
    private Context ctx;

    private L3ServiceDelegate(Context ctx){
        this.ctx = ctx;
    }

    public static L3ServiceDelegate getInstance(Context ctx){
        synchronized (lock) {
            if(instance == null){
                instance = new L3ServiceDelegate(ctx);
            }
        }
        return instance;
    }

    private long generateRequestID() {
        long requestId = UUID.randomUUID().getLeastSignificantBits();
        return requestId;
    }

    public boolean isRequestPending(long requestId){
        return this.pendingRequests.containsValue(requestId);
    }

    private long checkingPendingRequest(String key) {
        if(pendingRequests.containsKey(key)){
            return pendingRequests.get(key);
        }

        long requestId = generateRequestID();
        pendingRequests.put(key, requestId);
        return requestId;
    }

    public long postLogin(String username, String password, String apiKey, ResultReceiver serviceCallback) {
        RequestResource loginRequest = new LoginRequest(username, password, apiKey);
        long requestId = checkingPendingRequest(Login.TABLE_NAME);

        Intent intent = new Intent(this.ctx, L3Service.class);
        intent.putExtra(L3Service.METHOD_EXTRA, RestMethodFactory.Method.POST.toString());
        intent.putExtra(L3Service.RESOURCE_TYPE_EXTRA, L3Contract.LOGIN);
        intent.putExtra(L3Service.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(L3Service.DATA_EXTRA, loginRequest);
        intent.putExtra(REQUEST_ID, requestId);
        this.ctx.startService(intent);

        return requestId;
    }

    public long getOfferSummaries(ResultReceiver serviceCallback) {
        long requestId = checkingPendingRequest(Offer.TABLE_NAME);
        Log.e("LOYAL3", "service delegate getOfferSummaries");
        Intent intent = new Intent(this.ctx, L3Service.class);
        intent.putExtra(L3Service.METHOD_EXTRA, RestMethodFactory.Method.GET.toString());
        intent.putExtra(L3Service.RESOURCE_TYPE_EXTRA, L3Contract.OFFERS);
        intent.putExtra(L3Service.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(REQUEST_ID, requestId);
        this.ctx.startService(intent);

        return requestId;
    }

    public long getPlanSummaries(ResultReceiver serviceCallback) {
        long requestId = checkingPendingRequest(Plan.TABLE_NAME);

        Intent intent = new Intent(this.ctx, L3Service.class);
        intent.putExtra(L3Service.METHOD_EXTRA, RestMethodFactory.Method.GET.toString());
        intent.putExtra(L3Service.RESOURCE_TYPE_EXTRA, L3Contract.PLANS);
        intent.putExtra(L3Service.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(REQUEST_ID, requestId);
        this.ctx.startService(intent);

        return requestId;
    }

    public long getTransactionSummaries(ResultReceiver serviceCallback) {
        long requestId = checkingPendingRequest(Transaction.TABLE_NAME);

        Intent intent = new Intent(this.ctx, L3Service.class);
        intent.putExtra(L3Service.METHOD_EXTRA, RestMethodFactory.Method.GET.toString());
        intent.putExtra(L3Service.RESOURCE_TYPE_EXTRA, L3Contract.TRANSACTIONS);
        intent.putExtra(L3Service.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(REQUEST_ID, requestId);
        this.ctx.startService(intent);

        return requestId;
    }

    public long getAccounts(ResultReceiver serviceCallback) {
        long requestId = checkingPendingRequest(Account.TABLE_NAME);

        Intent intent = new Intent(this.ctx, L3Service.class);
        intent.putExtra(L3Service.METHOD_EXTRA, RestMethodFactory.Method.GET.toString());
        intent.putExtra(L3Service.RESOURCE_TYPE_EXTRA, L3Contract.ACCOUNTS);
        intent.putExtra(L3Service.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(REQUEST_ID, requestId);
        this.ctx.startService(intent);

        return requestId;
    }

    public long getPayments(ResultReceiver serviceCallback) {
        long requestId = checkingPendingRequest(Account.TABLE_NAME);

        Intent intent = new Intent(this.ctx, L3Service.class);
        intent.putExtra(L3Service.METHOD_EXTRA, RestMethodFactory.Method.GET.toString());
        intent.putExtra(L3Service.RESOURCE_TYPE_EXTRA, L3Contract.PAYMENTS);
        intent.putExtra(L3Service.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(REQUEST_ID, requestId);
        this.ctx.startService(intent);

        return requestId;
    }

    public long doLogout(ResultReceiver serviceCallback) {
        long requestId = checkingPendingRequest(Logout.TABLE_NAME);
        Log.e("LOYAL3", "service delegate doLogout");
        Intent intent = new Intent(this.ctx, L3Service.class);
        intent.putExtra(L3Service.METHOD_EXTRA, RestMethodFactory.Method.GET.toString());
        intent.putExtra(L3Service.RESOURCE_TYPE_EXTRA, L3Contract.LOGOUT);
        intent.putExtra(L3Service.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(REQUEST_ID, requestId);
        this.ctx.startService(intent);

        return requestId;
    }

    public long buy(Double amount, List<BuyRequest.FundSource> sources, String offerId, BuyRequest.BuyType buyType, BuyRequest.PaymentSchedule schedule, ResultReceiver serviceCallback) {
        RequestResource buyRequest = new BuyRequest(amount, sources, buyType, schedule);
        long requestId = checkingPendingRequest(Buy.TABLE_NAME);

        Intent intent = new Intent(this.ctx, L3Service.class);
        intent.putExtra(L3Service.METHOD_EXTRA, RestMethodFactory.Method.POST.toString());
        intent.putExtra(L3Service.URL_EXTRA, offerId);
        intent.putExtra(L3Service.RESOURCE_TYPE_EXTRA, L3Contract.BUY);
        intent.putExtra(L3Service.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(L3Service.DATA_EXTRA, buyRequest);
        intent.putExtra(REQUEST_ID, requestId);
        this.ctx.startService(intent);

        return requestId;
    }

    public long sell(String planId, Double shares, ResultReceiver serviceCallback)  {
        RequestResource sellRequest = new SellRequest(shares);
        long requestId = checkingPendingRequest(Sell.TABLE_NAME);

        Intent intent = new Intent(this.ctx, L3Service.class);
        intent.putExtra(L3Service.METHOD_EXTRA, RestMethodFactory.Method.POST.toString());
        intent.putExtra(L3Service.URL_EXTRA, planId);
        intent.putExtra(L3Service.RESOURCE_TYPE_EXTRA, L3Contract.SELL);
        intent.putExtra(L3Service.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(L3Service.DATA_EXTRA, sellRequest);
        intent.putExtra(REQUEST_ID, requestId);
        this.ctx.startService(intent);

        return requestId;
    }

    public long transferFunds(Double amount, String from, String to, ResultReceiver serviceCallback)  {
        RequestResource transferRequest = new TransferRequest(amount, from , to);
        long requestId = checkingPendingRequest(Transfer.TABLE_NAME);

        Intent intent = new Intent(this.ctx, L3Service.class);
        intent.putExtra(L3Service.METHOD_EXTRA, RestMethodFactory.Method.POST.toString());
        intent.putExtra(L3Service.RESOURCE_TYPE_EXTRA, L3Contract.TRANSFER);
        intent.putExtra(L3Service.SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(L3Service.DATA_EXTRA, transferRequest);
        intent.putExtra(REQUEST_ID, requestId);
        this.ctx.startService(intent);

        return requestId;
    }
}
