package com.loyal3.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Window;
import com.loyal3.R;
import com.loyal3.rest.request.L3RestCode;
import com.loyal3.service.L3ServiceDelegate;


public class Splash extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    private L3ServiceDelegate service;

    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);

        service = L3ServiceDelegate.getInstance(Splash.this);

        service.getAccounts(new ResultReceiver(mHandler) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                Log.d("LOYAL3", "Splash :: getAccounts :: " + resultCode);
                if (resultCode == L3RestCode.OK) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // This method will be executed once the timer is over
                            // Start your app main activity
                            Intent i = new Intent(Splash.this, MainActivity.class);
                            startActivity(i);

                            // close this activity
                            finish();
                        }
                    }, SPLASH_TIME_OUT);
                } else if (resultCode == L3RestCode.NOT_AUTH) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // This method will be executed once the timer is over
                            // Start your app main activity
                            Intent i = new Intent(Splash.this, LoginActivity.class);
                            startActivity(i);

                            // close this activity
                            finish();
                        }
                    }, SPLASH_TIME_OUT);
                }
            }

        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


}