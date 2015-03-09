package com.loyal3.rest.resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/11/14
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaymentResults implements ResponseResource {
    public class PaymentSummary {
        private String paymentMethodId;
        private Integer type;
        private String firstName;
        private String lastName;
        private String accountId;
        private String accountNumber;
        private String routingNumber;
        private Double amount;

        public String getPaymentMethodId() {
            return paymentMethodId;
        }

        public Integer getType() {
            return type;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getAccountId() {
            return accountId;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getRoutingNumber() {
            return routingNumber;
        }

        public Double getAmount() {
            return amount;
        }

        public PaymentSummary(JSONObject json) throws JSONException {
            paymentMethodId = json.getString("payment_method_id");
            type = json.getInt("type");
            if (!json.isNull("first_name")) firstName = json.getString("first_name");
            if (!json.isNull("last_name")) lastName = json.getString("last_name");
            accountId = json.getString("account_id");
            if (!json.isNull("account_number")) accountNumber = json.getString("account_number");
            if (!json.isNull("routing_number")) routingNumber = json.getString("routing_number");
            if (!json.isNull("amount")) amount = json.getDouble("amount");
        }
    }

    private List<PaymentSummary> payments;
    public PaymentResults(JSONObject json) throws JSONException {
        JSONArray jsonArray =  json.getJSONArray("payment_methods");
        payments = new ArrayList<PaymentSummary>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = (JSONObject)jsonArray.get(i);
            PaymentSummary summary = new PaymentSummary(obj);
            payments.add(summary);
        }
    }

    public List<PaymentSummary> getPayments() {
        return payments;
    }
}
