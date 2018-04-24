package com.elkana.ds.mitraapp.screen.assign;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.elkana.ds.mitraapp.AFirebaseMitraActivity;
import com.elkana.ds.mitraapp.R;
import com.elkana.ds.mitraapp.util.MitraUtil;
import com.elkana.dslibrary.component.RealmSearchView;
import com.elkana.dslibrary.firebase.FBFunction_BasicCallableRecord;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerGetOrder;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.pojo.OrderBucket;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.mitra.TechnicianReg;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.util.DateUtil;
import com.elkana.dslibrary.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

public class ActivityScrollingAssignment extends AFirebaseMitraActivity{

    private static final String TAG = "Assignment";

    public static final String PARAM_ORDER_ID = "order.id";
    public static final String PARAM_CUSTOMER_ID = "customer.id";
    public static final String PARAM_CUSTOMER_NAME = "customer.name";

    String mOrderId, mCustomerId, mCustomerName, mMitraId;

    boolean mServiceTimeFree;
    String mDateOfService;
    String mTimeOfService;
    long mServiceTimestamp;

    private RSVAdapterTechnicianReg mAdapter;
    private RealmSearchView search_view;

    TextView tvOrderId, tvCustomerName, tvCustomerAddress, tvOrderDate;
    Button btnPickTime;

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

                mDateOfService = orderHeader.getDateOfService();
                mTimeOfService = orderHeader.getTimeOfService();
                mServiceTimestamp = orderHeader.getServiceTimestamp();
                mServiceTimeFree = orderHeader.isServiceTimeFree();

                if (orderHeader.isServiceTimeFree() && orderHeader.getTimeOfService().equals("99:99")) {
                    tvOrderDate.setText(getString(R.string.label_order_date, Util.prettyDate(ActivityScrollingAssignment.this, Util.convertStringToDate(orderHeader.getDateOfService(), "yyyyMMdd"), true))
                            + (MitraUtil.isExpiredBooking(orderHeader) ? " (EXPIRED)" : "")
                    );

                    btnPickTime.setVisibility(View.VISIBLE);
                }else{
                    tvOrderDate.setText(getString(R.string.label_order_date, DateUtil.displayTimeInJakarta(orderHeader.getServiceTimestamp(), "dd MMM yyyy HH:mm"))
                            + (MitraUtil.isExpiredBooking(orderHeader) ? " (EXPIRED)" : ""));

                    btnPickTime.setVisibility(View.GONE);
                }
//                tvOrderDate.setText(getString(R.string.label_order_date, Util.convertDateToString(new Date(orderHeader.getServiceTimestamp()), "dd MMM yyyy HH:mm"))
//                        + (MitraUtil.isExpiredBooking(orderHeader) ? " (EXPIRED)" : "")
//                );

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

            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(mobileSetup.getTheme_color_default())));

        }

        mOrderId = getIntent().getStringExtra(PARAM_ORDER_ID);
        mCustomerId = getIntent().getStringExtra(PARAM_CUSTOMER_ID);
        mCustomerName = getIntent().getStringExtra(PARAM_CUSTOMER_NAME);
        mMitraId = mAuth.getCurrentUser().getUid();

//        if (Util.TESTING_MODE && mOrderId == null) {
//            mOrderId = "-L-UGt2NFAsIOq2I5n5c";
//            mCustomerId = "4AAwmPGueYNiKhJuw2rFlDEYAqD2";
//            mCustomerName = "Eric Elkana";
//            mMitraId = "lzTott4xLsQcVQzwrOVDKaJzt7l1";
//        }

//        toolbar.setTitle(null);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btnPickTime = findViewById(R.id.btnPickTime);
        btnPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // filter waktu buka berdasarkan hari service
                BasicInfo basicInfo1 = realm.where(BasicInfo.class).findFirst();

                int openTime = basicInfo1.getWorkingHourStart();
                int closeTime = basicInfo1.getWorkingHourEnd();
                int offsetHour = 2;
                String nextDayYYYYMMDD = mDateOfService;

                Date now = new Date();
                String today = Util.convertDateToString(now, "yyyyMMdd");
                if (nextDayYYYYMMDD.equals(today)) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(now);

                    int currentHour = c.get(Calendar.HOUR_OF_DAY);
                    if (currentHour > openTime)
                        openTime = currentHour + offsetHour;
                }

                // show working hours of selected mitra
                final String[] time_services = DateUtil.generateWorkingHours(openTime, closeTime, 15);

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ActivityScrollingAssignment.this);
                builder.setTitle("Pilih Jam Pengerjaan");

                builder.setItems(time_services, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String pick = time_services[which];

                        mTimeOfService = pick;
                        // recalculate
                        mServiceTimestamp = DateUtil.compileDateAndTime(mDateOfService, mTimeOfService);
                        tvOrderDate.setText(getString(R.string.label_order_date, DateUtil.displayTimeInJakarta(mServiceTimestamp, "dd MMM yyyy HH:mm")));
                    }
                });

                android.support.v7.app.AlertDialog dialog = builder.create();
                dialog.show();

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

                // TODO: already assigned for same order
//                FBUtil.TechnicianReg_isConflictJob(obj.getTechId(), mOrderId);

                if (mServiceTimeFree) {
                    if (mTimeOfService.equals("99:99")) {
                        Toast.makeText(ActivityScrollingAssignment.this, "Mohon " + btnPickTime.getText().toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Util.showDialogConfirmation(ActivityScrollingAssignment.this, "Manual Assignment", "Tugaskan " + obj.getName().toUpperCase() + " ?"
                        + "\nTgl Service: " + Util.prettyDate(ActivityScrollingAssignment.this, Util.convertStringToDate(mDateOfService, "yyyyMMdd"), true)
                        + "\nJam: " + mTimeOfService
                            , new ListenerPositiveConfirmation() {
                    @Override
                    public void onPositive() {
                        final AlertDialog dialog = Util.showProgressDialog(ActivityScrollingAssignment.this, "Assigning " + obj.getName().toUpperCase());

                        final Map<String, Object> keyVal = new HashMap<>();
                        keyVal.put("mitraId", mMitraId);
                        keyVal.put("techId", obj.getTechId());
                        keyVal.put("techName", obj.getName());
                        keyVal.put("orderId", mOrderId);
                        keyVal.put("custId", mCustomerId);
                        keyVal.put("dateOfService", mDateOfService);
                        keyVal.put("timeOfService", mTimeOfService);
                        keyVal.put("serviceTimeFree", mServiceTimeFree);
                        keyVal.put("serviceTimestamp", mServiceTimestamp);
                        keyVal.put("timestamp", ServerValue.TIMESTAMP);

                        mFunctions.getHttpsCallable(FBUtil.FUNCTION_MANUAL_ASSIGNMENT)
                                .call(keyVal)
                                .continueWith(new FBFunction_BasicCallableRecord())
                                .addOnCompleteListener(new OnCompleteListener<Map<String, Object>>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Map<String, Object>> task) {
                                        dialog.dismiss();

                                        if (!task.isSuccessful()) {
                                            Log.e(TAG, task.getException().getMessage(), task.getException());
                                            Toast.makeText(ActivityScrollingAssignment.this, FBUtil.friendlyTaskNotSuccessfulMessage(task.getException()), Toast.LENGTH_LONG).show();
                                            return;
                                        }

                                        Intent data = new Intent();
                                        data.putExtra("technician.id", obj.getTechId());
                                        setResult(RESULT_OK, data);
                                        finish();

                                    }
                                });
/*
                        // build assignment here
                        Assignment_create(obj.getTechId(), obj.getName(), mCustomerId, mOrderId, new ListenerModifyData() {
                            @Override
                            public void onSuccess() {
                                Intent data = new Intent();
                                data.putExtra("technician.id", obj.getTechId());
                                setResult(RESULT_OK, data);
                                finish();

                            }

                            @Override
                            public void onError(Exception e) {
                            }
                        });
*/
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
        DatabaseReference ref = mDatabase.getReference(FBUtil.REF_MITRA_AC)
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
                        long orderTodayCount = postSnapshot.child("jobs_assigned").child(todayYYYYMMDD).getChildrenCount();
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
