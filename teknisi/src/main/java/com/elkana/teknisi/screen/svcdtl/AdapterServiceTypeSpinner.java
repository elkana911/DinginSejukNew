package com.elkana.teknisi.screen.svcdtl;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.pojo.mitra.ServiceType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdapterServiceTypeSpinner extends ArrayAdapter<ServiceType> {
    private final String TAG = AdapterServiceTypeSpinner.class.getSimpleName();

    private Context mContext;
    private String mMitraId;
    private List<ServiceType> mList = new ArrayList<>();

    public AdapterServiceTypeSpinner(Context context, int resource, String mitraId) {
        super(context, resource);
        mContext = context;
        mMitraId = mitraId;

        FBUtil.Mitra_GetServicesRef(mMitraId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            ServiceType obj = postSnapshot.getValue(ServiceType.class);

                            mList.add(obj);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                    }
                });
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public ServiceType getItem(int position) {
        return mList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = new TextView(mContext);
//            TextView tv = (TextView) convertView.findViewById(R.id.nama);
        tv.setPadding(10,20,10,20);
        tv.setTextColor(Color.BLACK);
        tv.setText(mList.get(position).getServiceTypeNameBahasa());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        tv.setGravity(Gravity.CENTER);

        return tv;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(mContext);
//            label.setTextColor(Color.BLACK);
        label.setText(mList.get(position).getServiceTypeNameBahasa());
        label.setPadding(10, 20, 10, 20);
        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        return label;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
