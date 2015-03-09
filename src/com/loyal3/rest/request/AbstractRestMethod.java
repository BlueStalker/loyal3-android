package com.loyal3.rest.request;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.loyal3.R;
import com.loyal3.model.L3Contract;
import com.loyal3.rest.*;
import com.loyal3.rest.resource.ResponseResource;


public abstract class AbstractRestMethod<T extends ResponseResource> implements RestMethod<T> {

    private static final String DEFAULT_ENCODING = "UTF-8";

    public String API_ENDPOINT;

    protected AbstractRestMethod(Context context) {
        API_ENDPOINT = context.getResources().getString(R.string.api_endpoint);
    }


    public RestMethodResult<T> execute() {

        Request request = buildRequest();
        GenericResponse response = doRequest(request);
        return buildResult(response);
    }

    protected abstract Context getContext();

    /**
     * Subclasses can overwrite for full control, eg. need to do special
     * inspection of response headers, etc.
     *
     * @param response
     * @return
     */
    protected RestMethodResult<T> buildResult(GenericResponse response) {

        int status = response.status;
        String statusMsg = "";
        String responseBody = null;
        T resource = null;
        if (status != 200) {
            Log.e("LOYAL3", "REST :: buildResult :: " + status + " " + this.getClass().getName());
            return new RestMethodResult<T>(status, statusMsg, resource);
        }
        List<String> cookie = response.headers.get("Set-Cookie");
        if (cookie != null && cookie.size() > 0) {
            SharedPreferences.Editor editor = this.getContext().getSharedPreferences("com.loyal3", Context.MODE_MULTI_PROCESS).edit();
            Set<String> cookies = new HashSet<String>();
            for (String value : cookie) cookies.add(value);
            editor.putStringSet(L3Contract.COOKIE, cookies);
            editor.apply();
        }
        try {
            responseBody = new String(response.body, getCharacterEncoding(response.headers));
            Log.d("LOYAL3", responseBody);
            resource = parseResponseBody(responseBody);
        } catch (Exception ex) {
//            Log.e("LOYAL3", ex.getMessage());
            ex.printStackTrace();
            status = 500; // spec only defines up to 505
            statusMsg = ex.getMessage();
        }
        return new RestMethodResult<T>(status, statusMsg, resource);
    }

    protected abstract Request buildRequest();

    protected abstract T parseResponseBody(String responseBody) throws Exception;

    private GenericResponse doRequest(Request request) {

        RestClient client = new RestClient();
        return client.execute(request);
    }

    private String getCharacterEncoding(Map<String, List<String>> headers) {
        // TODO get value from headers
        return DEFAULT_ENCODING;
    }

}
