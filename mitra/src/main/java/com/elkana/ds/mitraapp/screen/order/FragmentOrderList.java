package com.elkana.ds.mitraapp.screen.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.elkana.ds.mitraapp.R;
import com.elkana.ds.mitraapp.screen.assign.ActivityScrollingAssignment;
import com.elkana.ds.mitraapp.screen.map.ActivityTechOtwMap;
import com.elkana.ds.mitraapp.util.MitraUtil;
import com.elkana.dslibrary.firebase.FBFunction_BasicCallableRecord;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.pojo.FightInfo;
import com.elkana.dslibrary.pojo.OrderBucket;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentOrderListInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentOrderList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentOrderList extends Fragment {
    private static final String TAG = FragmentOrderList.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MITRA_ID = "mitraId";
    private static final String ARG_PARAM2 = "param2";
    private static final int columnCount = 1;   // kalo mau pake tablet tinggal ganti > 1

    private String mParamMitraId;
    private String mParam2;

    TextView tvEmptyMsg;
    RecyclerView rvOrders;

    private OnFragmentOrderListInteractionListener mListener;
    private RVAdapterOrderList mAdapter;

    private FirebaseFunctions mFunctions;

    // listen to assignment fight
//    DatabaseReference assignmentFightRef;
//    ValueEventListener assignmentFightValueListener;

    public FragmentOrderList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param paramMitraId Parameter 1.
     * @param param2       Parameter 2.
     * @return A new instance of fragment FragmentOrderList.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentOrderList newInstance(String paramMitraId, String param2) {
        FragmentOrderList fragment = new FragmentOrderList();
        Bundle args = new Bundle();
        args.putString(ARG_MITRA_ID, paramMitraId);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamMitraId = getArguments().getString(ARG_MITRA_ID);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mFunctions = FirebaseFunctions.getInstance();

        mAdapter = new RVAdapterOrderList(getContext(), mParamMitraId, new ListenerOrderList() {
            @Override
            public void onItemSelected(OrderBucket order) {

                if (MitraUtil.isExpiredBooking(order)) {
                    Toast.makeText(getContext(), "Expired Order !!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent;
                switch (EOrderDetailStatus.convertValue(order.getStatusDetailId())) {
                    case CREATED:
                        Toast.makeText(getActivity(), "Waiting Technicians Confirmation", Toast.LENGTH_SHORT).show();
                        break;
                    case UNHANDLED:
                        intent = new Intent(getActivity(), ActivityScrollingAssignment.class);
                        intent.putExtra(ActivityScrollingAssignment.PARAM_ORDER_ID, order.getUid());
                        intent.putExtra(ActivityScrollingAssignment.PARAM_CUSTOMER_ID, order.getCustomerId());
                        intent.putExtra(ActivityScrollingAssignment.PARAM_CUSTOMER_NAME, order.getCustomerName());
                        startActivity(intent);
                        break;
                    case ASSIGNED:
                        Toast.makeText(getActivity(), "Waiting for confirmation from Technician", Toast.LENGTH_SHORT).show();
                        break;
                    case CANCELLED_BY_TIMEOUT:
                        Toast.makeText(getActivity(), getString(R.string.status_expired), Toast.LENGTH_SHORT).show();
                        break;
                    case CANCELLED_BY_CUSTOMER:
                        Toast.makeText(getActivity(), getString(R.string.status_cancelled_by_customer), Toast.LENGTH_SHORT).show();
                        break;
                    case OTW:
                        intent = new Intent(getActivity(), ActivityTechOtwMap.class);
                        intent.putExtra(ActivityTechOtwMap.PARAM_ORDER_ID, order.getUid());
                        intent.putExtra(ActivityTechOtwMap.PARAM_TECH_NAME, order.getTechnicianName());
                        startActivity(intent);
                        break;
                    default:
                        Toast.makeText(getActivity(), "Unhandled " + order.getStatusDetailId(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onChangeTech(final OrderBucket data) {
                // per 8 apr 2018 dicancel dulu, krn kalo assign teknisi yg sama malah double assignment
                // jd utk sementara ga bisa change tech
                if (true)
                    return;
                Util.showDialogConfirmation(getActivity(), "Change Technician", "Yakin ganti/pindah teknisi ?", new ListenerPositiveConfirmation() {
                    @Override
                    public void onPositive() {
                        Intent intent = new Intent(getActivity(), ActivityScrollingAssignment.class);
                        intent.putExtra(ActivityScrollingAssignment.PARAM_ORDER_ID, data.getUid());
                        intent.putExtra(ActivityScrollingAssignment.PARAM_CUSTOMER_ID, data.getCustomerId());
                        intent.putExtra(ActivityScrollingAssignment.PARAM_CUSTOMER_NAME, data.getCustomerName());
                        startActivity(intent);

                    }
                });

            }

            @Override
            public void onRefresh() {
                tvEmptyMsg.setVisibility(mAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onNewOrderCameIn(OrderBucket orderBucket) {

            }

            @Override
            public void onCancelOrder(OrderBucket data) {
                final AlertDialog alertDialog = Util.showProgressDialog(getActivity(), "Proses pembatalan order dari " + data.getCustomerName());

                final Map<String, Object> _keyVal = new HashMap<>();
                _keyVal.put("customerId", data.getCustomerId() );
                _keyVal.put("orderId", data.getUid());
                _keyVal.put("cancelStatus", EOrderDetailStatus.CANCELLED_BY_SERVER.name());
                _keyVal.put("cancelReason", data.getStatusComment());

                mFunctions.getHttpsCallable(FBUtil.FUNCTION_CANCEL_BOOKING)
                        .call(_keyVal)
                        .continueWith(new FBFunction_BasicCallableRecord())
                        .addOnCompleteListener(new OnCompleteListener<Map<String, Object>>() {
                            @Override
                            public void onComplete(@NonNull Task<Map<String, Object>> task) {
                                if (!getActivity().isDestroyed())
                                    alertDialog.dismiss();

                                if (!task.isSuccessful()) {
                                    if (getContext() != null) {
                                        Log.e(TAG, task.getException().getMessage(), task.getException());
                                        Util.showErrorDialog(getContext(), "Cancel Order Error", task.getException().getMessage());
                                    }

                                    return;
                                }
/*
                                Realm _r = Realm.getDefaultInstance();
                                try {
                                    _r.beginTransaction();
                                    _r.copyToRealmOrUpdate(orderHeaderCopy);
                                    _r.commitTransaction();

                                    if (mListener != null) {
                                        mListener.onOrderCancelled(orderHeaderCopy.getServiceType(), orderHeaderCopy.getInvoiceNo());
                                    }

                                } finally {
                                    _r.close();
                                }*/
                            }
                        });

            }
        });

        // mitra will listen to path assignments/ac/fight/<orderId>/
        // barangsiapa ada teknisi yg update path tersebut, maka mitra akan otomatis buat assignmentordernya
        /*
        assignmentFightValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    return;

//                "assignments/ac/fight/<orderId>/techId:12345abc"
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final FightInfo value = postSnapshot.getValue(FightInfo.class);

                    if (value == null)
                        continue;

                    //TODO: antisipasi persistence, cek dulu kalo udah diassign jgn create lagi

//                // assign to pending order. see on logic assignment
                     //dipindah ke cloud
//                    mListener.onCreateAssignment(value);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
            }
        };
//        assignmentFightRef = FirebaseDatabase.getInstance().getReference(FBUtil.REF_ASSIGNMENTS_FIGHT);
//        assignmentFightRef.addValueEventListener(assignmentFightValueListener);   //dipindah ke cloud function
*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        assignmentFightRef.removeEventListener(assignmentFightValueListener);
        mAdapter.cleanUpListener();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_order_list, container, false);

        tvEmptyMsg = v.findViewById(R.id.tvEmptyMsg);

        rvOrders = v.findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new GridLayoutManager(getContext(), columnCount));
//        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        rvOrders.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentOrderListInteractionListener) {
            mListener = (OnFragmentOrderListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnFragmentOrderListInteractionListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentOrderListInteractionListener {
//        @Deprecated
//        void onCreateAssignment(FightInfo fightInfo);   //tdk lagi diassign di mitra, tp di cloud function
    }


}
