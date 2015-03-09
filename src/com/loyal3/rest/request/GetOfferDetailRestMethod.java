package com.loyal3.rest.request;

import android.content.Context;
import com.loyal3.model.L3Contract;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.resource.OfferDetailResult;
import com.loyal3.rest.resource.OffersResults;
import org.json.JSONObject;

import java.net.URI;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/11/14
 * Time: 9:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetOfferDetailRestMethod extends AbstractRestMethod<OfferDetailResult> {
    private Context mContext;

    private URI OFFER_URL;

    public GetOfferDetailRestMethod(Context context, String offer_id) {
        super(context);
        OFFER_URL = URI.create(API_ENDPOINT + "offers" + "/" + offer_id);
        mContext = context;
    }

    @Override
    protected Request buildRequest() {
        List<String> cookie = new ArrayList<String>();
        for (String value : mContext.getSharedPreferences("com.loyal3", Context.MODE_PRIVATE).getStringSet(L3Contract.COOKIE, new HashSet<String>()))
            cookie.add(value);
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put(L3Contract.COOKIE, cookie);
        return new L3GetRequest(RestMethodFactory.Method.GET, OFFER_URL, headers, mContext);
    }

    @Override
    protected OfferDetailResult parseResponseBody(String responseBody) throws Exception {

        JSONObject json = new JSONObject(responseBody);

        return new OfferDetailResult(json);

    }

    @Override
    protected Context getContext() {
        return mContext;
    }
}
