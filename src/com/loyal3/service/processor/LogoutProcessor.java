package com.loyal3.service.processor;

import android.content.Context;
import android.util.Log;
import com.loyal3.model.*;
import com.loyal3.rest.RestMethod;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.RestMethodResult;
import com.loyal3.rest.request.L3RestCode;
import com.loyal3.rest.resource.LoginResults;
import com.loyal3.rest.resource.LogoutResults;
import com.loyal3.rest.resource.RequestResource;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/9/14
 * Time: 10:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class LogoutProcessor {
    private Context mContext;

    public LogoutProcessor(Context context) {
        mContext = context;
    }

    public interface LogoutCallback {
        public void send(int resultCode);
    }

    public void doLogout(LogoutCallback callback) {

        @SuppressWarnings("unchecked")
        RestMethod<LogoutResults> postLoginRequest = RestMethodFactory.getInstance(mContext).getRestMethod(Logout.CONTENT_URI, RestMethodFactory.Method.GET, null, null);
        RestMethodResult<LogoutResults> result = postLoginRequest.execute();
        if (result == null) return;

        if (result.getStatusCode() == L3RestCode.OK) {
            mContext.getContentResolver().delete(Account.CONTENT_URI, null, null);
            mContext.getContentResolver().delete(Offer.CONTENT_URI, null, null);
            mContext.getContentResolver().delete(Plan.CONTENT_URI, null, null);
            mContext.getContentResolver().delete(Transaction.CONTENT_URI, null, null);
            callback.send(result.getStatusCode());
        } else if (result.getStatusCode() == L3RestCode.NOT_AUTH) {
            callback.send(result.getStatusCode());
        }
    }
}
