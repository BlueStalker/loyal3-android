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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.loyal3.R;
import com.loyal3.model.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class TransactionFragment extends Fragment {
    private FragmentActivity activity;

    private LayoutInflater inflater;

    private String userId;

    private DisplayImageOptions options;

    private Cursor transactionCursor;

    private ContentObserver transactionObserver;

    ListView transactionList;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public static final int TRANSACTION_CHANGE = 4001;
    private Handler transactionHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case TRANSACTION_CHANGE: {
                    transactionCursor = activity.getContentResolver().query(Transaction.CONTENT_URI, null, null, null, null);
                    ((BaseAdapter) transactionList.getAdapter()).notifyDataSetChanged();
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_transactions, container, false);

        activity = getActivity();
        this.inflater = inflater;

        options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.csop)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();


        userId = activity.getSharedPreferences("com.loyal3", Context.MODE_MULTI_PROCESS).getString(L3Contract.APPUSER, "");

        transactionCursor = activity.getContentResolver().query(Transaction.CONTENT_URI, null, null, null, null);

//        transactionCursor.setNotificationUri(activity.getContentResolver(), Transaction.CONTENT_URI);

        transactionList = (ListView) rootView.findViewById(R.id.transactions_list);
        transactionList.setAdapter(new TransactionListAdapter());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        transactionObserver = new ContentObserver(transactionHandler) {
            @Override
            public void onChange(boolean selfChange) {
                Message msg = new Message();
                msg.what = TRANSACTION_CHANGE;
                transactionHandler.sendMessage(msg);
            }
        };
        activity.getContentResolver().registerContentObserver(Transaction.CONTENT_URI, true, transactionObserver);

    }

    @Override
    public void onPause() {
        super.onPause();
        activity.getContentResolver().unregisterContentObserver(transactionObserver);
    }


    private static class ViewHolder {
        TextView transaction_title;
        TextView transaction_description;
        TextView transaction_amount;
        TextView transaction_date;
        ImageView image;
    }


    class TransactionListAdapter extends BaseAdapter {

        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        @Override
        public int getCount() {
            return transactionCursor.getCount();
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
                view = inflater.inflate(R.layout.transaction_list_item, parent, false);
                holder = new ViewHolder();
                holder.transaction_title = (TextView) view.findViewById(R.id.transaction_title);
                holder.transaction_amount = (TextView) view.findViewById(R.id.transaction_amount);
                holder.transaction_description = (TextView) view.findViewById(R.id.transaction_description);
                holder.transaction_date = (TextView) view.findViewById(R.id.transaction_date);
                holder.image = (ImageView) view.findViewById(R.id.transaction_image);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            transactionCursor.moveToPosition(position);
            String transfer_title = transactionCursor.getString(transactionCursor.getColumnIndex(Offer.ORGANIZATION_NAME));
            if (transfer_title == null) transfer_title = "Transfer";
            holder.transaction_title.setText("" + transfer_title);
            holder.transaction_description.setText("" + transactionCursor.getString(transactionCursor.getColumnIndex(Transaction.DESCRIPTION)));
            double amount = transactionCursor.getDouble(transactionCursor.getColumnIndex(Transaction.AMOUNT));
            holder.transaction_amount.setText((amount > 0 ? "Credit: " : "Debit: " ) + amount);
            String date = transactionCursor.getString(transactionCursor.getColumnIndex(Transaction.DATE));
            holder.transaction_date.setText("" + date);

            String symbol = transactionCursor.getString(transactionCursor.getColumnIndex(Offer.STOCK_SYMBOL));

            String imageUrl;
            if (symbol == null) {
                imageUrl = "drawable://" + R.drawable.csop;
            } else {
                imageUrl = getString(R.string.api_baseurl) + "css/images/logos/" + symbol + ".png";
            }

            imageLoader.displayImage(imageUrl, holder.image, options, animateFirstListener);

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
