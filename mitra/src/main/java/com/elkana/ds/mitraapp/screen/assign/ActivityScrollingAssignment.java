package com.elkana.ds.mitraapp.screen.assign;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.elkana.ds.mitraapp.AFirebaseMitraActivity;
import com.elkana.ds.mitraapp.R;
import com.elkana.ds.mitraapp.util.DataUtil;
import com.elkana.dslibrary.activity.FirebaseActivity;
import com.elkana.dslibrary.component.RealmSearchView;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerGetOrder;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.pojo.OrderBucket;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.mitra.TechnicianReg;
import com.elkana.dslibrary.util.Util;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import io.realm.Realm;

public class ActivityScrollingAssignment extends AFirebaseMitraActivity{

    private static final String TAG = "Assignment";

    public static final String PARAM_ORDER_ID = "order.id";
    public static final String PARAM_CUSTOMER_ID = "customer.id";
    public static final String PARAM_CUSTOMER_NAME = "customer.name";

    String mOrderId, mCustomerId, mCustomerName, mMitraId;

    private RSVAdapterTechnicianReg mAdapter;
    private RealmSearchView search_view;

    TextView tvOrderId, tvCustomerName, tvCustomerAddress, tvOrderDate;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        final AlertDialog dialog = Util.showProgressDialog(this);

        FBUtil.Order_getPending(mCustomerId, mOrderId, new ListenerGetOrder() {
            @Override
            public void onGetData(OrderHeader orderHeader, OrderBucket orderBucket) {
                dialog.dismiss();
                tvCustomerAddress.setText(getString(R.string.label_customer_address, orderHeader.getAddressByGoogle()));
                tvCustomerName.setText(orderHeader.getCustomerName());
                tvOrderDate.setText(getString(R.string.label_order_date, Util.convertDateToString(new Date(orderHeader.getTimestamp()), "dd MMM yyyy HH:mm"))
                        + (Util.isExpiredOrder(orderHeader) ? " (EXPIRED)" : "")
                );
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_assign_tech);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(userFullName);
//            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
//            getSupportActionBar().setTitle(TAG);

//            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(mobileSetup.getTheme_color_default())));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                window.setStatusBarColor(Color.parseColor(mobileSetup.getTheme_color_default()));
            }

        }

        mOrderId = getIntent().getStringExtra(PARAM_ORDER_ID);
        mCustomerId = getIntent().getStringExtra(PARAM_CUSTOMER_ID);
        mCustomerName = getIntent().getStringExtra(PARAM_CUSTOMER_NAME);
        mMitraId = mAuth.getCurrentUser().getUid();

        if (Util.TESTING_MODE && mOrderId == null) {
            mOrderId = "-L-UGt2NFAsIOq2I5n5c";
            mCustomerId = "4AAwmPGueYNiKhJuw2rFlDEYAqD2";
            mCustomerName = "Eric Elkana";
            mMitraId = "lzTott4xLsQcVQzwrOVDKaJzt7l1";
        }

//        toolbar.setTitle(null);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        tvOrderId = findViewById(R.id.tvOrderId);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvCustomerAddress = findViewById(R.id.tvAddress);

        tvOrderId.setText(mOrderId);
        tvCustomerName.setText(mCustomerName);

        mAdapter = new RSVAdapterTechnicianReg(this, realm, "name", new ListenerTechnicianList() {
            @Override
            public void onItemSelected(final TechnicianReg obj) {

//                if (expired)
//                    return;

                Util.showDialogConfirmation(ActivityScrollingAssignment.this, "Assignment", "Tugaskan " + obj.getName().toUpperCase() + " ?", new ListenerPositiveConfirmation() {
                    @Override
                    public void onPositive() {

                        final AlertDialog dialog = Util.showProgressDialog(ActivityScrollingAssignment.this);

                        //TODO: should check DataUtil.isExpiredOrder(obj)
                        // build assignment here
                        Assignment_create(obj.getTechId(), obj.getName(), mCustomerId, mOrderId, new ListenerModifyData() {
                            @Override
                            public void onSuccess() {
                                dialog.dismiss();

                                Intent data = new Intent();
                                data.putExtra("technician.id", obj.getTechId());
                                setResult(RESULT_OK, data);
                                finish();

                            }

                            @Override
                            public void onError(Exception e) {
                                dialog.dismiss();
                            }
                        });

                    }
                });

            }
        });

        search_view = findViewById(R.id.search_view);
        search_view.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final AlertDialog dialog = Util.showProgressDialog(this, "Getting Technicians");

        // get all registered technicians
        DatabaseReference ref = database.getReference(FBUtil.REF_MITRA_AC)
                .child(mMitraId)
                .child("technicians");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dialog.dismiss();

                if (!dataSnapshot.exists())
                    return;

                Realm r = Realm.getDefaultInstance();
                try{
                    r.beginTransaction();
                    String todayYYYYMMDD = Util.convertDateToString(new Date(), "yyyyMMdd");

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        TechnicianReg _obj = postSnapshot.getValue(TechnicianReg.class);

                        // actually there's a node called jobs inside getChildren to get ordertodaycount
                        /*
                            value = {suspend=false, joinDate=1512225232276, jobs={20171203={1512343838050=-L-UGt2NFAsIOq2I5n5c}}, techId=i3p56IZtWHQYPBLxAIYlxEt5aKu1, name=Lia} }

                         */
                        long orderTodayCount = postSnapshot.child("jobs").child(todayYYYYMMDD).getChildrenCount();
                        _obj.setOrderTodayCount((int)orderTodayCount);

                        Log.e(TAG, _obj.toString());

                        r.copyToRealmOrUpdate(_obj);
                    }

                    r.commitTransaction();
                    mAdapter.notifyDataSetChanged();
                }finally {
                    r.close();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();
                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
            }
        });


    }

}
