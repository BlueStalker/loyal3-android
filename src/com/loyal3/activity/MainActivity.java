package com.loyal3.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.*;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import com.loyal3.R;
import com.loyal3.model.L3Contract;
import com.loyal3.rest.request.L3RestCode;
import com.loyal3.service.L3ServiceDelegate;
import com.loyal3.view.AccountHomeFragment;
import com.loyal3.view.BrowseFragment;
import com.loyal3.view.TransactionFragment;
import com.loyal3.view.TransferFragment;


/**
 * Created with IntelliJ IDEA.
 * User: curt
 */
public class MainActivity extends FragmentActivity {
    public static final String NEED_REFRESH = "com.loyal3.service.NEED_REFRESH";

    private FragmentTabHost mTabHost;

    private L3ServiceDelegate service;

    public static final int READY = 10;

    public static final int GO = 11;

    public static final int REQUEST_BUY = 70;

    public static final int REQUEST_SELL = 80;

    public static final int UPDATE_TAB_HOST = 90;

    ProgressDialog dialog;

    boolean isSyncing = false;
    boolean readyMessagePosted = false;
    boolean isActive = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case READY: {
                    Log.e("LOYAL3", "READY message received :: isSyncing " + isSyncing);
                    if (!isSyncing) {
                        isSyncing = true;
                        readyMessagePosted = false;
                        Message offerSummary = new Message();
                        offerSummary.what = L3Contract.OFFERS;
                        mHandler.sendMessage(offerSummary);
                    }
                    break;
                }
                case L3Contract.OFFERS: {
                    service.getOfferSummaries(new ResultReceiver(mHandler) {

                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            handleServiceError(resultCode);
                            Message accounts = new Message();
                            accounts.what = L3Contract.ACCOUNTS;
                            mHandler.sendMessage(accounts);
                        }

                    });
                    break;
                }

                case L3Contract.ACCOUNTS: {
                    service.getAccounts(new ResultReceiver(mHandler) {

                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            handleServiceError(resultCode);
                            Message plans = new Message();
                            plans.what = L3Contract.PAYMENTS;
                            mHandler.sendMessage(plans);
                        }

                    });
                    break;
                }

                case L3Contract.PAYMENTS: {
                    service.getPayments(new ResultReceiver(mHandler){

                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            handleServiceError(resultCode);
                            Message transactions = new Message();
                            transactions.what = L3Contract.PLANS;
                            mHandler.sendMessage(transactions);
                        }

                    });
                    break;
                }

                case L3Contract.PLANS: {
                    service.getPlanSummaries(new ResultReceiver(mHandler){

                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            handleServiceError(resultCode);
                            Message transactions = new Message();
                            transactions.what = L3Contract.TRANSACTIONS;
                            mHandler.sendMessage(transactions);
                        }

                    });
                    break;
                }

                case L3Contract.TRANSACTIONS: {
                    service.getTransactionSummaries(new ResultReceiver(mHandler){

                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            handleServiceError(resultCode);
                            Message go = new Message();
                            go.what = GO;
                            mHandler.sendMessage(go);
                        }

                    });
                    break;
                }

                case GO: {
                    if (dialog.isShowing()) dialog.dismiss();
                    isSyncing = false;
                    if (isActive && !readyMessagePosted) {
                        readyMessagePosted = true;
                        Message ready = new Message();
                        ready.what = READY;
                        mHandler.sendMessageDelayed(ready, 1000L * 60 * 1);
                    }
                    break;
                }

                case UPDATE_TAB_HOST: {
                    mTabHost.setCurrentTabByTag("" + message.arg1);
                    break;
                }

                default: break;
            }
        }
    };

    public void startBuyActivity(String offerId) {
        Intent intent = new Intent(this, BuyStockActivity.class);
        intent.putExtra(BuyStockActivity.OFFER_ID, offerId);
        startActivityForResult(intent, MainActivity.REQUEST_BUY);
    }

    public void startSellActivity(String planId, String imgUrl) {
        Intent intent = new Intent(this, SspSellActivity.class);
        intent.putExtra(SspSellActivity.PLAN_ID, planId);
        intent.putExtra(SspSellActivity.IMG_URL, imgUrl);
        startActivityForResult(intent, MainActivity.REQUEST_SELL);
    }

    public Handler getHandler() {
        return mHandler;
    }

    private void handleServiceError(int errorCode) {
        if (errorCode == L3RestCode.NOT_AUTH) {
            SharedPreferences.Editor editor = getSharedPreferences("com.loyal3", Context.MODE_MULTI_PROCESS).edit();
            editor.remove(L3Contract.APPUSER);
            editor.remove(L3Contract.COOKIE);
            editor.apply();
            finish();
        }
    }
    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {

        Log.d("LOYAL3", "MainActivity onCreate");
        setContentView(R.layout.main);
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("Waiting");
        dialog.setMessage("Loading");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        buildTabHost();
        dialog.show();
        service = L3ServiceDelegate.getInstance(MainActivity.this);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("LOYAL3", "MAIN onActivityResult " + requestCode + " " + resultCode);
        if (requestCode == REQUEST_BUY) {
            if (resultCode == BuyStockActivity.SUCCESS_BUY) {
                //mTabHost.setCurrentTabByTag("1");
                Message msg = new Message();
                msg.what = UPDATE_TAB_HOST;
                msg.arg1 = 1;
                mHandler.sendMessage(msg);
            }
        } else if (requestCode == REQUEST_SELL) {
            // Already in Home, doesn't need to update host
        }

        partiallySync();
    }

    public void partiallySync() {
        Log.e("LOYAL3", "partiallySync go");
        if (!isSyncing) {
            Message account = new Message();
            account.what = L3Contract.ACCOUNTS;
            mHandler.sendMessage(account);
        }
    }

    public void changeTab(String tab) {
        mTabHost.setCurrentTabByTag(tab);
    }

    private void buildTabHost() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("1").setIndicator(getTabHostView(R.string.action_overview, R.drawable.account_overview_gray)),
                AccountHomeFragment.class, new Bundle());

        mTabHost.addTab(mTabHost.newTabSpec("2").setIndicator(getTabHostView(R.string.action_browse_buy, R.drawable.browse_buy_gray)),
                BrowseFragment.class, new Bundle());

        mTabHost.addTab(mTabHost.newTabSpec("3").setIndicator(getTabHostView(R.string.action_transaction, R.drawable.transactions_gray)),
                TransactionFragment.class, new Bundle());

        mTabHost.addTab(mTabHost.newTabSpec("4").setIndicator(getTabHostView(R.string.action_transfer_funds, R.drawable.transfer_funds_gray)),
                TransferFragment.class, new Bundle());

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabName) {
                switch (Integer.valueOf(tabName)) {
                    case 1:
                        resetTabHostView();
                        ((TextView) mTabHost.getCurrentTabView().findViewById(R.id.tabLabel)).setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.account_overview), null, null);
                        break;
                    case 2:
                        resetTabHostView();
                        ((TextView) mTabHost.getCurrentTabView().findViewById(R.id.tabLabel)).setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.browse_buy), null, null);
                        break;
                    case 3:
                        resetTabHostView();
                        ((TextView) mTabHost.getCurrentTabView().findViewById(R.id.tabLabel)).setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.transactions), null, null);
                        break;
                    case 4:
                        resetTabHostView();
                        ((TextView) mTabHost.getCurrentTabView().findViewById(R.id.tabLabel)).setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.transfer_funds), null, null);
                        break;
                    default:
                        break;
                }
            }
        });
        // Pre select 1;
        ((TextView) mTabHost.getCurrentTabView().findViewById(R.id.tabLabel)).setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.account_overview), null, null);
    }

    private void resetTabHostView() {
        ((TextView)mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabLabel)).setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.account_overview_gray), null, null);
        ((TextView)mTabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabLabel)).setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.browse_buy_gray), null, null);
        ((TextView)mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabLabel)).setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.transactions_gray), null, null);
        ((TextView)mTabHost.getTabWidget().getChildAt(3).findViewById(R.id.tabLabel)).setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.transfer_funds_gray), null, null);
    }

    private View getTabHostView(int labelId, int drawableId) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_indicator, null);
        TextView tv = (TextView) view.findViewById(R.id.tabLabel);
        tv.setText(getResources().getString(labelId));
        tv.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(drawableId), null, null);
        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("LOYAL3", "main onResume");
        isActive = true;
        Message ready = new Message();
        ready.what = READY;
        mHandler.sendMessage(ready);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LOYAL3", "main onPause");
        isActive = false;
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_logout:
                Log.e("LOYAL3", "before remove all messages");
                mHandler.removeCallbacksAndMessages(null);
                Log.e("LOYAL3", "end remove all messages");
                service.doLogout(new ResultReceiver(mHandler) {

                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        // Clear all the SP.
                        SharedPreferences.Editor editor = getSharedPreferences("com.loyal3", Context.MODE_MULTI_PROCESS).edit();
                        editor.remove(L3Contract.APPUSER);
                        editor.remove(L3Contract.COOKIE);
                        editor.apply();
                        finish();
                    }

                });
                break;
            // action with ID action_settings was selected
            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        return true;
    }
}
