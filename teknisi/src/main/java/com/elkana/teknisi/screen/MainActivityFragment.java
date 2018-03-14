package com.elkana.teknisi.screen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.mitra.Assignment;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.R;
import com.elkana.teknisi.pojo.MobileSetup;
import com.elkana.teknisi.screen.map.MapsActivity;
import com.elkana.teknisi.screen.payment.ActivityPayment;
import com.elkana.teknisi.screen.svcdtl.ActivityServiceDetail;
import com.elkana.teknisi.util.DataUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import io.realm.Realm;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String TAG = MainActivityFragment.class.getSimpleName();
    private static final int REQUESTCODE_MAP = 60;
    private static final int REQUESTCODE_SERVICE_DTL = 61;
    private static final int REQUESTCODE_SERVICE_PAYMENT = 62;

    private View llBlank;
    private RecyclerView rvAssigments;
    private RVAdapterAssignment mAdapter;

    public String technicianId;

    protected OnFragmentAssignmentistInteractionListener mListener;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        llBlank = v.findViewById(R.id.llBlank);
        rvAssigments = v.findViewById(R.id.rvAssigments);
        rvAssigments.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        reInitiate(technicianId);

    }

    @Override
    public void onStop() {
        super.onStop();

        cleanUpListener();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        String orderStatus = data.getStringExtra("statusDetailId");
        EOrderDetailStatus orderDetailStatus = EOrderDetailStatus.convertValue(orderStatus);

        switch (requestCode) {
            case REQUESTCODE_MAP:
//                if (orderDetailStatus == EOrderDetailStatus.OTW) {
                if (orderDetailStatus == EOrderDetailStatus.WORKING) {
                    Intent i = new Intent(getActivity(), ActivityServiceDetail.class);
                    i.putExtra(ActivityServiceDetail.PARAM_ASSIGNMENT_ID, data.getStringExtra(MapsActivity.PARAM_ASSIGNMENT_ID));
                    i.putExtra(ActivityServiceDetail.PARAM_TECHNICIAN_ID, data.getStringExtra(MapsActivity.PARAM_TECHNICIAN_ID));
                    i.putExtra(ActivityServiceDetail.PARAM_MITRA_ID, data.getStringExtra(MapsActivity.PARAM_MITRA_ID));
//                    i.putExtra(ActivityServiceDetail.PARAM_CUSTOMER_ID, data.getStringExtra(MapsActivity.PARAM_CUSTOMER_ID));
//                    i.putExtra(ActivityServiceDetail.PARAM_ORDER_ID, data.getStringExtra(MapsActivity.PARAM_ORDER_ID));

                    startActivityForResult(i, REQUESTCODE_SERVICE_DTL);
//                    startActivity(i);
                }
                break;
            case REQUESTCODE_SERVICE_DTL:
                if (orderDetailStatus == EOrderDetailStatus.PAYMENT) {
                    Intent i = new Intent(getActivity(), ActivityPayment.class);

                    i.putExtra(ActivityPayment.PARAM_ASSIGNMENT_ID, data.getStringExtra(ActivityServiceDetail.PARAM_ASSIGNMENT_ID));
                    i.putExtra(ActivityPayment.PARAM_TECHNICIAN_ID, data.getStringExtra(ActivityServiceDetail.PARAM_TECHNICIAN_ID));
                    i.putExtra(ActivityPayment.PARAM_CUSTOMER_ID, data.getStringExtra(ActivityServiceDetail.PARAM_CUSTOMER_ID));
                    i.putExtra(ActivityPayment.PARAM_MITRA_ID, data.getStringExtra(ActivityServiceDetail.PARAM_MITRA_ID));
                    i.putExtra(ActivityPayment.PARAM_ORDER_ID, data.getStringExtra(ActivityServiceDetail.PARAM_ORDER_ID));
                    startActivityForResult(i, REQUESTCODE_SERVICE_PAYMENT);

                }
                break;
            case REQUESTCODE_SERVICE_PAYMENT:
                //?
                break;
        }
    }

    public void reInitiate(String uid) {
        this.technicianId = uid;

        if (mAdapter != null)
            mAdapter.cleanUpListener();

        if (technicianId == null) {
            llBlank.setVisibility(View.VISIBLE);
            rvAssigments.setVisibility(View.GONE);
            return;
        }

        mAdapter = new RVAdapterAssignment(getContext(), technicianId, new ListenerAssignmentList() {

            @Override
            public void onItemSelected(final Assignment assignment) {

                final AlertDialog dialog = Util.showProgressDialog(getContext(), "Check Status");

                //read the last status of orderid
                final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference(DataUtil.REF_ORDERS_CUSTOMER_AC_PENDING)
                        .child(assignment.getCustomerId())
                        .child(assignment.getOrderId());

                orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dialog.dismiss();

                        if (!dataSnapshot.exists())
                            return;

                        OrderHeader obj = dataSnapshot.getValue(OrderHeader.class);
                        Log.e(TAG, "obj:" + obj.toString());

                        Intent i;
                        switch (EOrderDetailStatus.convertValue(obj.getStatusDetailId())) {
                            case CREATED:
                            case ASSIGNED:
                            case OTW:
                                i = new Intent(getActivity(), MapsActivity.class);
                                i.putExtra(MapsActivity.PARAM_TECHNICIAN_ID, technicianId);
                                i.putExtra(MapsActivity.PARAM_ASSIGNMENT_ID, assignment.getUid());
                                i.putExtra(MapsActivity.PARAM_LATITUDE_ID, obj.getLatitude());
                                i.putExtra(MapsActivity.PARAM_LONGITUDE_ID, obj.getLongitude());
                                i.putExtra(MapsActivity.PARAM_CUSTOMER_ID, obj.getCustomerId());
                                i.putExtra(MapsActivity.PARAM_ADDRESS_ID, obj.getAddressId());
                                i.putExtra(MapsActivity.PARAM_ORDER_ID, obj.getUid());
                                i.putExtra(MapsActivity.PARAM_MITRA_ID, obj.getPartyId());

                                startActivityForResult(i, REQUESTCODE_MAP);
                                break;
                            case WORKING:
                                i = new Intent(getActivity(), ActivityServiceDetail.class);

                                i.putExtra(ActivityServiceDetail.PARAM_ASSIGNMENT_ID, assignment.getUid());
                                i.putExtra(ActivityServiceDetail.PARAM_TECHNICIAN_ID, technicianId);
                                i.putExtra(ActivityServiceDetail.PARAM_MITRA_ID, obj.getPartyId());
                                i.putExtra(ActivityServiceDetail.PARAM_CUSTOMER_ID, obj.getCustomerId());
                                i.putExtra(ActivityServiceDetail.PARAM_ORDER_ID, obj.getUid());
//                                i.putExtra(ActivityServiceDetail.PARAM_CUSTOMER_NAME, obj.getCustomerName());
//                                i.putExtra(ActivityServiceDetail.PARAM_CUSTOMER_ADDRESS, obj.getAddressId());

//                                String tos = Util.convertDateToString(Util.convertStringToDate(obj.getDateOfService(), "yyyyMMdd"), "dd MMM yyyy")
//                                        + " " + obj.getTimeOfService();
//
//                                i.putExtra(ActivityServiceDetail.PARAM_STARTSERVICE_TIME, assignment.getStartDate());

                                startActivityForResult(i, REQUESTCODE_SERVICE_DTL);
                                break;
                            case PAYMENT:
                                i = new Intent(getActivity(), ActivityPayment.class);
                                i.putExtra(ActivityPayment.PARAM_ASSIGNMENT_ID, assignment.getUid());
                                i.putExtra(ActivityPayment.PARAM_TECHNICIAN_ID, technicianId);
                                i.putExtra(ActivityPayment.PARAM_CUSTOMER_ID, obj.getCustomerId());
                                i.putExtra(ActivityPayment.PARAM_MITRA_ID, obj.getPartyId());
                                i.putExtra(ActivityPayment.PARAM_ORDER_ID, obj.getUid());
                                startActivityForResult(i, REQUESTCODE_SERVICE_PAYMENT);
                                break;
                            /*case RESCHEDULED:
                                Util.showDialog(getActivity(), getString(R.string.service_status_changed), getString(R.string.status_rescheduled));
                                break;*/
                            case CANCELLED_BY_CUSTOMER:
                                Util.showDialog(getActivity(), getString(R.string.status_cancelled_by_customer), getString(R.string.status_rescheduled));
                                break;
                            case PAID:
                                Util.showDialog(getActivity(), getString(R.string.service_finished), getString(R.string.status_paid));
                                break;
                            default:
                                Toast.makeText(getContext(), "Unhandled order status", Toast.LENGTH_SHORT).show();
                                break;
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        dialog.dismiss();
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                    }
                });

            }

            @Override
            public void onDataChanged(List<Assignment> list) {
                if (list.size() < 1) return;

                llBlank.setVisibility(View.GONE);
                rvAssigments.setVisibility(View.VISIBLE);

                //cek jika ada yg status OTW, nyalakan tracking gps
                stopTracking();
                for (Assignment ass : list) {
                    EOrderDetailStatus status = EOrderDetailStatus.convertValue(ass.getStatusDetailId());
                    if (status != EOrderDetailStatus.OTW)
                        continue;

                    //turn on tracking
                    startTracking(ass.getOrderId());

                    break;
                }
            }
        });
        rvAssigments.setAdapter(mAdapter);
    }

    private void stopTracking() {
        Realm r = Realm.getDefaultInstance();
        try {
            MobileSetup setup = r.where(MobileSetup.class).findFirst();

            r.beginTransaction();
            setup.setTrackingGps(false);
            setup.setTrackingOrderId(null);

            r.copyToRealmOrUpdate(setup);
            r.commitTransaction();
        } finally {
            r.close();
        }

    }

    private void startTracking(String orderId) {
        Realm r = Realm.getDefaultInstance();
        try {
            MobileSetup setup = r.where(MobileSetup.class).findFirst();

            r.beginTransaction();
            setup.setTrackingGps(true);
            setup.setTrackingOrderId(orderId);

            r.copyToRealmOrUpdate(setup);
            r.commitTransaction();
        } finally {
            r.close();
        }

    }

    public void cleanUpListener() {
        if (mAdapter != null)
            mAdapter.cleanUpListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentAssignmentistInteractionListener) {
            mListener = (OnFragmentAssignmentistInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnFragmentAssignmentistInteractionListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentAssignmentistInteractionListener {
        void onStatusChange(EOrderDetailStatus status);
    }
}
