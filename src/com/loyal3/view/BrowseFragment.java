package com.loyal3.view;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.loyal3.R;
import com.loyal3.activity.BuyStockActivity;
import com.loyal3.activity.MainActivity;
import com.loyal3.model.Offer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/3/14
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class BrowseFragment extends Fragment {

    private List<String> offerIds;
    private List<String> imageUrls;

    private DisplayImageOptions options;

    private FragmentActivity activity;

    private  LayoutInflater inflater;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_browse, container, false);
        activity = getActivity();

        loadImageUrls();
        //imageUrls = bundle.getStringArray(IMAGES);

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
//                .showImageOnFail(R.drawable.csop)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        this.inflater = inflater;
        GridView gridView = (GridView) rootView.findViewById(R.id.browse_gridview);
        gridView.setAdapter(new ImageAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity) activity).startBuyActivity(offerIds.get(i));
            }
        });

        return rootView;
    }

    private void loadImageUrls() {
        Cursor c = activity.getContentResolver().query(Offer.CONTENT_URI,
            new String[] {Offer.OFFER_ID, Offer.STOCK_SYMBOL}, Offer.OFFER_TYPE + " = ?", new String[]{Offer.OfferType.DIRECT.type}, Offer.OFFER_NAME + " ASC");
        offerIds = new ArrayList<String>();
        imageUrls = new ArrayList<String>();
        if (c.moveToFirst()) {
            do {
                offerIds.add(c.getString(c.getColumnIndex(Offer.OFFER_ID)));
                imageUrls.add(getString(R.string.api_baseurl) + "css/images/logos/" + c.getString(c.getColumnIndex(Offer.STOCK_SYMBOL)) + ".png");
            } while(c.moveToNext());
        }
        c.close();
    }

    static class ViewHolder {
        ImageView imageView;
    }

    public class ImageAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.browse_stock_image, parent, false);
                holder = new ViewHolder();
                assert view != null;
                holder.imageView = (ImageView) view.findViewById(R.id.imgBrowseStock);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            imageLoader.displayImage(imageUrls.get(position), holder.imageView, options, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                                    FailReason failReason) {
                            for (int i = 0; i < imageUrls.size(); i++) {
                                if (imageUrls.get(i).equals(imageUri)) {
                                    imageUrls.set(i, "drawable://" + R.drawable.csop);
                                }
                            }
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current,
                                                     int total) {
                        }
                    }
            );

            return view;
        }
    }
}
