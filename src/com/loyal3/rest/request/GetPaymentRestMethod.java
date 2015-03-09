package com.loyal3.rest.request;

import android.content.Context;
import com.loyal3.model.L3Contract;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.resource.PaymentResults;
import org.json.JSONObject;

import java.net.URI;
import java.util.*;

public class GetPaymentRestMethod extends AbstractRestMethod<PaymentResults> {
    private Context mContext;

    private final URI PAYMENTS_URL = URI.create(API_ENDPOINT + "payment_methods");

    public GetPaymentRestMethod(Context context) {
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
        return new L3GetRequest(RestMethodFactory.Method.GET, PAYMENTS_URL, headers, mContext);
    }

    @Override
    protected PaymentResults parseResponseBody(String responseBody) throws Exception {

        JSONObject json = new JSONObject(responseBody);

        return new PaymentResults(json);

    }

    @Override
    protected Context getContext() {
        return mContext;
    }
}
