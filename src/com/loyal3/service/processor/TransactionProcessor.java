package com.loyal3.service.processor;

import android.content.ContentValues;
import android.content.Context;
import com.loyal3.model.Plan;
import com.loyal3.model.Transaction;
import com.loyal3.rest.RestMethod;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.RestMethodResult;
import com.loyal3.rest.request.L3RestCode;
import com.loyal3.rest.resource.TransactionResults;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/8/14
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionProcessor {
    private Context mContext;

    public TransactionProcessor(Context context) {
        mContext = context;
    }

    public interface TransactionCallback {
        public void send(int resultCode);
    }

    public void getTransactionSummaries(TransactionCallback callback) {
        @SuppressWarnings("unchecked")
        RestMethod<TransactionResults> transactionRequest = RestMethodFactory.getInstance(mContext).getRestMethod(Transaction.CONTENT_URI, RestMethodFactory.Method.GET, null, null);
        RestMethodResult<TransactionResults> result = transactionRequest.execute();
        if (result == null) return;

        TransactionResults transactionResults = result.getResource();
        if (result.getStatusCode() == L3RestCode.OK) {
            mContext.getContentResolver().delete(Transaction.CONTENT_URI, null, null);

            // Get the list of offer summaries
            List<TransactionResults.TransactionSummary> transactions = transactionResults.getTransactions();
            ContentValues[] allValues = new ContentValues[transactions.size()];
            for (int i = 0; i < transactions.size(); i++) {
                TransactionResults.TransactionSummary transactionSummary = transactions.get(i);
                ContentValues values = new ContentValues();
                values.put(Transaction.PLAN_ID, transactionSummary.getPlanId());
                if (transactionSummary.getOfferId() != null)
                    values.put(Transaction.OFFER_ID, transactionSummary.getOfferId());
                if (transactionSummary.getAmount() != null)
                    values.put(Transaction.AMOUNT, transactionSummary.getAmount());
                if (transactionSummary.getPrice() != null) values.put(Transaction.PRICE, transactionSummary.getPrice());
                values.put(Transaction.DESCRIPTION, transactionSummary.getDescription());
                if (transactionSummary.getShares() != null)
                    values.put(Transaction.SHARES, transactionSummary.getShares());
                values.put(Transaction.STATUS, transactionSummary.getStatus());
                values.put(Transaction.DATE, transactionSummary.getDate());

                allValues[i] = values;
            }
            mContext.getContentResolver().delete(Transaction.CONTENT_URI, null, null);
            mContext.getContentResolver().bulkInsert(Transaction.CONTENT_URI, allValues);
            callback.send(result.getStatusCode());
        } else if (result.getStatusCode() == L3RestCode.NOT_AUTH) {
            callback.send(result.getStatusCode());
        }
    }
}
