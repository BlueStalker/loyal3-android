package com.loyal3.rest.resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 */
public class OffersResults implements ResponseResource {
    public class OfferSummary {
        private String offerId;
        private String stock_symbol;
        private String exchange_name;
        private String organizationId;
        private String organizationName;
        private String type;
        private String state;
        private String prospectusHtml;
        private String updated_at;
        private boolean is_client;

        public OfferSummary(JSONObject json) throws JSONException {
            this.offerId = json.getString("offer_id");
            this.stock_symbol = json.getString("stock_symbol");
            this.exchange_name = json.getString("exchange_name");
            this.organizationName = json.getString("organization_name");
            this.organizationId = json.getString("organization_id");
            this.type = json.getString("type");
            this.state = json.getString("state");
            this.prospectusHtml = json.getString("prospectus_html");
            this.updated_at = json.getString("updated_at");
            this.is_client = json.getBoolean("is_client");
        }

        public String getOfferId() {
            return offerId;
        }

        public String getStockSymbol() {
            return stock_symbol;
        }

        public String getExchangeName() {
            return exchange_name;
        }

        public String getOrganizationId() {
            return organizationId;
        }

        public String getOrganizationName() {
            return organizationName;
        }

        public String getType() {
            return type;
        }

        public String getState() {
            return state;
        }

        public String getProspectusHtml() {
            return prospectusHtml;
        }

        public String getUpdatedAt() {
            return updated_at;
        }

        public boolean getIsClient() {
            return is_client;
        }
    }

    private List<OfferSummary> offers;
    public OffersResults(JSONObject json) throws JSONException {
        JSONArray jsonArray =  json.getJSONArray("offers");
        offers = new ArrayList<OfferSummary>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = (JSONObject)jsonArray.get(i);
            OfferSummary summary = new OfferSummary(obj);
            offers.add(summary);
        }
    }

    public List<OfferSummary> getOffers() {
        return offers;
    }

}
