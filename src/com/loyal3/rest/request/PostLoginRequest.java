package com.loyal3.rest.request;

import android.content.Context;
import com.loyal3.model.L3Contract;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.resource.LoginResults;
import com.loyal3.rest.resource.RequestResource;
import org.json.JSONObject;

import java.net.URI;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 */
public class PostLoginRequest extends AbstractRestMethod<LoginResults> {
    private Context mContext;

    private RequestResource requestResource;

    private final URI LOGIN_URL = URI.create(API_ENDPOINT + "login");

    public PostLoginRequest(Context context, RequestResource request) {
        super(context);
        mContext = context;
        this.requestResource = request;
    }

    @Override
    protected Request buildRequest() {
        /*List<String> cookie = new ArrayList<String>();
        for (String value : mContext.getSharedPreferences("com.loyal3", Context.MODE_MULTI_PROCESS).getStringSet(L3Contract.COOKIE, new HashSet<String>()))
            cookie.add(value);
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put(L3Contract.COOKIE, cookie);
          */
        return new L3PostRequest(RestMethodFactory.Method.POST, LOGIN_URL, new HashMap<String, List<String>>(), requestResource.getRequestBody(), mContext);
    }

    @Override
    protected LoginResults parseResponseBody(String responseBody) throws Exception {

        JSONObject json = new JSONObject(responseBody);
        return new LoginResults(json);
    }

    @Override
    protected Context getContext() {
        return mContext;
    }
}
