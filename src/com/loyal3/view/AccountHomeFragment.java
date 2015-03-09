package com.loyal3.view;


import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.loyal3.R;
import com.loyal3.activity.MainActivity;
import com.loyal3.model.Account;
import com.loyal3.model.L3Contract;
import com.loyal3.model.Offer;
import com.loyal3.model.Plan;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AccountHomeFragment extends Fragment {

    class PLanMeta {
        String planId, image;
        Double shares, price, value;
    }

    private FragmentActivity activity;

    private  LayoutInflater inflater;

    private TextView totalAccountValueView;

    private  TextView cashBalanceView;

    private String userId;

    private DisplayImageOptions options;

    private Cursor plansCursor;

    private Cursor accountCursor;

    private List<PLanMeta> plans = new ArrayList<PLanMeta>();
    private ListView planList;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private static final int ACCOUNT_CHANGE = 10;

    private static final int PLAN_CHANGE = 11;

    private ContentObserver accountObserver;

    private ContentObserver planObserver;

    private Handler accountHomeHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case ACCOUNT_CHANGE: {
                    updateAccountText();
                    break;
                }

                case PLAN_CHANGE: {
                    plansCursor = activity.getContentResolver().query(Plan.CONTENT_URI, null, null, null, null);
                    refreshPlanLists();
                    ((BaseAdapter) planList.getAdapter()).notifyDataSetChanged();
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void refreshPlanLists() {
        plans.clear();
        if (plansCursor.moveToFirst()) {
            do {
                PLanMeta plan = new PLanMeta();
                plan.shares = plansCursor.getDouble(plansCursor.getColumnIndex(Plan.SHARES_OWNED));
                plan.price = plansCursor.getDouble(plansCursor.getColumnIndex(Plan.SHARES_PRICE));
                plan.value = plansCursor.getDouble(plansCursor.getColumnIndex(Plan.CURRENT_VALUE));
                plan.image = getString(R.string.api_baseurl) + "css/images/logos/" + plansCursor.getString(plansCursor.getColumnIndex(Offer.STOCK_SYMBOL)) + ".png";
                plan.planId = plansCursor.getString(plansCursor.getColumnIndex(Plan.PLAN_ID));
                plans.add(plan);
            } while(plansCursor.moveToNext());
        }
    }

    private void updateAccountText() {
        if (userId.trim().isEmpty()) {
          accountCursor = activity.getContentResolver().query(Account.CONTENT_URI,
                    new String[] {Account.TOTAL_VALUE, Account.CASH_VALUE}, null, null, null);
        } else {
          accountCursor = activity.getContentResolver().query(Account.CONTENT_URI,
                    new String[] {Account.TOTAL_VALUE, Account.CASH_VALUE}, Account.ACCOUNT_ID + " = ?", new String[]{userId}, null);
        }

        if (accountCursor.moveToFirst()) {
            totalAccountValueView.setText("Total Account Value : " + accountCursor.getDouble(accountCursor.getColumnIndex(Account.TOTAL_VALUE)));
            cashBalanceView.setText("Available Cash : " + accountCursor.getDouble(accountCursor.getColumnIndex(Account.CASH_VALUE)));
        }
        accountCursor.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        accountObserver = new ContentObserver(accountHomeHandler) {
            @Override
            public void onChange(boolean selfChange) {
                Message msg = new Message();
                msg.what = ACCOUNT_CHANGE;
                accountHomeHandler.sendMessage(msg);
            }
        };
        activity.getContentResolver().registerContentObserver(Account.CONTENT_URI, true, accountObserver);

        planObserver = new ContentObserver(accountHomeHandler) {
            @Override
            public void onChange(boolean selfChange) {
                Message msg = new Message();
                msg.what = PLAN_CHANGE;
                accountHomeHandler.sendMessage(msg);
            }
        };
        activity.getContentResolver().registerContentObserver(Plan.CONTENT_URI, true, planObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.getContentResolver().unregisterContentObserver(accountObserver);
        activity.getContentResolver().unregisterContentObserver(planObserver);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_accounthome, container, false);

        activity = getActivity();
        this.inflater = inflater;

        options = new DisplayImageOptions.Builder()
//                .showImageOnFail(R.drawable.csop)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
        userId = activity.getSharedPreferences("com.loyal3", Context.MODE_MULTI_PROCESS).getString(L3Contract.APPUSER, "");

        totalAccountValueView = (TextView)rootView.findViewById(R.id.total_account_value);
        cashBalanceView = (TextView) rootView.findViewById(R.id.cash_balance);

        updateAccountText();

        plansCursor = activity.getContentResolver().query(Plan.CONTENT_URI, null, null, null, null);

        refreshPlanLists();
        planList = (ListView) rootView.findViewById(R.id.plans_list);
        planList.setAdapter(new PlanListAdapter());

        planList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (plans.get(i).shares <= 0) {
                    Toast.makeText(activity, "You don't own any shares", Toast.LENGTH_SHORT).show();
                } else {
                    ((MainActivity) activity).startSellActivity(plans.get(i).planId, plans.get(i).image);
                }
            }
        });

        return rootView;
    }

    private static class ViewHolder {
        TextView shareAmount;
        TextView sharePrice;
        TextView shareValue;
        ImageView image;
    }


    class PlanListAdapter extends BaseAdapter {

        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        @Override
        public int getCount() {
            return plans.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final ViewHolder holder;
            if (convertView == null) {
                view = inflater.inflate(R.layout.plan_list_item, parent, false);
                holder = new ViewHolder();
                holder.shareAmount = (TextView) view.findViewById(R.id.plan_shares_amount);
                holder.sharePrice = (TextView) view.findViewById(R.id.plan_share_price);
                holder.shareValue = (TextView) view.findViewById(R.id.plan_share_value);
                holder.image = (ImageView) view.findViewById(R.id.plan_image);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            PLanMeta plan = plans.get(position);
            holder.shareAmount.setText("" + "Shares:\n" + plan.shares);
            holder.sharePrice.setText("" + "Prices:\n" + plan.price);
            holder.shareValue.setText("" + "Value:\n" + plan.value);

            imageLoader.displayImage(plan.image, holder.image, options, animateFirstListener);

            return view;
        }
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
