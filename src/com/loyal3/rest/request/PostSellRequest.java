package com.loyal3.rest.request;

import android.content.Context;
import com.loyal3.model.L3Contract;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.resource.BuyResults;
import com.loyal3.rest.resource.RequestResource;
import com.loyal3.rest.resource.SellResults;
import org.json.JSONObject;

import java.net.URI;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/18/14
 * Time: 4:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class PostSellRequest extends AbstractRestMethod<SellResults> {
    private Context mContext;

    private RequestResource requestResource;

    private String planId;
    private URI SELL_URL;

    public PostSellRequest(Context context, RequestResource request, String planId) {
        super(context);
        mContext = context;
        this.planId = planId;
        this.requestResource = request;
        this.SELL_URL = URI.create(API_ENDPOINT + "plans/" + planId + "/sell");
    }

    @Override
    protected Request buildRequest() {
        List<String> cookie = new ArrayList<String>();
        for (String value : mContext.getSharedPreferences("com.loyal3", Context.MODE_PRIVATE).getStringSet(L3Contract.COOKIE, new HashSet<String>()))
            cookie.add(value);
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put(L3Contract.COOKIE, cookie);
        return new L3PostRequest(RestMethodFactory.Method.POST, SELL_URL, headers, requestResource.getRequestBody(), mContext);
    }

    @Override
    protected SellResults parseResponseBody(String responseBody) throws Exception {

        JSONObject json = new JSONObject(responseBody);
        return new SellResults(json);
    }

    @Override
    protected Context getContext() {
        return mContext;
    }
}
