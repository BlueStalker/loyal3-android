package com.loyal3.rest.resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class BuyRequest implements RequestResource {
    private Double amount;
    private List<FundSource> sources;
    private BuyType buyType;
    private PaymentSchedule schedule;

    public static enum BuyType {
        ONETIME("ONETIME"), MONTHLY("MONTHLY");
        public final String type;
        BuyType(String type) {
            this.type = type;
        }
    }

    public static class PaymentSchedule implements RequestResource {
        private Double amount;
        private Integer day;

        public Double getAmount() {
            return amount;
        }

        public Integer getDay() {
            return day;
        }

        public PaymentSchedule(Double amount, Integer day) {
            this.amount = amount;
            this.day = day;
        }

        @Override
        public JSONObject getRequestBody() {
            JSONObject schedule = new JSONObject();
            try {
                schedule.put("monthly_investment_amount", amount);
                schedule.put("monthly_investment_day", day);
                return schedule;
            } catch (JSONException e) {
                return null;
            }
        }
    }

    public static class FundSource implements RequestResource {
        private Double amount;
        private String paymentMethodId;

        public Double getAmount() {
            return amount;
        }

        public String getPaymentMethodId() {
            return paymentMethodId;
        }

        public FundSource(Double amount, String payment_method_id) {
            this.amount = amount;
            this.paymentMethodId = payment_method_id;
        }

        @Override
        public JSONObject getRequestBody() {
            JSONObject fundSource = new JSONObject();
            try {
                fundSource.put("amount", amount);
                fundSource.put("payment_method_id", paymentMethodId);
                return fundSource;
            } catch (JSONException e) {
                return null;
            }
        }
    }
    public BuyRequest(Double amount, List<FundSource> sources, BuyType buyType, PaymentSchedule schedule) {
        this.amount = amount;
        this.sources = sources;
        this.buyType = buyType;
        this.schedule = schedule;
    }

    @Override
    public JSONObject getRequestBody() {
        JSONObject json = new JSONObject();
        try {
            json.put("amount", amount);
            JSONArray fundSources = new JSONArray();
            for (int i = 0; i < sources.size(); i++) {
                FundSource source = sources.get(i);
                fundSources.put(i, source.getRequestBody());
            }
            json.put("fund_sources", fundSources);
            if (buyType == BuyType.MONTHLY) {
                // Null is really shit
                assert (schedule != null);
                assert (sources.size() == 1);
                json.put("payment_schedule", schedule.getRequestBody());
            }
            return json;
        } catch (JSONException e) {
            return null;
        }
    }
}
