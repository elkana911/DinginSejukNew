package com.elkana.dslibrary.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.elkana.dslibrary.R;
import com.elkana.dslibrary.pojo.Banner;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MyBannerAdapter extends PagerAdapter {
    private List<Banner> images;
    private LayoutInflater inflater;
    private Context mContext;

    private OkHttpClient client;

    public MyBannerAdapter(Context context, List<Banner> images) {
        this.mContext = context;
        this.images=images;
        inflater = LayoutInflater.from(context);

        client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View v = inflater.inflate(R.layout.banner_slide, view, false);
        ImageView ivBanner = v.findViewById(R.id.ivBanner);

        Picasso pic = new Picasso.Builder(mContext)
                .downloader(new OkHttp3Downloader(client))
                .build();
        pic.setIndicatorsEnabled(true);

        String url = images.get(position).getUrl();

        Uri uri =  Uri.parse( url );
//        Uri uri =  Uri.parse( "http://www.facebook.com" );

        pic.load(uri).into(ivBanner, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                Toast.makeText(mContext, "Error loading banner ", Toast.LENGTH_SHORT).show();
            }
        });

        view.addView(v, 0);

        return v;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public void updateList(List<Banner> newList) {
        images.clear();
        images.addAll(newList);
        notifyDataSetChanged();
    }
}
