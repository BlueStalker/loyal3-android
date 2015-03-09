package com.loyal3.service.processor;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import com.loyal3.model.Offer;
import com.loyal3.rest.RestMethod;
import com.loyal3.rest.RestMethodFactory;
import com.loyal3.rest.RestMethodResult;
import com.loyal3.rest.request.L3RestCode;
import com.loyal3.rest.resource.OfferDetailResult;
import com.loyal3.rest.resource.OffersResults;
import com.loyal3.rest.resource.RequestResource;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.*;

public class OfferProcessor {
    private Context mContext;

    public OfferProcessor(Context context) {
        mContext = context;
    }

    public interface OffersCallback {
        public void send(int resultCode);
    }

    class OfferIdResource implements RequestResource {
        private String offerId;

        public OfferIdResource(String offerId) {
            this.offerId = offerId;
        }

        public JSONObject getRequestBody() {
            JSONObject ret = new JSONObject();
            try {
                ret.put("offer_id", offerId);
                return ret;
            } catch (Exception e) {
                return null;
            }
        }
    }

    private ConcurrentHashMap<String, ContentValues> allValues;

    class OfferDetailsLoadingThread extends Thread {
        private String offer_id;

        public OfferDetailsLoadingThread(String offer_id) {
            this.offer_id = offer_id;
        }

        public void run() {
            RequestResource data = new OfferIdResource(offer_id);
            RestMethod<OfferDetailResult> offerDetailRequest = RestMethodFactory.getInstance(mContext).getRestMethod(Offer.CONTENT_URI, RestMethodFactory.Method.GET, null, data);
            RestMethodResult<OfferDetailResult> detailResult = offerDetailRequest.execute();
            if (detailResult == null) return;
            OfferDetailResult offerDetail = detailResult.getResource();
            ContentValues values = allValues.get(offer_id);
            if (detailResult.getStatusCode() == L3RestCode.OK) {
                if (offerDetail.getEstimatedPricingDate() != null)
                    values.put(Offer.ESTIMATED_PRICING_DATE, offerDetail.getEstimatedPricingDate());
                if (offerDetail.getPriceRangeLow() != null)
                    values.put(Offer.PRICE_RANGE_LOW, offerDetail.getPriceRangeLow());
                if (offerDetail.getPriceRangeHigh() != null)
                    values.put(Offer.PRICE_RANGE_HIGH, offerDetail.getPriceRangeHigh());

                if (offerDetail.getIpoPrice() != null) values.put(Offer.IPO_PRICE, offerDetail.getIpoPrice());
                if (offerDetail.getMustConfirm() != null)
                    values.put(Offer.MUST_RECONFIRM, offerDetail.getMustConfirm());
                if (offerDetail.getMinimumDollarAmount() != null)
                    values.put(Offer.MINIMUM_DOLLAR_AMOUNT, offerDetail.getMinimumDollarAmount());
                if (offerDetail.getMedianDollarAmount() != null)
                    values.put(Offer.MEDIAN_DOLLAR_AMOUNT, offerDetail.getMedianDollarAmount());
                if (offerDetail.getMaximumDollarAmount() != null)
                    values.put(Offer.MAXIMUM_DOLLAR_AMOUNT, offerDetail.getMaximumDollarAmount());

                if (offerDetail.getStartAt() != null) values.put(Offer.START_AT, offerDetail.getStartAt());
                if (offerDetail.getEndAt() != null) values.put(Offer.END_AT, offerDetail.getEndAt());
                if (offerDetail.getIsPrivate() != null) values.put(Offer.IS_PRIVATE, offerDetail.getIsPrivate());
                if (offerDetail.isAllowMonthly() != null) values.put(Offer.ALLOW_MONTHLY, offerDetail.isAllowMonthly());
                if (offerDetail.isAllowOnetime() != null) values.put(Offer.ALLOW_ONETIME, offerDetail.isAllowOnetime());
                if (offerDetail.isSuitabilityRequired() != null)
                    values.put(Offer.SUITABILITY_REQUIRED, offerDetail.isSuitabilityRequired());
                if (offerDetail.getIsClient() != null) values.put(Offer.IS_CLIENT, offerDetail.getIsClient());
                if (offerDetail.getMarketClosePrice() != null)
                    values.put(Offer.MARKET_CLOSE_PRICE, offerDetail.getMarketClosePrice());
                allValues.put(offer_id, values);
            }
        }
    }

    public void getOffers(OffersCallback callback) {
        @SuppressWarnings("unchecked")
        RestMethod<OffersResults> offersSummaryRequest = RestMethodFactory.getInstance(mContext).getRestMethod(Offer.CONTENT_URI, RestMethodFactory.Method.GET, null, null);
        RestMethodResult<OffersResults> result = offersSummaryRequest.execute();
        if (result == null) return;

        OffersResults offersResults = result.getResource();

        // Use the executor to execute fetch offer details in the background thread.
        final ExecutorService executor = new ThreadPoolExecutor(20, 40, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());


        if (result.getStatusCode() == L3RestCode.OK) {
            // Get the list of offer summaries
            List<OffersResults.OfferSummary> offers = offersResults.getOffers();
            allValues = new ConcurrentHashMap<String, ContentValues>();
            for (int i = 0; i < offers.size(); i++) {
                OffersResults.OfferSummary offerSummary = offers.get(i);
                ContentValues values = new ContentValues();
                values.put(Offer.OFFER_ID, offerSummary.getOfferId());
                values.put(Offer.ORGANIZATION_ID, offerSummary.getOrganizationId());
                values.put(Offer.ORGANIZATION_NAME, offerSummary.getOrganizationName());
                values.put(Offer.EXCHANGE_NAME, offerSummary.getExchangeName());
                values.put(Offer.OFFER_TYPE, offerSummary.getType());
                values.put(Offer.STOCK_SYMBOL, offerSummary.getStockSymbol());
                allValues.put(offerSummary.getOfferId(), values);
                executor.execute(new OfferDetailsLoadingThread(offerSummary.getOfferId()));
            }
            executor.shutdown();

            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                Log.e("LOYAL3", "some problems here");
            }

            mContext.getContentResolver().bulkInsert(Offer.CONTENT_URI, allValues.values().toArray(new ContentValues[0]));
            callback.send(result.getStatusCode());
        } else if (result.getStatusCode() == L3RestCode.NOT_AUTH) {
            callback.send(result.getStatusCode());
        }
    }
}
