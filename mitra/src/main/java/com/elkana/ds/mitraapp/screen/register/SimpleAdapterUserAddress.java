package com.elkana.ds.mitraapp.screen.register;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.elkana.dslibrary.pojo.user.UserAddress;
import com.elkana.dslibrary.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Eric on 27-Oct-17.
 */

public class SimpleAdapterUserAddress extends ArrayAdapter<UserAddress> {

    private static final String TAG = SimpleAdapterUserAddress.class.getSimpleName();

    private String sRefAddress;
    private DatabaseReference addressRef;
    private String mUserId;
    private List<UserAddress> mList = new ArrayList<>();

    private ValueEventListener mValueEventListener;

    public SimpleAdapterUserAddress(@NonNull Context context, int resource, String userId) {
        super(context, resource);
        mUserId = userId;

        getDataLocal();

        if (mUserId == null)
            return;

        sRefAddress = "users/" + mUserId + "/address";
        addressRef = FirebaseDatabase.getInstance().getReference(sRefAddress);
//        addressRef = FirebaseDatabase.getInstance().getReference().child("users").child(mUserId).child("address");

        ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    return;

                if (Util.TESTING_MODE)
                    Log.e(TAG, "valueEventListener.DataChange for " + sRefAddress);

                final Realm r = Realm.getDefaultInstance();
                try {
                    r.beginTransaction();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        UserAddress _obj = postSnapshot.getValue(UserAddress.class);
                        Log.e(TAG, _obj.toString());

                        r.copyToRealmOrUpdate(_obj);
                    }

                    r.commitTransaction();
                } finally {
                    r.close();
                }
/*
                GenericTypeIndicator<ArrayList<UserAddress>> t = new GenericTypeIndicator<ArrayList<UserAddress>>() {};
                final List<UserAddress> list = dataSnapshot.getValue(t);

                final Realm r = Realm.getDefaultInstance();
                try {
                    r.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            for (UserAddress addr : list) {
                                realm.copyToRealmOrUpdate(addr);
                            }
                        }
                    });

                } finally {
                    r.close();
                }
*/
                getDataLocal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
            }
        };

        mValueEventListener = valueEventListener;
        addressRef.addValueEventListener(mValueEventListener);
    }

    public void cleanUpListener() {
        if (mValueEventListener != null && addressRef != null)
            addressRef.removeEventListener(mValueEventListener);
    }

    private void getDataLocal() {
        mList.clear();

        Realm r = Realm.getDefaultInstance();

        try {

//            User user = r.where(User.class).equalTo("uid", mUserId).findFirst();
//
//            if (user != null) {
            mList.addAll(r.copyFromRealm(r.where(UserAddress.class).findAll()));
            notifyDataSetChanged();
//            }
        } finally {
            r.close();
        }

//        if (mListener != null) {
//            mListener.onPostLoadingData();
//        }

    }

    @Nullable
    @Override
    public UserAddress getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView tv = new TextView(getContext());
//            TextView tv = (TextView) convertView.findViewById(R.id.nama);
        tv.setTextColor(Color.BLACK);
        tv.setText(mList.get(position).getLabel());
//            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

        return tv;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = new TextView(getContext());
//            label.setTextColor(Color.BLACK);
        label.setText(mList.get(position).getLabel());
        label.setPadding(10, 20, 10, 20);
//            label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        return label;
    }
}
