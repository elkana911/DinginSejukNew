package com.elkana.teknisi.screen.svcdtl;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.pojo.mitra.Assignment;
import com.elkana.dslibrary.pojo.technician.ServiceItem;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.AFirebaseTeknisiActivity;
import com.elkana.teknisi.R;
import com.elkana.teknisi.pojo.IsiDataAC;
import com.elkana.teknisi.screen.dataac.ActivityDataAC;
import com.elkana.teknisi.util.TeknisiUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityServiceDetail extends AFirebaseTeknisiActivity {
    private static final String TAG = ActivityServiceDetail.class.getSimpleName();

    public static final String PARAM_ASSIGNMENT_ID = "assignment.id";
    public static final String PARAM_TECHNICIAN_ID = "tech.id";
    public static final String PARAM_CUSTOMER_ID = "customer.id";
    //    public static final String PARAM_CUSTOMER_NAME = "customer.name";
//    public static final String PARAM_CUSTOMER_ADDRESS = "customer.address";
//    public static final String PARAM_STARTSERVICE_TIME = "dateOfService";
    public static final String PARAM_MITRA_ID = "mitra.id";
    public static final String PARAM_ORDER_ID = "order.id";
    public static final String PARAM_SERVICE_TYPE = "order.serviceType";

    //    public static final String PARAM_LATITUDE_ID = "customer.latitude";

//    public static final String PARAM_LONGITUDE_ID = "customer.longitude";
    private static final int REQUESTCODE_SCREEN_DATA_AC = 33;


//    private List<ServiceItem> mList = new ArrayList<>();

//    private ListView lvACItems;
    private RecyclerView rvACItems;
    private RVAdapterServiceDtl mAdapter;
//    private RealmSearchView search_view;

//    FloatingActionButton fabAddService;
    Button btnGo2Payment;
    TextView tvTotalFare, tvCustomerName, tvCustomerAddress, tvStartTime;

    private String mTechnicianId, mAssignmentId, mOrderId, mCustomerId, mCustomerAddress, mCustomerName, mMitraId, mStartTime;//, mLatitude, mLongitude;
    private int mServiceType;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // next coba pindah ke onStart
        final AlertDialog dialog = Util.showProgressDialog(this, "Check Orders...");

        // 13 mar 18 lupa knp utk ngisi mOrderId dll ga based on param di oncreate aja ? soalnya sempet forceclose wkt activity ini dipanggil
        mDatabase.getReference(FBUtil.REF_ASSIGNMENTS_PENDING)
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
//            getSupportActionBar().setTitle(getString(R.string.title_activity_servicedtl));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(mobileSetup.getTheme_color_default())));
        }

        mAssignmentId = getIntent().getStringExtra(PARAM_ASSIGNMENT_ID);
        mTechnicianId = getIntent().getStringExtra(PARAM_TECHNICIAN_ID);
//        mPartyId = getIntent().getStringExtra(PARAM_MITRA_ID);
        mMitraId = getIntent().getStringExtra(PARAM_MITRA_ID);
        mServiceType = getIntent().getIntExtra(PARAM_SERVICE_TYPE, Const.SERVICE_TYPE_SCHEDULED);

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

        /*
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
            }
        });
        */

        btnGo2Payment = findViewById(R.id.btnGo2Payment);
        btnGo2Payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final List<ServiceItem> list = mAdapter.getList();


                Util.showDialogConfirmation(ActivityServiceDetail.this, null, getString(R.string.confirm_start_payment), new ListenerPositiveConfirmation() {
                    @Override
                    public void onPositive() {

                        final AlertDialog dialog = Util.showProgressDialog(ActivityServiceDetail.this);

                        TeknisiUtil.Assignment_addServiceItems(mTechnicianId, mAssignmentId, list, new ListenerModifyData(){
                            @Override
                            public void onSuccess() {
                                FBUtil.Order_SetStatus(mMitraId, mCustomerId, mOrderId, mAssignmentId,mTechnicianId, EOrderDetailStatus.PAYMENT, String.valueOf(Const.USER_AS_TECHNICIAN), new ListenerModifyData() {
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

        mAdapter = new RVAdapterServiceDtl(this, mMitraId, mAssignmentId, new ListenerServiceDetail() {

            @Override
            public void onAddServiceDetail() {
//                ServiceItem item = new ServiceItem();
//                item.setUid(new Date().getTime());
            }

            @Override
            public void onDeleteItem(ServiceItem obj, int position) {
                realm.beginTransaction();
                IsiDataAC first = realm.where(IsiDataAC.class).equalTo("uid", obj.getUid()).findFirst();
                if (first != null)
                    first.deleteFromRealm();
                realm.commitTransaction();
            }

            @Override
            public void onAddDataAC(ServiceItem obj) {
                Intent intent = new Intent(ActivityServiceDetail.this, ActivityDataAC.class);
                intent.putExtra(ActivityDataAC.PARAM_DATAAC_UID, obj.getUid());
                intent.putExtra(ActivityDataAC.PARAM_ASSIGNMENT_ID, mAssignmentId);
                intent.putExtra(ActivityDataAC.PARAM_TECHNICIAN_ID, mTechnicianId);

                startActivityForResult(intent, REQUESTCODE_SCREEN_DATA_AC);
            }

            @Override
            public void onPrepareList(List<ServiceItem> mList) {

                if (mServiceType == Const.SERVICE_TYPE_QUICK) {
                    ServiceItem item = new ServiceItem();
                    item.setUid(new Date().getTime());
                    item.setUidNegative(item.getUid() * -1);
                    item.setCount(1);
                    item.setAssignmentId(mAssignmentId);
                    item.setPromoCode(null);

                    item.setServiceTypeId(Const.SERVICE_TYPE_QUICK_SERVICE_CHARGE);
                    item.setServiceLabel(Const.SERVICE_TYPE_QUICK_SERVICE_CHARGE_LABEL);
                    item.setRate(25000);

                    mList.add(item);
                }

                // cleanup isidata table
                Realm r = Realm.getDefaultInstance();
                try{
                    r.beginTransaction();
                    RealmResults<IsiDataAC> all = r.where(IsiDataAC.class).findAll();

                    if (all.size() > 0)
                        all.deleteAllFromRealm();

                    r.commitTransaction();
                }finally {
                    r.close();
                }
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode != REQUESTCODE_SCREEN_DATA_AC)
            return;

        // TODO: do something to respond isi data ac
    }
}
