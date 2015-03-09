package com.loyal3.service.processor;

import android.content.Context;
import android.util.Log;
import com.loyal3.model.Login;
import com.loyal3.rest.RestMethod;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.RestMethodResult;
import com.loyal3.rest.resource.LoginResults;
import com.loyal3.rest.resource.RequestResource;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 8/27/14
 */
public class LoginProcessor {
    private Context mContext;

    public LoginProcessor(Context context) {
        mContext = context;
    }

    public interface LoginCallback {
        public void send(int resultCode);
    }

    public void doLogin(LoginCallback callback, RequestResource data) {
        @SuppressWarnings("unchecked")
        RestMethod<LoginResults> postLoginRequest = RestMethodFactory.getInstance(mContext).getRestMethod(Login.CONTENT_URI, RestMethodFactory.Method.POST, null, data);
        RestMethodResult<LoginResults> result = postLoginRequest.execute();

        LoginResults loginResults = result.getResource();
        if (result != null && loginResults.getResultCode().equals("0")) {
            Log.i("LOYAL3", loginResults.getResultCode());
            Log.i("LOYAL3", loginResults.getServerTime());
            callback.send(result.getStatusCode());
        }
    }
}
