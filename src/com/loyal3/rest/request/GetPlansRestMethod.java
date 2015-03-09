package com.loyal3.rest.request;

import android.content.Context;
import com.loyal3.model.L3Contract;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.resource.PlansResults;
import org.json.JSONObject;

import java.net.URI;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/4/14
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetPlansRestMethod extends AbstractRestMethod<PlansResults> {
    private Context mContext;

    private final URI PLANS_URL = URI.create(API_ENDPOINT + "plans");

    public GetPlansRestMethod(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected Request buildRequest() {
        List<String> cookie = new ArrayList<String>();
        for (String value : mContext.getSharedPreferences("com.loyal3", Context.MODE_PRIVATE).getStringSet(L3Contract.COOKIE, new HashSet<String>()))
            cookie.add(value);
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put(L3Contract.COOKIE, cookie);
        return new L3GetRequest(RestMethodFactory.Method.GET, PLANS_URL, headers, mContext);
    }

    @Override
    protected PlansResults parseResponseBody(String responseBody) throws Exception {

        JSONObject json = new JSONObject(responseBody);

        return new PlansResults(json);

    }

    @Override
    protected Context getContext() {
        return mContext;
    }
}