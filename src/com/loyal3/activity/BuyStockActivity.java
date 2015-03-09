package com.loyal3.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.loyal3.R;
import com.loyal3.model.Aip;
import com.loyal3.model.L3Contract;
import com.loyal3.model.Offer;
import com.loyal3.model.Payment;
import com.loyal3.rest.resource.BuyRequest;
import com.loyal3.service.L3ServiceDelegate;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class BuyStockActivity extends Activity {

    public static final String OFFER_ID = "com.loyal3.service.OFFER_ID";
    private L3ServiceDelegate service;

    private ImageView imgBrowseStock;
    private TextView exchange, symbol, price;
    private Button oneTime, add_update_aip, delete_aip;

    private String offerId;
    private String imageUrl;
    private Double minAmount, medianAmount, maxAmount;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private Double aipAmount;
    private String aipPayment;
    private Integer aipDay;

    private boolean dataReady = false;
    public static final int SUCCESS_BUY = 145;

    public static final int REQUEST_CODE = 100;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {

            }
        }
    };

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {

        Log.d("LOYAL3", "BuyStockActivity created");
        setContentView(R.layout.buy_stock);
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        offerId = extras.getString(OFFER_ID);
        service = L3ServiceDelegate.getInstance(BuyStockActivity.this);
        imgBrowseStock = (ImageView) findViewById(R.id.img_buy_stock);
        exchange = (TextView) findViewById(R.id.stock_exchange);
        symbol = (TextView) findViewById(R.id.stock_symbol);
        price = (TextView) findViewById(R.id.stock_price);
        oneTime = (Button) findViewById(R.id.one_time_buy);
        oneTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataReady) {
                    Intent intent = commonBuyIntent();
                    intent.putExtra(SspBuyOrUpdateActivity.TYPE, BuyRequest.BuyType.ONETIME.type);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });
        add_update_aip = (Button) findViewById(R.id.add_update_aip);

        add_update_aip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataReady) {
                    Intent intent = commonBuyIntent();
                    if (aipAmount != null) intent.putExtra(SspBuyOrUpdateActivity.AMOUNT, aipAmount);
                    if (aipDay != null) intent.putExtra(SspBuyOrUpdateActivity.DAY, aipDay);
                    if (aipPayment != null) intent.putExtra(SspBuyOrUpdateActivity.PAYMENT, aipPayment);

                    intent.putExtra(SspBuyOrUpdateActivity.TYPE, BuyRequest.BuyType.MONTHLY.type);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });
        delete_aip = (Button) findViewById(R.id.remove_aip);
        delete_aip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuyRequest.PaymentSchedule schedule = new BuyRequest.PaymentSchedule(0.0, aipDay);
                service.buy(0.0, buildFundingSource(), offerId, BuyRequest.BuyType.MONTHLY, schedule, new ResultReceiver(mHandler) {

                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        Log.d("LOYAL3", "onReceiveResult :: " + resultCode);
                        setResult(SUCCESS_BUY);
                        finish();
                    }

                });
            }
        });
    }

    private List<BuyRequest.FundSource> buildFundingSource() {
        List<BuyRequest.FundSource> results = new ArrayList<BuyRequest.FundSource>();
        BuyRequest.FundSource source = new BuyRequest.FundSource(0.0, aipPayment);
        results.add(source);
        return results;
    }

    private Intent commonBuyIntent() {
        Intent intent = new Intent(BuyStockActivity.this, SspBuyOrUpdateActivity.class);
        intent.putExtra(SspBuyOrUpdateActivity.OFFER_ID, offerId);
        intent.putExtra(SspBuyOrUpdateActivity.MIN, minAmount);
        intent.putExtra(SspBuyOrUpdateActivity.MEDIAN, medianAmount);
        intent.putExtra(SspBuyOrUpdateActivity.MAX, maxAmount);
        intent.putExtra(SspBuyOrUpdateActivity.IMG_URL, imageUrl);
        return intent;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("LOYAL3", "onActivityResult :: " + requestCode + " :: " + resultCode);
        if (requestCode == REQUEST_CODE) {
            if(resultCode == L3Contract.COMMON_OK_RESULT_CODE){
                //finish();
                setResult(SUCCESS_BUY);
                finish();
            }
            else finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Cursor offerCursor = getContentResolver().query(Uri.parse("content://" + L3Contract.AUTHORITY + "/" + Offer.TABLE_NAME + "/" + offerId),
                new String[]{ Offer.EXCHANGE_NAME,
                              Offer.ALLOW_MONTHLY,
                              Offer.ALLOW_ONETIME,
                              Offer.STOCK_SYMBOL,
                              Offer.MARKET_CLOSE_PRICE,
                              Offer.MINIMUM_DOLLAR_AMOUNT,
                              Offer.MEDIAN_DOLLAR_AMOUNT,
                              Offer.MAXIMUM_DOLLAR_AMOUNT}, null, null, null);
        offerCursor.moveToFirst();
        imageUrl = getString(R.string.api_baseurl) + "css/images/logos/" + offerCursor.getString(offerCursor.getColumnIndex(Offer.STOCK_SYMBOL)) + ".png";
        imageLoader.displayImage(imageUrl, imgBrowseStock);
        exchange.setText(offerCursor.getString(offerCursor.getColumnIndex(Offer.EXCHANGE_NAME)));
        symbol.setText(offerCursor.getString(offerCursor.getColumnIndex(Offer.STOCK_SYMBOL)));
        price.setText(offerCursor.getString(offerCursor.getColumnIndex(Offer.MARKET_CLOSE_PRICE)));
        if (offerCursor.getInt(offerCursor.getColumnIndex(Offer.ALLOW_ONETIME)) == 0) {
            oneTime.setVisibility(View.GONE);
        }
        minAmount = offerCursor.getDouble(offerCursor.getColumnIndex(Offer.MINIMUM_DOLLAR_AMOUNT));
        medianAmount = offerCursor.getDouble(offerCursor.getColumnIndex(Offer.MEDIAN_DOLLAR_AMOUNT));
        maxAmount = offerCursor.getDouble(offerCursor.getColumnIndex(Offer.MAXIMUM_DOLLAR_AMOUNT));
        offerCursor.close();

        Cursor aipCursor = getContentResolver().query(Aip.CONTENT_URI, null,  Aip.OFFER_ID + " = ?", new String[]{offerId}, null);
        if (aipCursor.moveToFirst()) {
            aipAmount = aipCursor.getDouble(aipCursor.getColumnIndex(Aip.AMOUNT));
            aipDay = aipCursor.getInt(aipCursor.getColumnIndex(Aip.DAY));
            aipPayment = aipCursor.getString(aipCursor.getColumnIndex(Aip.PAYMENT_METHOD_ID));

            add_update_aip.setText("Update Monthly Plan");
        } else {
            add_update_aip.setText("Add Monthly Plan");
            delete_aip.setVisibility(View.GONE);
        }
        aipCursor.close();
        dataReady = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataReady = false;
    }
}
