package com.loyal3.service.processor;


import android.content.Context;
import android.util.Log;
import com.loyal3.model.Transfer;
import com.loyal3.rest.RestMethod;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.RestMethodResult;
import com.loyal3.rest.request.L3RestCode;
import com.loyal3.rest.resource.RequestResource;
import com.loyal3.rest.resource.TransferResults;

public class TransferProcessor {
    private Context mContext;

    public TransferProcessor(Context context) {
        mContext = context;
    }

    public interface TransferCallback {
        public void send(int resultCode);
    }

    public void sell(TransferCallback callback, RequestResource data) {
        @SuppressWarnings("unchecked")
        RestMethod<TransferResults> request = RestMethodFactory.getInstance(mContext).getRestMethod(Transfer.CONTENT_URI, RestMethodFactory.Method.POST, null, data);
        RestMethodResult<TransferResults> result = request.execute();
        if (result == null) return;

        TransferResults transferResults = result.getResource();
        Log.d("LOYAL3", "transfer " + transferResults.getResultCode() + "");
        if (transferResults.getResultCode() != null) {
            callback.send(transferResults.getResultCode());
        }
    }
}
