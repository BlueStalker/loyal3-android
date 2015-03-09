package com.loyal3.rest.request;

import android.content.Context;
import com.loyal3.R;
import com.loyal3.rest.RestMethodFactory;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 */
public class L3PostRequest extends Request {
    private Context context;

    public L3PostRequest(RestMethodFactory.Method method, URI requestUri, Map<String, List<String>> headers, JSONObject body, Context context) {
        super(method, requestUri, headers, body.toString().getBytes());
        this.context = context;
        List<String> contentType = new ArrayList<String>();
        contentType.add("application/json");
        List<String> apiKeys = new ArrayList<String>();
        apiKeys.add(context.getResources().getString(R.string.api_key));
        addHeader("Content-Type", contentType);
        addHeader("API_KEY", apiKeys);
    }

}
