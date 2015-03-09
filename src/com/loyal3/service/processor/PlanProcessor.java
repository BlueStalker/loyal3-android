package com.loyal3.service.processor;

import android.content.ContentValues;
import android.content.Context;
import com.loyal3.model.Aip;
import com.loyal3.model.Plan;
import com.loyal3.rest.RestMethod;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.RestMethodResult;
import com.loyal3.rest.request.L3RestCode;
import com.loyal3.rest.resource.PlansResults;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 8/27/14
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlanProcessor {
    private Context mContext;

    public PlanProcessor(Context context) {
        mContext = context;
    }

    public interface PlansCallback {
        public void send(int resultCode);
    }

    public void getPlanSummaries(PlansCallback callback) {
        @SuppressWarnings("unchecked")
        RestMethod<PlansResults> planSummaryRequest = RestMethodFactory.getInstance(mContext).getRestMethod(Plan.CONTENT_URI, RestMethodFactory.Method.GET, null, null);
        RestMethodResult<PlansResults> result = planSummaryRequest.execute();

        if (result == null) return;
        PlansResults plansResults = result.getResource();
        Map<String, ContentValues> aips = new HashMap<String, ContentValues>();
        if (result.getStatusCode() == L3RestCode.OK) {
            // Get the list of offer summaries
            List<PlansResults.PlanSummary> plans = plansResults.getPlans();
            ContentValues[] allValues = new ContentValues[plans.size()];
            for(int i = 0; i < plans.size(); i++) {
                PlansResults.PlanSummary planSummary = plans.get(i);
                ContentValues values = new ContentValues();
                values.put(Plan.PLAN_ID, planSummary.getPlanId());
                values.put(Plan.OFFER_ID, planSummary.getOfferId());
                values.put(Plan.ORGANIZATION_ID, planSummary.getOrganizationId());
                if (planSummary.getReservationAmount() != null) values.put(Plan.IPO_RESERVATION_AMOUNT, planSummary.getReservationAmount());
                if (planSummary.getReservationState() != null) values.put(Plan.IPO_RESERVATION_STATE, planSummary.getReservationState());
                if (planSummary.getSharesOwned() != null) values.put(Plan.SHARES_OWNED, planSummary.getSharesOwned());
                if (planSummary.getSharePrice() != null) values.put(Plan.SHARES_PRICE, planSummary.getSharePrice());
                if (planSummary.getCurrentValue() != null) values.put(Plan.CURRENT_VALUE, planSummary.getCurrentValue());

                if (planSummary.getAip() != null) {
                    ContentValues aip = new ContentValues();
                    aip.put(Aip.AMOUNT, planSummary.getAip().getAmount());
                    aip.put(Aip.DAY, planSummary.getAip().getDay());
                    aip.put(Aip.PAYMENT_METHOD_ID, planSummary.getAip().getPaymentMethodId());
                    aip.put(Aip.OFFER_ID, planSummary.getOfferId());
                    aips.put(planSummary.getOfferId(), aip);
                }
                allValues[i] = values;
            }
            mContext.getContentResolver().delete(Plan.CONTENT_URI, null, null);
            mContext.getContentResolver().bulkInsert(Plan.CONTENT_URI, allValues);

            mContext.getContentResolver().delete(Aip.CONTENT_URI, null, null);
            if (!aips.isEmpty()) mContext.getContentResolver().bulkInsert(Aip.CONTENT_URI, aips.values().toArray(new ContentValues[0]));
            callback.send(result.getStatusCode());
        } else if (result.getStatusCode() == L3RestCode.NOT_AUTH) {
            callback.send(result.getStatusCode());
        }
    }
}
