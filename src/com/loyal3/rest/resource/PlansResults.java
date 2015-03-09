package com.loyal3.rest.resource;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/4/14
 * Time: 4:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlansResults implements ResponseResource {
    public class AipSummary {
        private Double amount;
        private Integer day;
        private String paymentMethodId;

        public Double getAmount() {
            return amount;
        }

        public Integer getDay() {
            return day;
        }

        public String getPaymentMethodId() {
            return paymentMethodId;
        }

        public AipSummary(JSONObject json) throws JSONException {
            amount = json.getDouble("monthly_investment_amount");
            day = json.getInt("monthly_investment_day");
            paymentMethodId = json.getString("payment_method_id");
        }
    }

    public class PlanSummary {
        private String planId;
        private String offerId;
        private String organizationId;
        private Double reservationAmount;
        private String reservationState;
        private Double sharesOwned;
        private Double sharePrice;
        private Double currentValue;
        private AipSummary aip;
        public PlanSummary(JSONObject json) throws JSONException {
            this.planId = json.getString("plan_id");
            this.offerId = json.getString("offer_id");
            this.organizationId = json.getString("organization_id");
            if (!json.isNull("ipo_reservation_amount")) this.reservationAmount = json.getDouble("ipo_reservation_amount");
            if (!json.isNull("ipo_reservation_state"))  this.reservationState = json.getString("ipo_reservation_state");
            if (!json.isNull("current_share_price")) this.sharePrice = json.getDouble("current_share_price");
            if (!json.isNull("shares_owned"))  this.sharesOwned = json.getDouble("shares_owned");
            if (!json.isNull("current_value")) this.currentValue = json.getDouble("current_value");
            if (!json.isNull("auto_investment_plan")) this.aip = new AipSummary(json.getJSONObject("auto_investment_plan"));
        }

        public AipSummary getAip() {
            return aip;
        }

        public String getPlanId() {
            return planId;
        }

        public String getOfferId() {
            return offerId;
        }

        public String getOrganizationId() {
            return organizationId;
        }

        public Double getReservationAmount() {
            return reservationAmount;
        }

        public String getReservationState() {
            return reservationState;
        }

        public Double getSharesOwned() {
            return sharesOwned;
        }

        public Double getSharePrice() {
            return sharePrice;
        }

        public Double getCurrentValue() {
            return currentValue;
        }
    }

    private List<PlanSummary> plans;

    public PlansResults(JSONObject json) throws JSONException {
        JSONArray jsonArray =  json.getJSONArray("plans");
        plans = new ArrayList<PlanSummary>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = (JSONObject)jsonArray.get(i);
            PlanSummary summary = new PlanSummary(obj);
            plans.add(summary);
        }
    }

    public List<PlanSummary> getPlans() {
        return plans;
    }

}
