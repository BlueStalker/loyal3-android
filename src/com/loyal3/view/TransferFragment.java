package com.loyal3.view;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.loyal3.R;
import com.loyal3.activity.MainActivity;
import com.loyal3.entity.PaymentInfo;
import com.loyal3.model.L3Contract;
import com.loyal3.model.Payment;
import com.loyal3.rest.resource.TransferResults;
import com.loyal3.service.L3ServiceDelegate;

public class TransferFragment extends Fragment {
    private FragmentActivity activity;

    private ImageView transferArrow;

    private EditText transferAmount;

    Button go;

    private boolean transferIn = true;

    private L3ServiceDelegate service;

    PaymentInfo cash, checking;

    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_transfer_funds, container, false);

        activity = getActivity();

        handler = ((MainActivity) activity).getHandler();
        transferArrow = (ImageView)rootView.findViewById(R.id.img_transfer);

        transferAmount = (EditText)rootView.findViewById(R.id.transfer_amount);

        go = (Button) rootView.findViewById(R.id.do_transfer);

        service = L3ServiceDelegate.getInstance(activity);

        init();

        transferArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (transferIn) {
                    transferArrow.setImageBitmap(rotateImage(BitmapFactory.decodeResource(getResources(), R.drawable.transfer_arrow), 180));
                    transferIn = false;
                } else {
                    transferArrow.setImageBitmap(rotateImage(BitmapFactory.decodeResource(getResources(), R.drawable.transfer_arrow), 0));
                    transferIn = true;
                }
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double amount =  Double.valueOf(transferAmount.getText().toString());
                if (!transferIn) {
                  // loyal3 to checking
                    service.transferFunds(amount, cash.id, checking.id, new ResultReceiver(handler) {

                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            if (resultCode == TransferResults.OK) {
                                Toast.makeText(activity, "Success", Toast.LENGTH_LONG).show();
                                ((MainActivity) activity).partiallySync();
                                ((MainActivity) activity).changeTab("3");
                            } else {
                                Toast.makeText(activity, "Failed because of " + resultCode, Toast.LENGTH_LONG).show();
                            }
                        }

                    });
                } else {
                  // checking to loyal3
                    service.transferFunds(amount, checking.id, cash.id, new ResultReceiver(handler) {

                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            if (resultCode == TransferResults.OK) {
                                Toast.makeText(activity, "Success", Toast.LENGTH_LONG).show();
                                ((MainActivity) activity).partiallySync();
                                ((MainActivity) activity).changeTab("3");
                            } else {
                                Toast.makeText(activity, "Failed because of " + resultCode, Toast.LENGTH_LONG).show();
                            }
                        }

                    });
                }
            }
        });

        return rootView;
    }

    public Bitmap rotateImage(Bitmap src, float degree) {
        // create new matrix object
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);
        // return new bitmap rotated using matrix
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    private void init() {
        Cursor c = activity.getContentResolver().query(Payment.CONTENT_URI, null, null, null, null);

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
                if (info.type == Payment.PaymentType.CASH.type) {
                    cash = info;
                } else if (info.type == Payment.PaymentType.CHECKING.type) {
                    checking = info;
                }
            } while(c.moveToNext());
        }
        c.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        transferAmount.requestFocus();
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
