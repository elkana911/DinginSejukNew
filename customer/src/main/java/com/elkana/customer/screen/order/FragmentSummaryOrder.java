package com.elkana.customer.screen.order;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.elkana.customer.R;
import com.elkana.customer.util.CustomerUtil;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.listener.ListenerSync;
import com.elkana.dslibrary.pojo.OrderBucket;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.mitra.Mitra;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.EOrderStatus;
import com.elkana.dslibrary.util.NetUtil;
import com.elkana.dslibrary.util.Util;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentSOInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentSummaryOrder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSummaryOrder extends Fragment {

    private static final String TAG = FragmentSummaryOrder.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "userId";
//    private static final String ARG_PARAM2 = "orderId"; // can be null since fragment is created before orderId created

    //    private String mOrderId;
    private String kapanYYYYMMDD;

    private DatabaseReference orderHeaderPendingRef;

    private OnFragmentSOInteractionListener mListener;

    private ValueEventListener mOrderHeaderPendingListener;

    View llBlank, llNonBlank;

    EditText etStatus, etRatingComments;

    TextView tvStatusDetil, tvServiceType, tvDateService, tvDateRequest, tvMitra, tvAddress, tvProblem;

    Button btnCheckTechnicianGps, btnPayment, btnCancelOrder, btnReschedule;

    CardView cardReview;

    RatingBar ratingBar;

    private String sRefOrderHeader;
    private Typeface fontFace;

    private String mSelectedUserId;
    private String mSelectedOrderId;

    public FragmentSummaryOrder() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentSummaryOrder.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSummaryOrder newInstance() {
        FragmentSummaryOrder fragment = new FragmentSummaryOrder();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, userId);
//        args.putString(ARG_PARAM2, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mUserId = getArguments().getString(ARG_PARAM1);
//            mOrderId = getArguments().getString(ARG_PARAM2);
        }

        fontFace = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/DinDisplayProMedium.otf");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }

                if (Util.TESTING_MODE)
                    Log.e(TAG, "valueEventListener.DataChange for " + sRefOrderHeader);

                final OrderHeader value = dataSnapshot.getValue(OrderHeader.class);

                Realm realm = Realm.getDefaultInstance();
                try {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(value);
                        }
                    });

                } finally {
                    realm.close();
                }

                displaySummaryOrder(mSelectedUserId, mSelectedOrderId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
            }
        };

        mOrderHeaderPendingListener = valueEventListener;

    }

    /**
     * All Order displayed should call this.
     *
     * @param orderId if null, latest order will be displayed
     */
    private OrderHeader displaySummaryOrder(String userId, String orderId) {
        final Realm realm = Realm.getDefaultInstance();
        try {

            RealmQuery<OrderHeader> query = realm.where(OrderHeader.class).equalTo("customerId", userId);

            if (query.count() > 0) {
                if (orderId == null) {
                    query = query.notEqualTo("statusId", EOrderStatus.FINISHED.name());
                } else {
                    query = query.equalTo("uid", orderId);
                }
            } else {
                return null;
            }

            mSelectedUserId = userId;

            RealmResults<OrderHeader> allSorted = query
                    .findAllSorted("updatedTimestamp", Sort.DESCENDING);

            if (allSorted.size() < 1)
                return null;

            final OrderHeader orderHeader = allSorted.first();

            mSelectedOrderId = orderHeader.getUid();

            if (orderHeader == null)
                return null;

            BasicInfo basicInfo = realm.where(BasicInfo.class)
                    .equalTo("uid", userId)
                    .findFirst();

            etStatus.setHint(getString(R.string.label_status_order, orderHeader.getInvoiceNo()));

            StringBuffer sb = new StringBuffer();
            sb.append(getString(R.string.prompt_hello, basicInfo.getName().toUpperCase())).append("\n");

            EOrderDetailStatus orderDetailStatus = EOrderDetailStatus.convertValue(orderHeader.getStatusDetailId());
            sb.append(CustomerUtil.getMessageStatusDetail(getContext(), orderDetailStatus)).append("\n");

            EOrderStatus orderStatus = EOrderStatus.convertValue(orderHeader.getStatusId());

            // any add-on requirements about order status
            btnCheckTechnicianGps.setVisibility(View.GONE);
            btnPayment.setVisibility(View.GONE);
            btnCancelOrder.setVisibility(View.INVISIBLE);
            btnReschedule.setVisibility(View.INVISIBLE);
            btnReschedule.setEnabled(true);
            switch (orderDetailStatus) {
                case CREATED:
                    //berhubung msh ada timer maka tidak bisa dicancel/reschedule
//                    btnCancelOrder.setVisibility(View.VISIBLE);
//                    btnReschedule.setVisibility(View.VISIBLE);
                    break;
                case UNHANDLED:
                    btnCancelOrder.setVisibility(View.VISIBLE);
                    btnReschedule.setVisibility(View.VISIBLE);
                    break;
                case ASSIGNED:
                    btnCancelOrder.setVisibility(View.VISIBLE);
                    btnReschedule.setVisibility(View.VISIBLE);
                    btnReschedule.setEnabled(orderHeader.getRescheduleCounter() < 1);
                    break;
                case OTW:
                    // TODO: mungkin msh boleh dibatalkan ? tp kena charge atau gmn
                    btnCheckTechnicianGps.setVisibility(View.VISIBLE);
                    break;
                case WORKING:
                    break;
                case PAYMENT:
//                    btnPayment.setVisibility(View.VISIBLE);
                    break;
                case PAID:
                    if (orderStatus == EOrderStatus.FINISHED) {
                        cardReview.setVisibility(View.GONE);
                        tvStatusDetil.setText(tvStatusDetil.getText() + "\n" + getString(R.string.message_review_given));
                    } else {
                        cardReview.setVisibility(View.VISIBLE);
                        tvStatusDetil.setText(tvStatusDetil.getText() + "\n" + getString(R.string.message_review_not_given));

                    }
                    break;
                case CANCELLED_BY_CUSTOMER:
                    break;
                case CANCELLED_BY_SERVER:
                    break;
                case CANCELLED_BY_TIMEOUT:
                    break;
//                case RESCHEDULED:
//                    break;
            }

            tvStatusDetil.setText(sb.toString());

            tvServiceType.setText(getString(R.string.prompt_ac_serviceType) + ": " + CustomerUtil.getServiceTypeLabel(getContext(), orderHeader.getServiceType()));

            tvDateRequest.setText(getString(R.string.prompt_date_request) + ": " + Util.prettyTimestamp(getContext(), orderHeader.getCreatedTimestamp()));
            tvDateService.setText(getString(R.string.prompt_date_service) + ": " + Util.prettyTimestamp(getContext(), orderHeader.getTimestamp()));

            tvAddress.setText(getString(R.string.prompt_cust_address) + ": " + orderHeader.getAddressId());

            CustomerUtil.syncMitra(getContext(), new ListenerSync() {
                @Override
                public void onPostSync(Exception e) {

                    Mitra mitraObj = CustomerUtil.lookUpMitraById(realm, orderHeader.getPartyId());

                    if (mitraObj != null) {
                        tvMitra.setText(getString(R.string.prompt_vendor) + ": " + mitraObj.getName() + ", " + mitraObj.getAddressLabel() + ", Ph: " + mitraObj.getPhone1());
                    }

                }
            });

            StringBuffer problem = new StringBuffer();
            problem.append(getString(R.string.summary_ac_problem, orderHeader.getJumlahAC(), orderHeader.getProblem()));

            tvProblem.setText(getString(R.string.prompt_problem_ac) + ": " + problem.toString());

            llBlank.setVisibility(View.GONE);
            llNonBlank.setVisibility(View.VISIBLE);

            return realm.copyFromRealm(orderHeader);

        } finally {
            realm.close();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_summary_order, container, false);

        llBlank = v.findViewById(R.id.llBlank);
        llNonBlank = v.findViewById(R.id.llNonBlank);

        etStatus = v.findViewById(R.id.etStatus);

        tvStatusDetil = v.findViewById(R.id.tvStatusDetil);
        tvAddress = v.findViewById(R.id.tvAddress);
        tvServiceType = v.findViewById(R.id.tvServiceType);
        tvDateRequest = v.findViewById(R.id.tvDateRequest);
        tvDateService = v.findViewById(R.id.tvDateService);
        tvMitra = v.findViewById(R.id.tvMitra);
        tvProblem = v.findViewById(R.id.tvProblem);

        btnPayment = v.findViewById(R.id.btnPayment);

        btnReschedule = v.findViewById(R.id.btnReschedule);
        btnReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_reschedule_order, null);
                final EditText etDate = view.findViewById(R.id.etDate);
                final EditText etTime = view.findViewById(R.id.etTime);
                etDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Get Current Date
                        final Calendar c = Calendar.getInstance();
                        final int mYear = c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        etDate.setText(Util.prettyDate(getContext(), dayOfMonth, monthOfYear, year, false));

                                        Calendar c = Calendar.getInstance();
                                        c.set(year, monthOfYear, dayOfMonth);
                                        kapanYYYYMMDD = Util.convertDateToString(c.getTime(), "yyyyMMdd");

                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                        datePickerDialog.show();
                    }
                });

                etTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
// Get Current Time
                        final Calendar c = Calendar.getInstance();
                        int mHour = c.get(Calendar.HOUR_OF_DAY);
                        int mMinute = c.get(Calendar.MINUTE);

                        // Launch Time Picker Dialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {

                                        etTime.setText(String.format("%02d:%02d", hourOfDay, minute));
//                                etTime.setText(hourOfDay + ":" + minute);

                                    }
                                }, mHour, mMinute, false);
                        timePickerDialog.show();
                    }
                });


                Util.showDialogConfirmation(getContext(), getString(R.string.title_reschedule_order)
                        , "Silakan isi jadwal yang baru..."
                        , view
                        , new ListenerPositiveConfirmation() {
                            @Override
                            public void onPositive() {
                                String jam = etTime.getText().toString().trim();
                                Date newDate = Util.convertStringToDate(kapanYYYYMMDD + jam, "yyyyMMddHH:mm");

                                rescheduleOrder(newDate);
                            }
                        });

            }
        });

        btnCancelOrder = v.findViewById(R.id.btnCancelOrder);
        btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isConnected(getContext())) {
                    Toast.makeText(getContext(), "Not Connected", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mSelectedOrderId == null) {
                    Toast.makeText(getContext(), "Order Id Not Assigned. Unable to cancel", Toast.LENGTH_SHORT).show();
                    return;
                }

                final Realm realm = Realm.getDefaultInstance();
                try {
                    final OrderHeader orderHeader = realm.where(OrderHeader.class)
                            .equalTo("customerId", mSelectedUserId)
                            .equalTo("uid", mSelectedOrderId)
                            .findFirst();

                    if (orderHeader == null)
                        return;

                    final EOrderDetailStatus orderStatus = EOrderDetailStatus.convertValue(orderHeader.getStatusDetailId());

                    if (orderStatus == EOrderDetailStatus.CREATED
                            || orderStatus == EOrderDetailStatus.ASSIGNED
                            || orderStatus == EOrderDetailStatus.UNHANDLED
                            ) {
                    } else {
                        Util.showDialog(getContext(), null, getString(R.string.error_technician_otw));
                        return;
                    }

                    final OrderHeader orderHeaderCopy = realm.copyFromRealm(orderHeader);
                    orderHeaderCopy.setStatusDetailId(EOrderDetailStatus.CANCELLED_BY_CUSTOMER.name());
                    orderHeaderCopy.setStatusId(EOrderStatus.FINISHED.name());
                    orderHeaderCopy.setUpdatedTimestamp(new Date().getTime());

                    Util.showDialogConfirmation(getContext()
                            , getString(R.string.title_cancel_order)
                            , getString(R.string.message_cancel_order_confirmation_for_date, Util.prettyTimestamp(getContext(), orderHeader.getTimestamp()))
                            , new ListenerPositiveConfirmation() {
                                @Override
                                public void onPositive() {
                                    final AlertDialog alertDialog = Util.showProgressDialog(getActivity(), "Proses pembatalan order...");

                                    FBUtil.Orders_cancel(orderHeaderCopy, EOrderDetailStatus.CANCELLED_BY_CUSTOMER, new ListenerModifyData() {
                                        @Override
                                        public void onSuccess() {
                                            if (!getActivity().isDestroyed())
                                                alertDialog.dismiss();

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
                                            }
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            if (!getActivity().isDestroyed())
                                                alertDialog.dismiss();

                                            if (getContext() != null)
                                                Util.showErrorDialog(getContext(), "Cancel Order Error", "Sorry, Batal Order tidak dapat dilakukan.\n" + e.getMessage());

                                        }
                                    });

                                    /*
                                    orderHeaderPendingRef.setValue(orderHeaderCopy).addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
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
                                            }
                                        }
                                    });*/
                                }
                            });

                } finally {
                    realm.close();
                }

            }
        });

        btnCheckTechnicianGps = v.findViewById(R.id.btnCheckTechnicianGps);
        btnCheckTechnicianGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Realm realm = Realm.getDefaultInstance();
                try {
                    final OrderHeader orderHeader = realm.where(OrderHeader.class)
                            .equalTo("customerId", mSelectedUserId)
                            .equalTo("uid", mSelectedOrderId)
                            .findFirst();

                    if (orderHeader == null)
                        return;

                    Intent intent = new Intent(getActivity(), ActivityTechOtwMap.class);
                    intent.putExtra(ActivityTechOtwMap.PARAM_ORDER_ID, orderHeader.getUid());
                    intent.putExtra(ActivityTechOtwMap.PARAM_TECH_NAME, orderHeader.getTechnicianName());
                    startActivity(intent);

                } finally {
                    realm.close();
                }
            }
        });

        Button btnSubmitReview = v.findViewById(R.id.btnSubmitReview);
        btnSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rating = (int) (ratingBar.getRating() * 10);

                String comments = etRatingComments.getText().toString();

                Map<String, Object> keyVal = new HashMap<>();
                keyVal.put("ratingByCustomer", rating);
                keyVal.put("ratingComments", comments);
                keyVal.put("statusId", EOrderStatus.FINISHED.name());

                orderHeaderPendingRef.updateChildren(keyVal).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        cardReview.setVisibility(View.GONE);
                        tvStatusDetil.setText(tvStatusDetil.getText() + "\n" + getString(R.string.message_review_given));

                    }
                });

//        Toast.makeText(getActivity(), "rating is " + rating, Toast.LENGTH_SHORT).show();

            }
        });

        Button btnCallMitra = v.findViewById(R.id.btnCallMitra);
        btnCallMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Realm realm = Realm.getDefaultInstance();
                try {
                    final OrderHeader orderHeader = realm.where(OrderHeader.class)
                            .equalTo("customerId", mSelectedUserId)
                            .equalTo("uid", mSelectedOrderId)
                            .findFirst();

                    if (orderHeader == null)
                        return;

                    Mitra mitra = CustomerUtil.lookUpMitraById(realm, orderHeader.getPartyId());

                    String availPhone = mitra.getPhone1() == null ? mitra.getPhone2() : mitra.getPhone1();

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + availPhone));
                    startActivity(callIntent);

                } finally {
                    realm.close();
                }

            }
        });

        cardReview = v.findViewById(R.id.cardReview);

        ratingBar = v.findViewById(R.id.ratingBar);

        etRatingComments = v.findViewById(R.id.etRatingComments);

        llBlank.setVisibility(View.VISIBLE);
        llNonBlank.setVisibility(View.GONE);

        tvStatusDetil.setTypeface(fontFace);
        tvServiceType.setTypeface(fontFace);
        tvDateRequest.setTypeface(fontFace);
        tvDateService.setTypeface(fontFace);
        tvProblem.setTypeface(fontFace);
        tvMitra.setTypeface(fontFace);
        tvAddress.setTypeface(fontFace);

//        AppCompatImageView ivNoOrder = (AppCompatImageView) v.findViewById(R.id.ivNoOrder);
//        ivNoOrder.setColorFilter(Color.parseColor("#26767676"));    not working

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentSOInteractionListener) {
            mListener = (OnFragmentSOInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnFragmentSOInteractionListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void rescheduleOrder(final Date newDate) {
        if (!NetUtil.isConnected(getContext())) {
            Toast.makeText(getContext(), "Not Connected", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mSelectedOrderId == null) {
            Toast.makeText(getContext(), "Order Id Not Assigned. Unable to reschedule", Toast.LENGTH_SHORT).show();
            return;
        }

        // yg sudah direschedule harusnsya tidak dapat dibatalkan ?

        final Realm realm = Realm.getDefaultInstance();
        try {
            final OrderHeader orderHeader = realm.where(OrderHeader.class)
                    .equalTo("customerId", mSelectedUserId)
                    .equalTo("uid", mSelectedOrderId)
                    .findFirst();

            final OrderBucket orderBucket = realm.where(OrderBucket.class)
                    .equalTo("customerId", mSelectedUserId)
                    .equalTo("uid", mSelectedOrderId)
                    .findFirst();

            if (orderHeader == null || orderBucket == null) {
                Toast.makeText(getActivity(), "Missing link data. Unable to Reschedule Order", Toast.LENGTH_SHORT).show();

                return;
            }

            if (orderHeader.getRescheduleCounter() > 0) {
                Util.showErrorDialog(getActivity(), "Reschedule Limit", "Maaf, tidak diperkenankan Reschedule untukm booking yayang sama.");
                return;
            }

            if (orderHeader.getStatusDetailId().equals(EOrderDetailStatus.CREATED.name())
//                    || orderHeader.getStatusDetailId().equals(EOrderDetailStatus.ASSIGNED.name())
                    ) {
                if (mListener != null)
                    mListener.onError(new RuntimeException("Tidak dapat reschedule di status saat ini"));
                return;
            } else {
            }

            Util.showDialogConfirmation(getContext()
                    , getString(R.string.title_cancel_order)
                    , getString(R.string.message_cancel_order_confirmation_for_date, Util.prettyTimestamp(getContext(), orderHeader.getTimestamp()))
                    , new ListenerPositiveConfirmation() {
                        @Override
                        public void onPositive() {

                            final OrderHeader orderHeaderCopy = realm.copyFromRealm(orderHeader);

                            final OrderBucket orderBucketCopy = realm.copyFromRealm(orderBucket);

                            FBUtil.Orders_reschedule(orderHeaderCopy, orderBucketCopy, newDate, new ListenerModifyData() {
                                @Override
                                public void onSuccess() {
                                    Realm _r = Realm.getDefaultInstance();
                                    try {
                                        _r.beginTransaction();
                                        _r.copyToRealmOrUpdate(orderHeaderCopy);
                                        _r.copyToRealmOrUpdate(orderBucketCopy);
                                        _r.commitTransaction();

                                        if (mListener != null) {
                                            mListener.onOrderRescheduled(orderHeaderCopy.getServiceType(), orderHeaderCopy.getInvoiceNo());
                                        }

                                    } finally {
                                        _r.close();
                                    }

                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });

//                            FBUtil.Order_SetStatus(orderHeaderCopy.getPartyId(), orderHeaderCopy.getCustomerId(), orderHeaderCopy.getUid(), null, EOrderDetailStatus.CREATED, String.valueOf(Const.USER_AS_COSTUMER), new ListenerModifyData() {
//                                @Override
//                                public void onSuccess() {
//                                }
//
//                                @Override
//                                public void onError(Exception e) {
//                                    if (getContext() != null)
//                                        Util.showErrorDialog(getContext(), "Reschedule Order Error", "Sorry, Jadwal ulang Order tidak dapat dilakukan.\n" + e.getMessage());
//                                }
//                            });

//                            orderHeaderPendingRef
//                                    .setValue(orderHeaderCopy)
//                                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                }
//                            });
                        }
                    });


        } finally {
            realm.close();
        }
    }

    /**
     * Because this fragment need to listen on an order, this function is important to called before displayed.
     *
     * @param orderId
     */
    public void reInitiate(String userId, String orderId) {

        this.mSelectedUserId = userId;

        OrderHeader orderHeader = displaySummaryOrder(userId, orderId);

        if (orderHeaderPendingRef != null)
            orderHeaderPendingRef.removeEventListener(mOrderHeaderPendingListener);

        if (orderHeader == null)
            return;

        orderHeaderPendingRef = FBUtil.Order_getPendingCustomerRef(userId, orderHeader.getUid());

        // assign listener
        orderHeaderPendingRef.addValueEventListener(mOrderHeaderPendingListener);

        // update kalo udah expired. ga perlu, dipindah di mitra saja
        if (!Util.isExpiredOrder(orderHeader))
            return;

        /*
        // TODO: due to timeline, just update the status. when time is available, please handle by moving node to "finished"
        // sebenere males juga ngurusin move node krn riskan putus koneksi
        Toast.makeText(getActivity(), "Expired Order ! Please handle.", Toast.LENGTH_SHORT).show();

        CustomerUtil.updateOrderStatus(orderHeader.getUid(), userId, EOrderDetailStatus.CANCELLED_BY_TIMEOUT, new ListenerModifyData() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {

            }
        });
        */

        // TODO: should go back to order list ?
                /*
                orderHeader.setStatusDetailId(EOrderDetailStatus.CANCELLED_BY_TIMEOUT.name());
                orderHeader.setStatusId(EOrderStatus.FINISHED.name());

                ((FirebaseActivity)getActivity()).firebaseOrder_SetStatus(orderHeader.getPartyId(), orderHeader.getCustomerId(), orderHeader.getUid(), EOrderDetailStatus.CANCELLED_BY_TIMEOUT, new ListenerModifyData() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

                // update ke server
//                if (orderHeaderPendingRef != null) {
//                    orderHeaderPendingRef.setValue(orderHeader);
//                }
*/
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (orderHeaderPendingRef != null)
            orderHeaderPendingRef.removeEventListener(mOrderHeaderPendingListener);
    }

    public void reInitiate(OrderHeader order) {
        reInitiate(order.getCustomerId(), order.getUid());
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
    public interface OnFragmentSOInteractionListener {

        void onError(Exception e);

        void onOrderCancelled(int serviceType, String invoiceNo);

        void onOrderRescheduled(int serviceType, String invoiceNo);
    }
}
