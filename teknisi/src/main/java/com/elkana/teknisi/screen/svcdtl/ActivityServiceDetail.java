package com.elkana.teknisi.screen.svcdtl;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.elkana.dslibrary.activity.FirebaseActivity;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.pojo.mitra.Assignment;
import com.elkana.dslibrary.pojo.technician.ServiceItem;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.R;
import com.elkana.teknisi.screen.dataac.ActivityDataAC;
import com.elkana.teknisi.screen.payment.ActivityPayment;
import com.elkana.teknisi.util.DataUtil;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.List;

public class ActivityServiceDetail extends FirebaseActivity {
    private static final String TAG = ActivityServiceDetail.class.getSimpleName();

    public static final String PARAM_ASSIGNMENT_ID = "assignment.id";
    public static final String PARAM_TECHNICIAN_ID = "tech.id";
    public static final String PARAM_CUSTOMER_ID = "customer.id";
//    public static final String PARAM_CUSTOMER_NAME = "customer.name";
//    public static final String PARAM_CUSTOMER_ADDRESS = "customer.address";
//    public static final String PARAM_STARTSERVICE_TIME = "dateOfService";
    public static final String PARAM_MITRA_ID = "mitra.id";
    public static final String PARAM_ORDER_ID = "order.id";

//    public static final String PARAM_LATITUDE_ID = "customer.latitude";
//    public static final String PARAM_LONGITUDE_ID = "customer.longitude";

    private static final int REQUESTCODE_SCREEN_DATA_AC = 33;


//    private List<ServiceItem> mList = new ArrayList<>();

//    private ListView lvACItems;
    private RecyclerView rvACItems;
    private RVAdapterServiceDetail mAdapter;
//    private RVAdapterSvcDtl mAdapter;
//    private RealmSearchView search_view;

    FloatingActionButton fabAddService;
    Button btnGo2Payment;
    TextView tvTotalFare, tvCustomerName, tvCustomerAddress, tvStartTime;

    private String mTechnicianId, mAssignmentId, mOrderId, mCustomerId, mCustomerAddress, mCustomerName, mMitraId, mStartTime;//, mLatitude, mLongitude;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // next coba pindah ke onStart
        final AlertDialog dialog = Util.showProgressDialog(this, "Check Orders...");

        // 13 mar 18 lupa knp utk ngisi mOrderId dll ga based on param di oncreate aja ? soalnya sempet forceclose wkt activity ini dipanggil
        database.getReference(DataUtil.REF_ASSIGNMENTS_PENDING)
                .child(mTechnicianId)
                .child(mAssignmentId)
                .child("assign").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialog.dismiss();

                if (!dataSnapshot.exists())
                    return;

                Assignment _assignment = dataSnapshot.getValue(Assignment.class);

                mCustomerId = _assignment.getCustomerId();
                mOrderId = _assignment.getOrderId();
                mCustomerName = _assignment.getCustomerName();
                mCustomerAddress = _assignment.getCustomerAddress();

                if (_assignment.getStartDate() <1) {
                    mStartTime = Util.convertDateToString(new Date(), "dd MMM yyyy HH:mm");
                } else
                    mStartTime = Util.convertDateToString(new Date(_assignment.getStartDate()), "dd MMM yyyy HH:mm");

                tvStartTime.setText("Waktu Mulai: " + mStartTime);
                tvCustomerName.setText("Customer: " + mCustomerName);
                tvCustomerAddress.setText("Alamat: " + mCustomerAddress);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();
                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvTotalFare = findViewById(R.id.tvTotalFare);
        tvCustomerAddress = findViewById(R.id.tvAddress);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvStartTime = findViewById(R.id.tvStartTime);

        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(userFullName);
//            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_activity_servicedtl));
        }

        mAssignmentId = getIntent().getStringExtra(PARAM_ASSIGNMENT_ID);
        mTechnicianId = getIntent().getStringExtra(PARAM_TECHNICIAN_ID);
//        mPartyId = getIntent().getStringExtra(PARAM_MITRA_ID);
        mMitraId = getIntent().getStringExtra(PARAM_MITRA_ID);

//        mCustomerId = getIntent().getStringExtra(PARAM_CUSTOMER_ID);
//        mOrderId = getIntent().getStringExtra(PARAM_ORDER_ID);
//        mCustomerName = getIntent().getStringExtra(PARAM_CUSTOMER_NAME);
//        mCustomerAddress = getIntent().getStringExtra(PARAM_CUSTOMER_ADDRESS);
//        mStartTime = getIntent().getStringExtra(PARAM_STARTSERVICE_TIME);

        if (Util.TESTING_MODE && mTechnicianId == null) {
            mTechnicianId = "YFh65qe1BSPMJyhS8KkIrtPUYR32";
            mAssignmentId = "assignmentIdAbc123";
//            mPartyId = "3";
//            mCustomerId = "2Chlu5e44Ig95SkjQxVgGbVvysk2";
            mMitraId = "-Kyhkq-AC9RrqNQAi4vb";
//            mCustomerName = "Eric Elkana Tarigan";
//            mCustomerAddress = "gii bsd Isuzu";
//            mStartTime = "27 Nov 2017 12:30";
//            mLatitude = "-6.275038065354676";
//            mLongitude = "106.65709599852562";
//            mCustomerAddress = "gii bsg isuzu";
        }

        fabAddService = findViewById(R.id.fabAddService);
        fabAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();

                ServiceItem newItem = realm.createObject(ServiceItem.class, new Date().getTime());
                newItem.setUidNegative(Math.abs(newItem.getUid()));
                newItem.setAssignmentId(mAssignmentId);
                newItem.setServiceTypeId(110);
                newItem.setServiceLabel("Cleaning AC");
                newItem.setCount(1);
                realm.copyToRealmOrUpdate(newItem);
                realm.commitTransaction();

                mAdapter.notifyDataSetChanged();
//                search_view.getRealmRecyclerView().invalidate();
            }
        });

        btnGo2Payment = findViewById(R.id.btnGo2Payment);
        btnGo2Payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final List<ServiceItem> list = mAdapter.getList();
                /*
                if (list.size() < 1) {
                    Util.showDialogConfirmation(ActivityServiceDetail.this, null, getString(R.string.confirm_no_charge), new ListenerPositiveConfirmation() {
                        @Override
                        public void onPositive() {
                            Intent data = new Intent();
                            data.putExtra("statusDetailId", EOrderDetailStatus.PAYMENT.name());
                            setResult(RESULT_OK);
                            finish();
                        }
                    });

                    return;

                } */

                Util.showDialogConfirmation(ActivityServiceDetail.this, null, getString(R.string.confirm_start_payment), new ListenerPositiveConfirmation() {
                    @Override
                    public void onPositive() {

                        final AlertDialog dialog = Util.showProgressDialog(ActivityServiceDetail.this);

                        FBUtil.Assignment_addServiceItems(mTechnicianId, mAssignmentId, list, new ListenerModifyData(){
                            @Override
                            public void onSuccess() {
                                FBUtil.Order_SetStatus(mMitraId, mCustomerId, mOrderId, mAssignmentId, EOrderDetailStatus.PAYMENT, String.valueOf(Const.USER_AS_TECHNICIAN), new ListenerModifyData() {
                                    @Override
                                    public void onSuccess() {
                                        dialog.dismiss();

                                        // berhubung akan pindah layar payment, pls see params within ActivityPayment
                                        Intent data = new Intent();
                                        data.putExtra("statusDetailId", EOrderDetailStatus.PAYMENT.name());
                                        data.putExtra(PARAM_ASSIGNMENT_ID, mAssignmentId);
                                        data.putExtra(PARAM_TECHNICIAN_ID, mTechnicianId);
                                        data.putExtra(PARAM_CUSTOMER_ID, mCustomerId);
                                        data.putExtra(PARAM_MITRA_ID, mMitraId);
                                        data.putExtra(PARAM_ORDER_ID, mOrderId);
//                                        data.putExtra(PARAM_ASSIGNMENT_ID, EOrderDetailStatus.PAYMENT.name());
//                                        data.putExtra(PARAM_TECHNICIAN_ID, EOrderDetailStatus.PAYMENT.name());
                                        setResult(RESULT_OK, data);
                                        finish();
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        dialog.dismiss();

                                    }
                                });
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


//        mAdapter = new RVAdapterSvcDtl(this, this.realm, "uid", new ListenerServiceDetail() {
//            @Override
//            public void onAddServiceDetail() {
////                mList.add(item);
////                ServiceItem item = new ServiceItem();
////                item.setUid(new Date().getTime());
////                mAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onDeleteItem(ServiceItem obj, int position) {
//            }
//        });
        mAdapter = new RVAdapterServiceDetail(this, mAssignmentId, mMitraId, new ListenerServiceDetail() {
            @Override
            public void onAddServiceDetail() {
                ServiceItem item = new ServiceItem();
                item.setUid(new Date().getTime());
//                mList.add(item);
//                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDeleteItem(ServiceItem obj, int position) {
            }

            @Override
            public void onAddDataAC() {
                Intent intent = new Intent(ActivityServiceDetail.this, ActivityDataAC.class);
                startActivityForResult(intent, REQUESTCODE_SCREEN_DATA_AC);
            }
        });

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                double sum = 0;
                for (ServiceItem item : mAdapter.getList()) {
                    sum += new Double(item.getCount()) * item.getRate();
                }
                tvTotalFare.setText("Total : " + Util.convertLongToRupiah(new Double(sum).longValue()));
                super.onChanged();
            }
        });

        rvACItems = findViewById(R.id.rvACItems);
        rvACItems.setLayoutManager(new LinearLayoutManager(this));
        rvACItems.setAdapter(mAdapter);
//        ListAdapter listAdapter;

//        search_view = findViewById(R.id.search_view);
//        search_view.getSearchBar().setVisibility(View.GONE);
//        search_view.setAdapter(mAdapter);
    }

    @Override
    protected void onLoggedOff() {

    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

//        String orderStatus = data.getStringExtra("statusDetailId");

        if (requestCode != REQUESTCODE_SCREEN_DATA_AC)
            return;

        // TODO: do something to respond isi data ac
    }
}
