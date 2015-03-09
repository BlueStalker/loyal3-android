package com.loyal3.rest.request;

import android.content.Context;
import com.loyal3.model.L3Contract;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.resource.BuyResults;
import com.loyal3.rest.resource.LoginResults;
import com.loyal3.rest.resource.RequestResource;
import org.json.JSONObject;

import java.net.URI;
import java.util.*;


public class PostBuyRequest extends AbstractRestMethod<BuyResults> {
    private Context mContext;

    private RequestResource requestResource;

    private String offerId;
    private URI BUY_URL;

    public PostBuyRequest(Context context, RequestResource request, String offerId) {
        super(context);
        mContext = context;
        this.offerId = offerId;
        this.requestResource = request;
        this.BUY_URL = URI.create(API_ENDPOINT + "offers/" + offerId + "/buy");
    }

    @Override
    protected Request buildRequest() {
        List<String> cookie = new ArrayList<String>();
        for (String value : mContext.getSharedPreferences("com.loyal3", Context.MODE_PRIVATE).getStringSet(L3Contract.COOKIE, new HashSet<String>()))
            cookie.add(value);
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put(L3Contract.COOKIE, cookie);
        return new L3PostRequest(RestMethodFactory.Method.POST, BUY_URL, headers, requestResource.getRequestBody(), mContext);
    }

    @Override
    protected BuyResults parseResponseBody(String responseBody) throws Exception {

        JSONObject json = new JSONObject(responseBody);
        return new BuyResults(json);
    }

    @Override
    protected Context getContext() {
        return mContext;
    }
}
