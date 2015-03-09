package com.loyal3.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import com.loyal3.model.L3Contract;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.resource.RequestResource;
import com.loyal3.service.processor.*;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 8/27/14
 */
public class L3Service extends IntentService {
    private ResultReceiver mCallback;

    private Intent mOriginalRequestIntent;

    public static final String METHOD_EXTRA = "com.loyal3.service.METHOD_EXTRA";

    public static final String SERVICE_CALLBACK = "com.loyal3.service.SERVICE_CALLBACK";

    public static final String RESOURCE_TYPE_EXTRA = "com.loyal3.service.RESOURCE_TYPE_EXTRA";

    public static final String ORIGINAL_INTENT_EXTRA = "com.loyal3.service.ORIGINAL_INTENT_EXTRA";

    public static final String DATA_EXTRA = "com.loyal3.service.DATA_EXTRA";

    public static final String URL_EXTRA = "com.loyal3.service.URL_EXTRA";

    public static final int INVALID = -1;
    @Override
    protected void onHandleIntent(Intent requestIntent) {
        mOriginalRequestIntent = requestIntent;
        RestMethodFactory.Method method = RestMethodFactory.Method.valueOf(requestIntent.getStringExtra(METHOD_EXTRA));
        int resourceType = requestIntent.getIntExtra(RESOURCE_TYPE_EXTRA, -1);
        mCallback = requestIntent.getParcelableExtra(SERVICE_CALLBACK);
        RequestResource data = (RequestResource) requestIntent.getSerializableExtra(DATA_EXTRA);
        Log.e("LOYAL3", "onHandleIntent begin :: " + resourceType);
        switch (resourceType) {
            case L3Contract.LOGIN:
                if (method.equals(RestMethodFactory.Method.POST)) {
                    LoginProcessor processor = new LoginProcessor(getApplicationContext());
                    processor.doLogin(new LoginProcessor.LoginCallback() {
                        @Override
                        public void send(int resultCode) {
                            genericCallback(resultCode);
                        }
                    }, data);
                } else {
                    mCallback.send(INVALID, getOriginalIntentBundle());
                }
                break;
            case L3Contract.OFFERS:
                if (method.equals(RestMethodFactory.Method.GET)) {
                    new Thread() {
                       public void run() {
                           OfferProcessor processor = new OfferProcessor(getApplicationContext());
                           processor.getOffers(new OfferProcessor.OffersCallback() {
                               @Override
                               public void send(int resultCode) {
                                   genericCallback(resultCode);
                               }
                           });
                       }
                    }.start();
                }
                break;
            case L3Contract.OFFER:
                break;

            case L3Contract.ACCOUNTS:
                if (method.equals(RestMethodFactory.Method.GET)) {
                    UserProcessor processor = new UserProcessor(getApplicationContext());
                    processor.processAccounts(new UserProcessor.UserCallback() {
                        @Override
                        public void send(int resultCode) {
                            genericCallback(resultCode);
                        }
                    });
                }
                break;
            case L3Contract.PLANS:
                if (method.equals(RestMethodFactory.Method.GET)) {
                    PlanProcessor processor = new PlanProcessor(getApplicationContext());
                    processor.getPlanSummaries(new PlanProcessor.PlansCallback() {
                        @Override
                        public void send(int resultCode) {
                            genericCallback(resultCode);
                        }
                    });
                }
                break;

            case L3Contract.TRANSACTIONS:
                if (method.equals(RestMethodFactory.Method.GET)) {
                    TransactionProcessor processor = new TransactionProcessor(getApplicationContext());
                    processor.getTransactionSummaries(new TransactionProcessor.TransactionCallback() {
                        @Override
                        public void send(int resultCode) {
                            genericCallback(resultCode);
                        }
                    });
                }
                break;

            case L3Contract.PAYMENTS:
                if (method.equals(RestMethodFactory.Method.GET)) {
                    PaymentProcessor processor = new PaymentProcessor(getApplicationContext());
                    processor.getPaymentSummaries(new PaymentProcessor.PaymentCallback() {
                        @Override
                        public void send(int resultCode) {
                            genericCallback(resultCode);
                        }
                    });
                }
                break;
            case L3Contract.BUY:
                if (method.equals(RestMethodFactory.Method.POST)) {
                    String offerId = requestIntent.getStringExtra(URL_EXTRA);
                    BuyProcessor processor = new BuyProcessor(getApplicationContext(), offerId);
                    processor.buy(new BuyProcessor.BuyCallback() {
                        @Override
                        public void send(int resultCode) {
                            genericCallback(resultCode);
                        }
                    }, data);
                } else {
                    mCallback.send(INVALID, getOriginalIntentBundle());
                }
                break;

            case L3Contract.SELL:
                if (method.equals(RestMethodFactory.Method.POST)) {
                    String planId = requestIntent.getStringExtra(URL_EXTRA);
                    SellProcessor processor = new SellProcessor(getApplicationContext(), planId);
                    processor.sell(new SellProcessor.SellCallback() {
                        @Override
                        public void send(int resultCode) {
                            genericCallback(resultCode);
                        }
                    }, data);
                } else {
                    mCallback.send(INVALID, getOriginalIntentBundle());
                }
                break;
            case L3Contract.TRANSFER:
                if (method.equals(RestMethodFactory.Method.POST)) {
                    TransferProcessor processor = new TransferProcessor(getApplicationContext());
                    processor.sell(new TransferProcessor.TransferCallback() {
                        @Override
                        public void send(int resultCode) {
                            genericCallback(resultCode);
                        }
                    }, data);
                } else {
                    mCallback.send(INVALID, getOriginalIntentBundle());
                }
                break;
            case L3Contract.LOGOUT:
                if (method.equals(RestMethodFactory.Method.GET)) {
                    LogoutProcessor processor = new LogoutProcessor(getApplicationContext());
                    processor.doLogout(new LogoutProcessor.LogoutCallback() {
                        @Override
                        public void send(int resultCode) {
                            genericCallback(resultCode);
                        }
                    });
                }
                break;
            default: break;
        }
    }

    private void genericCallback(int resultCode) {
        if (mCallback != null) {
            mCallback.send(resultCode, getOriginalIntentBundle());
        }
    }

    public L3Service() {
        super("L3Service");
    }

    protected Bundle getOriginalIntentBundle() {
        Bundle originalRequest = new Bundle();
        originalRequest.putParcelable(ORIGINAL_INTENT_EXTRA, mOriginalRequestIntent);
        return originalRequest;
    }
}
