package com.loyal3.service.processor;

import android.content.ContentValues;
import android.content.Context;
import com.loyal3.model.Payment;
import com.loyal3.model.Transaction;
import com.loyal3.rest.RestMethod;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.RestMethodResult;
import com.loyal3.rest.request.L3RestCode;
import com.loyal3.rest.resource.PaymentResults;

import java.util.List;

public class PaymentProcessor {
    private Context mContext;

    public PaymentProcessor(Context context) {
        mContext = context;
    }

    public interface PaymentCallback {
        public void send(int resultCode);
    }

    public void getPaymentSummaries(PaymentCallback callback) {
        @SuppressWarnings("unchecked")
        RestMethod<PaymentResults> paymentRequest = RestMethodFactory.getInstance(mContext).getRestMethod(Payment.CONTENT_URI, RestMethodFactory.Method.GET, null, null);
        RestMethodResult<PaymentResults> result = paymentRequest.execute();
        if (result == null) return;
        PaymentResults paymentResults = result.getResource();
        if (result.getStatusCode() == L3RestCode.OK) {
            mContext.getContentResolver().delete(Transaction.CONTENT_URI, null, null);

            // Get the list of offer summaries
            List<PaymentResults.PaymentSummary> payments = paymentResults.getPayments();
            ContentValues[] allValues = new ContentValues[payments.size()];
            for(int i = 0; i < payments.size(); i++) {
                PaymentResults.PaymentSummary paymentSummary = payments.get(i);
                ContentValues values = new ContentValues();
                values.put(Payment.PAYMENT_ID, paymentSummary.getPaymentMethodId());
                values.put(Payment.ACCOUNT_ID, paymentSummary.getAccountId());
                if (paymentSummary.getAccountNumber() != null ) values.put(Payment.ACCOUNT_NUMBER, paymentSummary.getAccountNumber());
                if (paymentSummary.getFirstName() != null ) values.put(Payment.FIRST_NAME, paymentSummary.getFirstName());
                if (paymentSummary.getLastName() != null ) values.put(Payment.LAST_NAME, paymentSummary.getLastName());
                if (paymentSummary.getRoutingNumber() != null ) values.put(Payment.ROUTING_NUMBER, paymentSummary.getRoutingNumber());
                if (paymentSummary.getType() != null ) values.put(Payment.PAYMENT_TYPE, paymentSummary.getType());
                if (paymentSummary.getAmount() != null ) values.put(Payment.AMOUNT, paymentSummary.getAmount());
                allValues[i] = values;
            }
            mContext.getContentResolver().delete(Payment.CONTENT_URI, null, null);
            mContext.getContentResolver().bulkInsert(Payment.CONTENT_URI, allValues);
            callback.send(result.getStatusCode());
        } else if (result.getStatusCode() == L3RestCode.NOT_AUTH) {
            callback.send(result.getStatusCode());
        }
    }
}
