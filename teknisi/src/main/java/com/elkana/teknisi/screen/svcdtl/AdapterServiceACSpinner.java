package com.elkana.teknisi.screen.svcdtl;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.elkana.teknisi.pojo.ServiceToParty;

import java.util.List;

/**
 * Created by Eric on 14-Nov-17.
 */

public class AdapterServiceACSpinner extends ArrayAdapter<ServiceToParty> {
    private Context mContext;
    private List<ServiceToParty> mList;

    public AdapterServiceACSpinner(Context context, int resource, List<ServiceToParty> list) {
        super(context, resource, list);
        mContext = context;
        mList = list;
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public ServiceToParty getItem(int position) {
        return mList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = new TextView(mContext);
//            TextView tv = (TextView) convertView.findViewById(R.id.nama);
        tv.setPadding(10,20,10,20);
        tv.setTextColor(Color.BLACK);
        tv.setText(mList.get(position).getServiceLabel());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        tv.setGravity(Gravity.CENTER);

        return tv;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(mContext);
//            label.setTextColor(Color.BLACK);
        label.setText(mList.get(position).getServiceLabel());
        label.setPadding(10, 20, 10, 20);
        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        return label;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
