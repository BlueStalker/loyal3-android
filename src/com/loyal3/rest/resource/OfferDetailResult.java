package com.loyal3.rest.resource;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/10/14
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class OfferDetailResult implements ResponseResource {
    private String offerId;
    private String organizationId;
    private String organizationName;
    private String estimatedPricingDate;
    private Double priceRangeLow;
    private Double priceRangeHigh;
    private Double ipoPrice;
    private String mustConfirm;
    private Double minimumDollarAmount;
    private Double medianDollarAmount;
    private Double maximumDollarAmount;

    private String startAt;
    private String endAt;
    private Boolean isPrivate;
    private Boolean allowMonthly;
    private Boolean allowOnetime;
    private Boolean suitabilityRequired;
    private Boolean isClient;
    private Double marketClosePrice;

    public String getOfferId() {
        return offerId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getEstimatedPricingDate() {
        return estimatedPricingDate;
    }

    public Double getPriceRangeLow() {
        return priceRangeLow;
    }

    public Double getPriceRangeHigh() {
        return priceRangeHigh;
    }

    public Double getIpoPrice() {
        return ipoPrice;
    }

    public String getMustConfirm() {
        return mustConfirm;
    }

    public Double getMinimumDollarAmount() {
        return minimumDollarAmount;
    }

    public Double getMedianDollarAmount() {
        return medianDollarAmount;
    }

    public Double getMaximumDollarAmount() {
        return maximumDollarAmount;
    }

    public String getStartAt() {
        return startAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public Boolean isAllowMonthly() {
        return allowMonthly;
    }

    public Boolean isAllowOnetime() {
        return allowOnetime;
    }

    public Boolean isSuitabilityRequired() {
        return suitabilityRequired;
    }

    public Boolean getIsClient() {
        return isClient;
    }

    public Double getMarketClosePrice() {
        return marketClosePrice;
    }

    public OfferDetailResult(JSONObject json) throws JSONException {
        if (!json.isNull("offer_id")) this.offerId = json.getString("offer_id");
        if (!json.isNull("organization_id")) this.organizationId = json.getString("organization_id");
        if (!json.isNull("organization_name")) this.organizationName = json.getString("organization_name");
        if (!json.isNull("estimated_pricing_date")) this.estimatedPricingDate = json.getString("estimated_pricing_date");

        if (!json.isNull("price_range_low")) this.priceRangeLow = json.getDouble("price_range_low");
        if (!json.isNull("price_range_high")) this.priceRangeHigh = json.getDouble("price_range_high");
        if (!json.isNull("ipo_price")) this.ipoPrice = json.getDouble("ipo_price");

        if (!json.isNull("must_confirm")) this.mustConfirm = json.getString("must_confirm");
        if (!json.isNull("minimum_dollar_amount")) this.minimumDollarAmount = json.getDouble("minimum_dollar_amount");
        if (!json.isNull("median_dollar_amount")) this.medianDollarAmount = json.getDouble("median_dollar_amount");
        if (!json.isNull("maximum_dollar_amount")) this.maximumDollarAmount = json.getDouble("maximum_dollar_amount");

        if (!json.isNull("start_at")) this.startAt = json.getString("start_at");
        if (!json.isNull("end_at")) this.endAt = json.getString("end_at");

        if (!json.isNull("is_private")) this.isPrivate = json.getBoolean("is_private");
        if (!json.isNull("allow_monthly")) this.allowMonthly = json.getBoolean("allow_monthly");
        if (!json.isNull("allow_one_time")) this.allowOnetime = json.getBoolean("allow_one_time");
        if (!json.isNull("suitability_required")) this.suitabilityRequired = json.getBoolean("suitability_required");
        if (!json.isNull("isClient")) this.isClient = json.getBoolean("isClient");
        if (!json.isNull("market_close_price")) this.marketClosePrice = json.getDouble("market_close_price");
    }
}
