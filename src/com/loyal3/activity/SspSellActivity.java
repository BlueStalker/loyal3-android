package com.loyal3.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.loyal3.R;
import com.loyal3.model.L3Contract;
import com.loyal3.rest.resource.BuyResults;
import com.loyal3.rest.resource.SellResults;
import com.loyal3.service.L3ServiceDelegate;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SspSellActivity extends Activity {
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public static final String PLAN_ID = "SspBuyOrUpdateActivity.PLAN_ID";
    public static final String IMG_URL = "SspSellActivity.IMG";
    private ImageView imageView;
    private EditText inputAmount;
    private String imageUrl;
    private String planId;
    private Button go;
    private L3ServiceDelegate service;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case L3Contract.SELL:
                    int resultCode = message.arg1;
                    if (resultCode == SellResults.OK) {
                        Toast.makeText(SspSellActivity.this, "Success", Toast.LENGTH_LONG).show();
                        setResult(L3Contract.COMMON_OK_RESULT_CODE);
                        finish();
                    } else {
                        Toast.makeText(SspSellActivity.this, "Failed Because Of: " + resultCode, Toast.LENGTH_LONG).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {

        Log.d("LOYAL3", "SspSellActivity created");
        setContentView(R.layout.ssp_sell);
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        imageUrl = extras.getString(IMG_URL);
        planId = extras.getString(PLAN_ID);
        imageView = (ImageView) findViewById(R.id.img_sell);
        inputAmount = (EditText) findViewById(R.id.sell_amount);
        service = L3ServiceDelegate.getInstance(SspSellActivity.this);
        go = (Button) findViewById(R.id.do_sell);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double shareAmount =  Double.valueOf(inputAmount.getText().toString());
                service.sell(planId, shareAmount, new ResultReceiver(mHandler) {

                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        Log.d("LOYAL3", "onReceiveResult :: " + resultCode);
                        Message sellResult = new Message();
                        sellResult.what = L3Contract.SELL;
                        sellResult.arg1 = resultCode;
                        mHandler.sendMessage(sellResult);
                    }

                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageLoader.displayImage(imageUrl, imageView);

        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(inputAmount, InputMethodManager.SHOW_FORCED);
    }
}
