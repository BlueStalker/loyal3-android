package com.loyal3.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.loyal3.R;
import com.loyal3.model.L3Contract;
import com.loyal3.rest.request.L3RestCode;
import com.loyal3.service.L3ServiceDelegate;


public class LoginActivity extends Activity {

    private EditText username=null;
    private EditText  password=null;
    private Button login;
    private L3ServiceDelegate service;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        service = L3ServiceDelegate.getInstance(LoginActivity.this);

        username = (EditText)findViewById(R.id.edit_username);
        password = (EditText)findViewById(R.id.edit_password);
        String savedUserName = getSharedPreferences("com.loyal3", Context.MODE_MULTI_PROCESS).getString(L3Contract.SAVED_USERNAME, "");
        if (savedUserName.length() > 0) username.setText(savedUserName);

        String savedPassword = getSharedPreferences("com.loyal3", Context.MODE_MULTI_PROCESS).getString(L3Contract.SAVED_PASSWORD, "");
        if (savedPassword.length() > 0) password.setText(savedPassword);
        login = (Button)findViewById(R.id.button1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(username, InputMethodManager.SHOW_FORCED);
    }

    public void doLogin(View view) {
        SharedPreferences.Editor editor = getSharedPreferences("com.loyal3", Context.MODE_MULTI_PROCESS).edit();
        editor.putString(L3Contract.SAVED_USERNAME, username.getText().toString());
        editor.putString(L3Contract.SAVED_PASSWORD, password.getText().toString());
        editor.apply();
        String apiKey = getString(R.string.api_key);
        service.postLogin(username.getText().toString(), password.getText().toString(), apiKey, new ResultReceiver(mHandler) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == 200) {
                    Toast.makeText(getApplicationContext(), "Redirecting...",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(MainActivity.NEED_REFRESH, true);
                    startActivity(intent);
                }   else {
                    Toast.makeText(getApplicationContext(), "Wrong Credentials",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}