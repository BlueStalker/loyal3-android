package com.loyal3.rest.request;

import android.content.Context;
import com.loyal3.model.L3Contract;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.resource.PlansResults;
import com.loyal3.rest.resource.TransactionResults;
import org.json.JSONObject;

import java.net.URI;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/8/14
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetTransactionRestMethod extends AbstractRestMethod<TransactionResults> {
    private Context mContext;

    private final URI TRANSACTION_URL = URI.create(API_ENDPOINT + "transactions");

    public GetTransactionRestMethod(Context context) {
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
        return new L3GetRequest(RestMethodFactory.Method.GET, TRANSACTION_URL, headers, mContext);
    }

    @Override
    protected TransactionResults parseResponseBody(String responseBody) throws Exception {

        JSONObject json = new JSONObject(responseBody);

        return new TransactionResults(json);

    }

    @Override
    protected Context getContext() {
        return mContext;
    }
}