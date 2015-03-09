package com.loyal3.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.loyal3.R;
import com.loyal3.adapter.SingleSelectionExpandableListAdapter;
import com.loyal3.entity.PaymentInfo;
import com.loyal3.model.L3Contract;
import com.loyal3.model.Payment;
import com.loyal3.rest.resource.BuyRequest;
import com.loyal3.rest.resource.BuyResults;
import com.loyal3.service.L3ServiceDelegate;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class SspBuyOrUpdateActivity extends Activity {

    public static final String OFFER_ID = "SspBuyOrUpdateActivity.OfferID";
    public static final String MIN = "SspBuyOrUpdateActivity.MIN";
    public static final String MEDIAN = "SspBuyOrUpdateActivity.MED";
    public static final String MAX = "SspBuyOrUpdateActivity.MAX";
    public static final String IMG_URL = "SspBuyOrUpdateActivity.IMG";

    public static final String TYPE = "SspBuyOrUpdateActivity.TYPE";
    public static final String AMOUNT = "SspBuyOrUpdateActivity.AMOUNT";
    public static final String DAY = "SspBuyOrUpdateActivity.DAY";
    public static final String PAYMENT = "SspBuyOrUpdateActivity.PAYMENT";

    private Double defaultAmount;
    private String defaultPayment;
    private Integer defaultDay;
    private String offerId;
    private Double minAmount, medianAmount, maxAmount, customAmount;
    private Double userCashAmount;
    private Double selectedAmount;
    private BuyRequest.BuyType buyType;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private ExpandableListView selectAmountList, selectPaymentList, monthlySelectList;

    private SingleSelectionExpandableListAdapter amountAdapter, paymentAdapter, monthlySelectAdapter;
    private ImageView imageView;
    private String imageUrl;
    private TextView descView;
    private L3ServiceDelegate service;

    private static final int UPDATE_DESC = 997;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case L3Contract.BUY:
                    int resultCode = message.arg1;
                    if (resultCode == BuyResults.OK) {
                        Toast.makeText(SspBuyOrUpdateActivity.this, "Success", Toast.LENGTH_LONG).show();
                        setResult(L3Contract.COMMON_OK_RESULT_CODE);
                        finish();
                    } else {
                        Toast.makeText(SspBuyOrUpdateActivity.this, "Failed Because Of: " + resultCode, Toast.LENGTH_LONG).show();
                    }
                    break;
                case UPDATE_DESC: {
                    descView.setText(buildDescription());
                    selectAmountList.collapseGroup(0);
                    selectPaymentList.collapseGroup(0);
                    if (buyType == BuyRequest.BuyType.MONTHLY) monthlySelectList.collapseGroup(0);
                    break;
                }
                default:
                    break;
            }
        }
    };

    private List<PaymentInfo> payments;
    private List<String> payments_labels;
    private List<String> monthly_selection;
    List<String> amounts = new ArrayList<String>();

    private String buildDescription() {
        String desc;
        if (selectedAmount == 0) return "";
        if (buyType == BuyRequest.BuyType.ONETIME) {
            if (userCashAmount >= selectedAmount)
                desc = selectedAmount + " one-time Buy " + selectedAmount + " from Cash";
            else {
                String label = payments_labels.get(paymentAdapter.getSelection());
                String ccOrCredit =  (selectedAmount - userCashAmount) + "from " + label;
                String cash = userCashAmount > 0 ?  userCashAmount + " from Cash " : "";
                desc = selectedAmount + " one-time Buy " + cash  + ccOrCredit;
            }
        } else {
            String label = payments_labels.get(paymentAdapter.getSelection());
            if (monthlySelectAdapter.getSelection() != -1)
                desc = selectedAmount + " monthly Buy using " + label + " " + "on " + monthly_selection.get(monthlySelectAdapter.getSelection());
            else desc = "";
        }
        return desc;
    }

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {

        Log.d("LOYAL3", "SspBuyOrUpdateActivity created");
        setContentView(R.layout.ssp_buy_update);
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        offerId = extras.getString(OFFER_ID);
        minAmount = extras.getDouble(MIN);
        medianAmount = extras.getDouble(MEDIAN);
        maxAmount = extras.getDouble(MAX);
        buyType = BuyRequest.BuyType.valueOf(extras.getString(TYPE));
        imageUrl = extras.getString(IMG_URL);
        imageView = (ImageView) findViewById(R.id.img_buy_onetime);
        descView = (TextView) findViewById(R.id.total_desc);

        amounts.add(minAmount.toString()); amounts.add(medianAmount.toString()); amounts.add(maxAmount.toString()); amounts.add("Other");
        service = L3ServiceDelegate.getInstance(SspBuyOrUpdateActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageLoader.displayImage(imageUrl, imageView);
        Cursor c = getContentResolver().query(Payment.CONTENT_URI, null, null, null, null);
        payments = new ArrayList<PaymentInfo>();
        payments_labels = new ArrayList<String>();
        if (c.moveToFirst()) {
            do {
                PaymentInfo info = new PaymentInfo();
                info.type = c.getInt(c.getColumnIndex(Payment.PAYMENT_TYPE));
                if (info.type == Payment.PaymentType.CASH.type) {
                    info.amount = c.getDouble(c.getColumnIndex(Payment.AMOUNT));
                }
                info.label = Payment.PaymentType.fromType(info.type).toString();
                info.desc = info.label;
                info.id = c.getString(c.getColumnIndex(Payment.PAYMENT_ID));
                // CASH can not be a label
                if (info.type != Payment.PaymentType.CASH.type) payments_labels.add(info.label);
                payments.add(info);
            } while(c.moveToNext());
        }
        c.close();

        for (PaymentInfo payment : payments) {
            if (payment.type == Payment.PaymentType.CASH.type) userCashAmount = payment.amount;
        }
        Bundle extras = getIntent().getExtras();
        if (buyType == BuyRequest.BuyType.MONTHLY) {
            if (extras.containsKey(AMOUNT)) defaultAmount = extras.getDouble(AMOUNT);
            if (extras.containsKey(PAYMENT)) {
                String defaultPaymentId = extras.getString(PAYMENT);
                Log.d("LOYAL3", "some sort of shit :" + defaultPaymentId);
                for(PaymentInfo paymentInfo : payments) {
                    Log.d("LOYAL3", "some sort of shit in :" + paymentInfo.id);
                    if (paymentInfo.id.equals(defaultPaymentId)) {

                        defaultPayment =  paymentInfo.label;
                    }
                }

            }
            if (extras.containsKey(DAY)) defaultDay =  extras.getInt(DAY);
        }
        //selectedAmount = 0.0;
        selectAmountList = (ExpandableListView) findViewById(R.id.select_amount_list);
        selectAmountList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        amountAdapter = new SingleSelectionExpandableListAdapter(this, "Select Investment Amount", amounts);
        selectAmountList.setAdapter(amountAdapter);

        selectPaymentList = (ExpandableListView) findViewById(R.id.select_payment_list);
        selectPaymentList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        paymentAdapter = new SingleSelectionExpandableListAdapter(this, "Select Payment Method", payments_labels);
        selectPaymentList.setAdapter(paymentAdapter);
        selectAmountList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int parentPos, int childPos, long l) {
                amountAdapter.setSelected(childPos);
                amountAdapter.notifyDataSetChanged();
                if (childPos != 3) {
                    customAmount = 0.0;
                    selectedAmount = Double.valueOf(amounts.get(childPos));
                    Message message = new Message();
                    message.what = UPDATE_DESC;
                    mHandler.sendMessage(message);
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(SspBuyOrUpdateActivity.this);

                    alert.setTitle("Choose Custom Amount");
                    alert.setMessage("should between 0 to 2500");

                    final EditText input = new EditText(SspBuyOrUpdateActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    alert.setView(input);
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            customAmount = Double.valueOf(input.getText().toString());
                            selectedAmount = customAmount;
                            Message message = new Message();
                            message.what = UPDATE_DESC;
                            mHandler.sendMessage(message);
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });

                    alert.show();
                }
                return true;
            }
        });

        // Always pre_select the 1st one.
        paymentAdapter.setSelected(0);
        selectPaymentList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int parentPos, int childPos, long l) {
                paymentAdapter.setSelected(childPos);
                Message message = new Message();
                message.what = UPDATE_DESC;
                mHandler.sendMessage(message);
                paymentAdapter.notifyDataSetChanged();
                return true;
            }
        });

        if (buyType == BuyRequest.BuyType.MONTHLY) {
            monthly_selection = new ArrayList<String>();
            for (int i = 1; i <= 28; i++) monthly_selection.add("" + i);
            monthlySelectList = (ExpandableListView) findViewById(R.id.monthly_selection);
            monthlySelectAdapter = new SingleSelectionExpandableListAdapter(this, "Select Monthly Day", monthly_selection);
            monthlySelectList.setAdapter(monthlySelectAdapter);

            monthlySelectList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView expandableListView, View view, int parentPos, int childPos, long l) {
                    monthlySelectAdapter.setSelected(childPos);
                    Message message = new Message();
                    message.what = UPDATE_DESC;
                    mHandler.sendMessage(message);
                    monthlySelectAdapter.notifyDataSetChanged();
                    return true;
                }
            });
        }
        restoreDefault();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_one_time, menu);
        return true;
    }

    private void restoreDefault() {
        Log.d("LOYAL3", "restoreDefault :: " + defaultAmount + " " + defaultPayment + " " +defaultDay);
        selectAmountList.collapseGroup(0);
        selectPaymentList.collapseGroup(0);
        if (buyType == BuyRequest.BuyType.MONTHLY) {
            monthlySelectList.collapseGroup(0);
        }
        if (defaultAmount == null) {
            selectedAmount = 0.0;
        } else {
            selectedAmount = defaultAmount;
            if (selectedAmount.equals(minAmount)) amountAdapter.setSelected(0);
            else if (selectedAmount.equals(medianAmount)) amountAdapter.setSelected(1);
            else if (selectedAmount.equals(maxAmount)) amountAdapter.setSelected(2);
            else amountAdapter.setSelected(3);
        }

        if (defaultPayment == null) {
            paymentAdapter.setSelected(0);
        } else {
            for (int i = 0; i < payments_labels.size(); i++) {
               if (payments_labels.get(i).equals(defaultPayment))
                 paymentAdapter.setSelected(i);
            }
        }

        if (defaultDay == null) {
           // Do Nothing
        } else {
           monthlySelectAdapter.setSelected(defaultDay - 1);
        }
        if (defaultAmount == null) descView.setText("");
        else descView.setText(buildDescription());
    }

    private List<BuyRequest.FundSource> buildFundingSource(Double amount, String label) {
        List<BuyRequest.FundSource> results = new ArrayList<BuyRequest.FundSource>();
        PaymentInfo cash = null, checking = null, creditCard = null;
        for (PaymentInfo payment : payments) {
            if (payment.type == Payment.PaymentType.CASH.type) {
                cash = payment;
            } else if (payment.type == Payment.PaymentType.CHECKING.type) {
                checking = payment;
            } else {
                creditCard = payment;
            }
        }
        if (buyType == BuyRequest.BuyType.ONETIME) {

            if (cash.amount >= amount) {
                BuyRequest.FundSource fromCash = new BuyRequest.FundSource(amount, cash.id);
                results.add(fromCash);
            } else {
                if (cash.amount > 0) {
                    BuyRequest.FundSource fromCash = new BuyRequest.FundSource(cash.amount, cash.id);
                    results.add(fromCash);
                }
                if (label.equals("CHECKING")) {
                    BuyRequest.FundSource fromChecking = new BuyRequest.FundSource(amount - cash.amount, checking.id);
                    results.add(fromChecking);
                } else {
                    BuyRequest.FundSource fromCreditCard = new BuyRequest.FundSource(amount - cash.amount, creditCard.id);
                    results.add(fromCreditCard);
                }
            }
        } else {
            if (label.equals("CHECKING")) {
                BuyRequest.FundSource fromChecking = new BuyRequest.FundSource(amount, checking.id);
                results.add(fromChecking);
            } else {
                BuyRequest.FundSource fromCreditCard = new BuyRequest.FundSource(amount, creditCard.id);
                results.add(fromCreditCard);
            }
        }
        return results;
    }

    private BuyRequest.PaymentSchedule buildSchedule(Double amount, Integer day) {
        return new BuyRequest.PaymentSchedule(amount, day);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_one_time_buy:
                int amountChoice = amountAdapter.getSelection();
                int paymentChoice = paymentAdapter.getSelection();
                int monthlyChoice = -1;
                if (buyType == BuyRequest.BuyType.MONTHLY) monthlyChoice = monthlySelectAdapter.getSelection();
                if (amountChoice == -1 || paymentChoice == -1 || (buyType == BuyRequest.BuyType.MONTHLY && monthlyChoice == -1)) {
                    Toast.makeText(this, "Make enough Choice Please", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(this, "Buy " + buyType + " with " + selectedAmount  + " using " + payments_labels.get(paymentChoice), Toast.LENGTH_SHORT)
                        .show();
                    BuyRequest.PaymentSchedule schedule = null;
                    if (buyType == BuyRequest.BuyType.MONTHLY) schedule = buildSchedule(selectedAmount, Integer.valueOf(monthly_selection.get(monthlyChoice)));
                    service.buy(selectedAmount, buildFundingSource(selectedAmount, payments_labels.get(paymentChoice)), offerId, buyType, schedule, new ResultReceiver(mHandler) {

                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            Log.d("LOYAL3", "onReceiveResult :: " + resultCode);
                            Message buyResult = new Message();
                            buyResult.what = L3Contract.BUY;
                            buyResult.arg1 = resultCode;
                            mHandler.sendMessage(buyResult);
                        }

                    });
                }
                break;
            case R.id.action_reset:
                restoreDefault();
                break;
            default:
                break;
        }

        return true;
    }
}
