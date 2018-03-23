package com.elkana.teknisi.screen.payment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.elkana.dslibrary.activity.FirebaseActivity;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.AFirebaseTeknisiActivity;
import com.elkana.teknisi.R;
import com.google.firebase.auth.FirebaseUser;

public class ActivityPayment extends AFirebaseTeknisiActivity {

    public static final String PARAM_ASSIGNMENT_ID = "assignment.id";
    public static final String PARAM_TECHNICIAN_ID = "tech.id";
    public static final String PARAM_MITRA_ID = "mitra.id";
    public static final String PARAM_CUSTOMER_ID = "customer.id";
    public static final String PARAM_ORDER_ID = "order.id";

    TextView tvTotalFare;
    RecyclerView rvACItems;
    private RVAdapterPayment mAdapter;
    private String mAssignmentId, mTechnicianId, mMitraId, mCustomerId, mOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(userFullName);
//            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_activity_payment));
        }

        mAssignmentId = getIntent().getStringExtra(PARAM_ASSIGNMENT_ID);
        mTechnicianId = getIntent().getStringExtra(PARAM_TECHNICIAN_ID);
        mMitraId = getIntent().getStringExtra(PARAM_MITRA_ID);
        mCustomerId = getIntent().getStringExtra(PARAM_CUSTOMER_ID);
        mOrderId = getIntent().getStringExtra(PARAM_ORDER_ID);

        if (Util.TESTING_MODE && mTechnicianId == null) {
            mTechnicianId = "YFh65qe1BSPMJyhS8KkIrtPUYR32";
            mAssignmentId = "assignmentIdAbc123";
            mMitraId = "lzTott4xLsQcVQzwrOVDKaJzt7l1";
            mCustomerId = "XqpLURQFD6UKee1xg5liPf52zlm2";
            mOrderId = "-L-zga_oKk_elVlcEgJ3";
        }

        tvTotalFare = findViewById(R.id.tvTotalFare);

        Button btnLunas = findViewById(R.id.btnLunas);
        btnLunas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.showDialogConfirmation(ActivityPayment.this, "Konfirmasi Lunas", "Transaksi selesai ?", new ListenerPositiveConfirmation() {
                    @Override
                    public void onPositive() {

                        final AlertDialog dialog = Util.showProgressDialog(ActivityPayment.this);

                        final EOrderDetailStatus newStatus = EOrderDetailStatus.PAID;

                        Assignment_setStatus(mMitraId, mTechnicianId, mAssignmentId, mCustomerId, mOrderId, newStatus, new ListenerModifyData() {
                            @Override
                            public void onSuccess() {
                                dialog.dismiss();

                                Intent data = new Intent();
                                data.putExtra("statusDetailId", newStatus.name());
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

        mAdapter = new RVAdapterPayment(this, mTechnicianId, mAssignmentId, new ListenerPaymentList() {
            @Override
            public void onCalculateTotalFare(final long sum) {

                final AlertDialog alertDialog = Util.showProgressDialog(ActivityPayment.this);
                // update orderHeader
                FBUtil.Order_updateValue(mCustomerId, mOrderId,"pleasePayAmount", sum, new ListenerModifyData(){

                    @Override
                    public void onSuccess() {
                        alertDialog.dismiss();

                        tvTotalFare.setText("Total : " + Util.convertLongToRupiah(sum));
                    }

                    @Override
                    public void onError(Exception e) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        rvACItems = findViewById(R.id.rvACItems);
        rvACItems.setLayoutManager(new LinearLayoutManager(this));
        rvACItems.setAdapter(mAdapter);

    }

}
