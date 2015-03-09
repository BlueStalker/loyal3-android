package com.loyal3.service.processor;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.loyal3.model.Buy;
import com.loyal3.model.L3Contract;
import com.loyal3.rest.RestMethod;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.RestMethodResult;
import com.loyal3.rest.resource.BuyResults;
import com.loyal3.rest.resource.RequestResource;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/16/14
 * Time: 2:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class BuyProcessor {
    private Context mContext;
    private String offerId;

    public BuyProcessor(Context context, String offerId) {
        mContext = context;
        this.offerId = offerId;
    }

    public interface BuyCallback {
        public void send(int resultCode);
    }

    public void buy(BuyCallback callback, RequestResource data) {
        @SuppressWarnings("unchecked")
        RestMethod<BuyResults> request = RestMethodFactory.getInstance(mContext).getRestMethod(Uri.parse("content://" + L3Contract.AUTHORITY + "/" + Buy.TABLE_NAME + "/" + offerId), RestMethodFactory.Method.POST, null, data);
        RestMethodResult<BuyResults> result = request.execute();
        if (result == null) return;
        BuyResults buyResults = result.getResource();
        Log.d("LOYAL3", "buy " + buyResults.getResultCode() + "");
        if (buyResults.getResultCode() != null) {
            callback.send(buyResults.getResultCode());
        }
    }
}
