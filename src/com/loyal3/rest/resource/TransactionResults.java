package com.loyal3.rest.resource;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class TransactionResults implements ResponseResource {
    public class TransactionSummary {
        private String planId;
        private String offerId;
        private String description;
        private Double amount;
        private Double shares;
        private Double price;
        private String status;
        private String date;

        public String getDate() {
            return date;
        }

        public String getStatus() {
            return status;
        }

        public Double getPrice() {
            return price;
        }

        public Double getShares() {
            return shares;
        }

        public Double getAmount() {
            return amount;
        }

        public String getDescription() {
            return description;
        }

        public String getPlanId() {
            return planId;
        }

        public String getOfferId() {
            return offerId;
        }

        public TransactionSummary(JSONObject json) throws Exception {
            if (!json.isNull("plan_id")) this.planId = json.getString("plan_id");
            if (!json.isNull("offer_id")) this.offerId = json.getString("offer_id");
            this.date = json.getString("date");
            this.description = json.getString("description");
            this.status = json.getString("status");
            if (!json.isNull("amount")) this.amount = json.getDouble("amount");
            if (!json.isNull("shares")) this.shares = json.getDouble("shares");
            if (!json.isNull("price")) this.price = json.getDouble("price");
        }

    }

    private List<TransactionSummary> transactions;

    public TransactionResults(JSONObject json) throws Exception {
        JSONArray jsonArray =  json.getJSONArray("transactions");
        transactions = new ArrayList<TransactionSummary>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = (JSONObject)jsonArray.get(i);
            TransactionSummary summary = new TransactionSummary(obj);
            transactions.add(summary);
        }
    }

    public List<TransactionSummary> getTransactions() {
        return transactions;
    }
}
