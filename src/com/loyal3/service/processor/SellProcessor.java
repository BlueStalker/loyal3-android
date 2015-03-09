package com.loyal3.service.processor;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.loyal3.model.Buy;
import com.loyal3.model.L3Contract;
import com.loyal3.model.Sell;
import com.loyal3.rest.RestMethod;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.RestMethodResult;
import com.loyal3.rest.request.L3RestCode;
import com.loyal3.rest.resource.BuyResults;
import com.loyal3.rest.resource.RequestResource;
import com.loyal3.rest.resource.SellResults;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/18/14
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class SellProcessor {
    private Context mContext;
    private String planId;

    public SellProcessor(Context context, String planId) {
        mContext = context;
        this.planId = planId;
    }

    public interface SellCallback {
        public void send(int resultCode);
    }

    public void sell(SellCallback callback, RequestResource data) {
        @SuppressWarnings("unchecked")
        RestMethod<SellResults> request = RestMethodFactory.getInstance(mContext).getRestMethod(Uri.parse("content://" + L3Contract.AUTHORITY + "/" + Sell.TABLE_NAME + "/" + planId), RestMethodFactory.Method.POST, null, data);
        RestMethodResult<SellResults> result = request.execute();
        if (result == null) return;
        SellResults sellResults = result.getResource();
        Log.d("LOYAL3", "sell " + sellResults.getResultCode() + "");
        if (sellResults.getResultCode() != null) {
            callback.send(sellResults.getResultCode());
        }
    }
}
