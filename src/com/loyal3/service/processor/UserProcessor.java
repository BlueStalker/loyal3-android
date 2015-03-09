package com.loyal3.service.processor;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import com.loyal3.model.Account;
import com.loyal3.model.L3Contract;
import com.loyal3.rest.RestMethod;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.RestMethodResult;
import com.loyal3.rest.request.L3RestCode;
import com.loyal3.rest.resource.AccountResults;

public class UserProcessor {
    private Context mContext;

    public UserProcessor(Context context) {
        mContext = context;
    }

    public interface UserCallback {
        public void send(int resultCode);
    }

    public void processAccounts(UserCallback callback) {
        @SuppressWarnings("unchecked")
        RestMethod<AccountResults> accountRequest = RestMethodFactory.getInstance(mContext).getRestMethod(Account.CONTENT_URI, RestMethodFactory.Method.GET, null, null);
        RestMethodResult<AccountResults> result = accountRequest.execute();
        if (result == null) return;
        AccountResults accountResults = result.getResource();

        if (result.getStatusCode() == L3RestCode.OK) {
            ContentValues values = new ContentValues();
            values.put(Account.ACCOUNT_ID, accountResults.getAccountId());
            values.put(Account.ACCOUNT_STATUS, accountResults.getStatus());
            values.put(Account.TOTAL_VALUE, accountResults.getTotalValue());
            values.put(Account.CASH_VALUE, accountResults.getCashValue());
            mContext.getContentResolver().insert(Account.CONTENT_URI, values);
            callback.send(result.getStatusCode());
        } else if (result.getStatusCode() == L3RestCode.NOT_AUTH) {
            callback.send(result.getStatusCode());
        }

        // Update the current user Id.
        if (accountResults != null) {
            SharedPreferences.Editor editor = mContext.getSharedPreferences("com.loyal3", Context.MODE_MULTI_PROCESS).edit();
            editor.putString(L3Contract.APPUSER, accountResults.getAccountId());
            editor.apply();
        }
    }
}
