package com.loyal3.rest.request;


import android.content.Context;
import com.loyal3.model.L3Contract;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.resource.RequestResource;
import com.loyal3.rest.resource.SellResults;
import com.loyal3.rest.resource.TransferResults;
import org.json.JSONObject;

import java.net.URI;
import java.util.*;

public class PostTransferRequest extends AbstractRestMethod<TransferResults> {
    private Context mContext;

    private RequestResource requestResource;

    private URI TRANSFER_URL = URI.create(API_ENDPOINT + "transfer");

    public PostTransferRequest(Context context, RequestResource request) {
        super(context);
        mContext = context;
        this.requestResource = request;
    }

    @Override
    protected Request buildRequest() {
        List<String> cookie = new ArrayList<String>();
        for (String value : mContext.getSharedPreferences("com.loyal3", Context.MODE_PRIVATE).getStringSet(L3Contract.COOKIE, new HashSet<String>()))
            cookie.add(value);
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put(L3Contract.COOKIE, cookie);
        return new L3PostRequest(RestMethodFactory.Method.POST, TRANSFER_URL, headers, requestResource.getRequestBody(), mContext);
    }

    @Override
    protected TransferResults parseResponseBody(String responseBody) throws Exception {

        JSONObject json = new JSONObject(responseBody);
        return new TransferResults(json);
    }

    @Override
    protected Context getContext() {
        return mContext;
    }
}
