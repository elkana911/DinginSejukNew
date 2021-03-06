package com.elkana.ds.mitraapp.screen.order;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.elkana.ds.mitraapp.R;
import com.elkana.ds.mitraapp.util.MitraUtil;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerGetString;
import com.elkana.dslibrary.pojo.OrderBucket;
import com.elkana.dslibrary.util.ColorUtil;
import com.elkana.dslibrary.util.DateUtil;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Eric on 01-Dec-17.
 */

public class RVAdapterOrderList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = RVAdapterOrderList.class.getSimpleName();

    private DatabaseReference orders4MitraRef;
    private Context mContext;
    private String mMitraId;

//    private static final int quotaNotifyTechnicians = 5;

    //https://stackoverflow.com/questions/31059251/how-to-handle-multiple-countdown-timers-in-listview
//    private List<MyViewHolder> lstHolders;
    private List<OrderBucket> mList = new ArrayList<>();

    private ValueEventListener mOrderBucketListener;
    private ListenerOrderList mListener;

    /*
    private Handler mHandler = new Handler();

    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lstHolders) {
                long currentTime = System.currentTimeMillis();
                for (MyViewHolder holder : lstHolders) {
                    holder.updateTimeRemaining(currentTime);
                }
            }
        }
    };*/


    public RVAdapterOrderList(Context ctx, final String mitraId, final ListenerOrderList listener) {
        mContext = ctx;

        if (mitraId == null) {
            return;
        }

        mListener = listener;
        mMitraId = mitraId;

        final AlertDialog alertDialog = Util.showProgressDialog(mContext, "Getting Data...");

        mOrderBucketListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();

                alertDialog.dismiss();

                if (!dataSnapshot.exists()) {
                    if (listener != null)
                        listener.onRefresh();

                    return;
                }

                Date today = new Date();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final OrderBucket _orderBucket = postSnapshot.getValue(OrderBucket.class);
//                    Log.e(TAG, "DataChange:" + _orderBucket.toString());
//                    final String _wktBooking = Util.convertDateToString(new Date(_orderBucket.getServiceTimestamp()), "yyyyMMddHHmm");

                    EOrderDetailStatus detailStatus = EOrderDetailStatus.convertValue(_orderBucket.getStatusDetailId());

                    // skip FINISHED yesterday order
                    if (detailStatus == EOrderDetailStatus.CANCELLED_BY_TIMEOUT ||
                            detailStatus == EOrderDetailStatus.CANCELLED_BY_CUSTOMER ||
                            detailStatus == EOrderDetailStatus.CANCELLED_BY_SERVER ||
                            detailStatus == EOrderDetailStatus.PAID
                            ) {

                        if (detailStatus == EOrderDetailStatus.PAID) {
                            //catet jobs as history. this job full stop status and should not be moved anywhere
                            // to avoid updating Firebase on every changes, we need realm to store
                            FBUtil.Mitra_jobAsHistory(_orderBucket);

                        } else if (detailStatus == EOrderDetailStatus.CANCELLED_BY_TIMEOUT
                                || detailStatus == EOrderDetailStatus.CANCELLED_BY_SERVER
                                || detailStatus == EOrderDetailStatus.CANCELLED_BY_CUSTOMER) {

                            if (_orderBucket.getTechnicianId() != null) {
                                FBUtil.Mitra_jobAsCancelled(_orderBucket);
                            }
                        }

                        // kalau lama ga ada update, dan hari udah lewat maka jgn ditambahkan ke display
                        Date _updatedDate = new Date(_orderBucket.getUpdatedTimestamp());
                        if (DateUtil.isBeforeDay(_updatedDate, today)) {
                            continue;
                        }
                    }

                    if (detailStatus == EOrderDetailStatus.OTW) {
                        //catet jobs sbg assigned. this job will be moved into jobs_history if not cancelled. otherwise will be moved into jobs_cancelled
                        // to avoid updating Firebase on every changes, we need realm to store

                        // ga jadi dipindah ke cloud krn dipakai waktu assigned manual jd mengandalkan data di lokal
                        FBUtil.Mitra_jobAsAssigned(_orderBucket);
                        /*
                        Realm __r = Realm.getDefaultInstance();
                        try {
                            JobsAssigned _jobs = new JobsAssigned();
                            _jobs.setId(new Date().getTime());
                            _jobs.setOrderId(_orderBucket.getUid());
                            _jobs.setTechId(_orderBucket.getTechnicianId());
                            _jobs.setWkt(_wktBooking);

                            __r.beginTransaction();
                            __r.copyToRealmOrUpdate(_jobs);
                            __r.commitTransaction();
                        } finally {
                            __r.close();
                        }*/
                    }

                    mList.add(_orderBucket);
/*
                    // notify_new_order hanya berlaku kalau status msh created
                    if (detailStatus != EOrderDetailStatus.CREATED)
                        continue;

                    //tp jg jgn kirim notifyneworder kalo expired
//                    if (MitraUtil.isExpiredBooking(_orderBucket)) {
//                        FBUtil.Order_SetStatus(mMitraId, _orderBucket.getCustomerId(), _orderBucket.getUid(), null, null, EOrderDetailStatus.CANCELLED_BY_TIMEOUT, String.valueOf(Const.USER_AS_MITRA), null);
//                        continue;
//                    }

                    // moved into cloud function
                    if (true)
                        continue;

                    // cek dulu jumlah teknisi terdaftar
                    List<TechnicianReg> targets = MitraUtil.getAllTechnicianRegByScoring(_orderBucket.getBookingTimestamp());

                    final Realm _r = Realm.getDefaultInstance();
                    try {

                        // TODO: urutin by scoring tertinggi

                        for (TechnicianReg reg : targets) {
                            final String techId = reg.getTechId();

                            NotifyTechnician notifyTechnician = _r.where(NotifyTechnician.class)
                                    .equalTo("orderId", _orderBucket.getUid())
                                    .equalTo("techId", techId)
                                    .findFirst();

                            // on reschedule/new booking, _obj.gettechnicianid will be null
                            if (_orderBucket.getTechnicianId() == null) {

                                // TODO: cek sudah pernah di kirim ?,
                                if (notifyTechnician == null) {
                                    NotifyTechnician __obj = new NotifyTechnician();
                                    __obj.setUid(java.util.UUID.randomUUID().toString());
                                    __obj.setOrderId(_orderBucket.getUid());
                                    __obj.setTechId(techId);
                                    __obj.setTimestamp(new Date().getTime());

                                    _r.beginTransaction();
                                    _r.copyToRealmOrUpdate(__obj);
                                    _r.commitTransaction();

                                    FBUtil.TechnicianReg_setNotifyNewOrderTo(techId, _orderBucket, new ListenerModifyData() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    // on reschedule case, we need to resent again by update some row
                                }


                            } else {
                                // cek kalo udah expired dihapus aja
                                if (MitraUtil.isExpiredBooking(_orderBucket)) {
                                    FBUtil.TechnicianReg_deleteNotifyNewOrder(mitraId, techId, _orderBucket.getUid(), new ListenerModifyData() {
                                        @Override
                                        public void onSuccess() {
                                            Realm __r = Realm.getDefaultInstance();
                                            try {
                                                __r.beginTransaction();
                                                __r.where(NotifyTechnician.class).equalTo("orderId", _orderBucket.getUid())
                                                        .equalTo("techId", techId).findAll().deleteAllFromRealm();
                                                __r.commitTransaction();
                                            } finally {
                                                __r.close();
                                            }
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }
                            }

                        }
                    } finally {
                        _r.close();
                    }
*/
                }

                if (mList.size() > 0){
                    Collections.sort(mList, new Comparator<OrderBucket>() {
                        @Override
                        public int compare(OrderBucket s1, OrderBucket s2) {

                            //1. compare by status first. kalo sukses brarti 1. masalahnya versi 0.5.0 blm ada statusId jd masih ngandalin statusdetailid
                            int compareStatus = 0;
                            EOrderDetailStatus s1DetailStatus = EOrderDetailStatus.convertValue(s1.getStatusDetailId());
                            EOrderDetailStatus s2DetailStatus = EOrderDetailStatus.convertValue(s2.getStatusDetailId());

                            if (s1DetailStatus != s2DetailStatus) {

                                boolean is_s1StatusIsDone = s1DetailStatus == EOrderDetailStatus.PAID
                                        || s1DetailStatus == EOrderDetailStatus.CANCELLED_BY_CUSTOMER
                                        || s1DetailStatus == EOrderDetailStatus.CANCELLED_BY_SERVER
                                        || s1DetailStatus == EOrderDetailStatus.CANCELLED_BY_TIMEOUT;

                                boolean is_s2StatusIsDone = s2DetailStatus == EOrderDetailStatus.PAID
                                        || s2DetailStatus == EOrderDetailStatus.CANCELLED_BY_CUSTOMER
                                        || s2DetailStatus == EOrderDetailStatus.CANCELLED_BY_SERVER
                                        || s2DetailStatus == EOrderDetailStatus.CANCELLED_BY_TIMEOUT;

                                if (is_s1StatusIsDone && is_s2StatusIsDone) {
                                } else {
                                    if (is_s1StatusIsDone)
                                        compareStatus = -1;
                                    else
                                        compareStatus = 1;
                                }

                            }

                            if (compareStatus != 0)
                                return compareStatus;

                            //2. compare by dateOfService
                            compareStatus = s1.getDateOfService().compareTo(s2.getDateOfService());

                            if (compareStatus != 0)
                                return compareStatus;

                            //3. compare by updatedTimestamp
                            if (s1.getUpdatedTimestamp() < s2.getUpdatedTimestamp())
                                return 1;   // DESCENDING
                            else if (s1.getUpdatedTimestamp() > s2.getUpdatedTimestamp())
                                return -1;
                            else
                                return 0;
                        }
                    });

                    notifyDataSetChanged();

                    if (listener != null)
                        listener.onRefresh();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                alertDialog.dismiss();
            }
        };

        orders4MitraRef = FirebaseDatabase.getInstance().getReference(FBUtil.REF_ORDERS_MITRA_AC_PENDING)
                .child(mitraId);

        orders4MitraRef.addValueEventListener(mOrderBucketListener);

    }

    public void cleanUpListener() {
        if (mOrderBucketListener != null) {
            orders4MitraRef.removeEventListener(mOrderBucketListener);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_new_order_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final OrderBucket obj = mList.get(position);

        ((MyViewHolder) holder).tvNo.setText("#" + (mList.size() - position));
//        ((MyViewHolder) holder).tvNo.setText("#" + String.valueOf(position+1));

        ((MyViewHolder) holder).setData(obj);

        EOrderDetailStatus detailStatus = EOrderDetailStatus.convertValue(obj.getStatusDetailId());

        if (detailStatus == EOrderDetailStatus.CANCELLED_BY_TIMEOUT) {
            ((MyViewHolder) holder).tvCounter.setText("Expired!!");

//            return;
        }

        if (detailStatus == EOrderDetailStatus.CREATED || detailStatus == EOrderDetailStatus.UNHANDLED || detailStatus == EOrderDetailStatus.ASSIGNED) {

            if (MitraUtil.isExpiredBooking(obj)) {
                ((MyViewHolder) holder).tvCounter.setText("Expired!!");

                // TODO: update firebase db

                return;
            }
        }

        // spy tdk infinite loop wkt refresh firebase, timer hanya berjalan jk statusnya CREATED
        if (detailStatus == EOrderDetailStatus.CREATED)
            ((MyViewHolder) holder).startTimer(obj);
        else
            ((MyViewHolder) holder).stopTimer(obj);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        public EOrderDetailStatus lastStatus;

        public TextView tvAddress, tvCustomerName, tvHandledBy, tvServiceTime, tvOrderStatus, tvCounter, tvNo, tvInvoiceNo;
        public View view;
        public ImageView ivIconStatus;
        public Button btnCallTech, btnCallCust, btnChangeTech, btnCancelOrder, btnCheckStatus;

        CountDownTimer timer;

        public MyViewHolder(View itemView) {
            super(itemView);

            view = itemView;

            tvNo = itemView.findViewById(R.id.tvNo);
            tvAddress = itemView.findViewById(R.id.tvAddress);

            tvAddress.setCompoundDrawablePadding(10);
            tvAddress.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(itemView.getContext(), R.drawable.ic_home_black_24dp, android.R.color.holo_blue_dark), null, null, null);

            tvInvoiceNo = itemView.findViewById(R.id.tvInvoiceNo);
            tvInvoiceNo.setCompoundDrawablePadding(10);
            tvInvoiceNo.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(itemView.getContext(), R.drawable.ic_receipt_black_24dp, android.R.color.holo_blue_dark), null, null, null);

            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerName.setCompoundDrawablePadding(10);
            tvCustomerName.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(itemView.getContext(), R.drawable.ic_person_black_24dp, android.R.color.holo_blue_dark), null, null, null);

            tvHandledBy = itemView.findViewById(R.id.tvHandledBy);
            tvHandledBy.setCompoundDrawablePadding(10);
            tvHandledBy.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(itemView.getContext(), R.drawable.ic_build_black_24dp, android.R.color.holo_blue_dark), null, null, null);

            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderStatus.setCompoundDrawablePadding(10);
            tvOrderStatus.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(itemView.getContext(), R.drawable.ic_flag_black_24dp, android.R.color.holo_blue_dark), null, null, null);

            tvServiceTime = itemView.findViewById(R.id.tvServiceTime);
            tvServiceTime.setCompoundDrawablePadding(10);
            tvServiceTime.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(itemView.getContext(), R.drawable.ic_access_time_black_24dp, android.R.color.holo_blue_dark), null, null, null);

            tvCounter = itemView.findViewById(R.id.tvOrderRemaining);
            tvCounter.setCompoundDrawablePadding(10);
            tvCounter.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(itemView.getContext(), R.drawable.ic_timer_black_24dp, android.R.color.holo_blue_dark), null, null, null);

            ivIconStatus = itemView.findViewById(R.id.ivIconStatus);

            btnCallCust = itemView.findViewById(R.id.btnCallCust);
            btnCallTech = itemView.findViewById(R.id.btnCallTech);
            btnChangeTech = itemView.findViewById(R.id.btnChangeTech);
            btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);
            btnCheckStatus = itemView.findViewById(R.id.btnCheckStatus);

        }

        private void changeDisplayTextColor(boolean enabled){
            tvAddress.setTextColor(enabled ? Color.DKGRAY : Color.LTGRAY);
            tvServiceTime.setTextColor(enabled ? Color.DKGRAY : Color.LTGRAY);
            tvCustomerName.setTextColor(enabled ? Color.DKGRAY : Color.LTGRAY);
            tvHandledBy.setTextColor(enabled ? Color.DKGRAY : Color.LTGRAY);
            tvInvoiceNo.setTextColor(enabled ? Color.DKGRAY : Color.LTGRAY);
        }

        public void setData(final OrderBucket data) {
            tvInvoiceNo.setText(data.getInvoiceNo());
            tvAddress.setText(data.getAddressByGoogle());
            tvCustomerName.setText(data.getCustomerName());

            if (data.isServiceTimeFree() && data.getTimeOfService().equals("99:99")) {
                tvServiceTime.setText(Util.prettyDate(mContext, Util.convertStringToDate(data.getDateOfService(), "yyyyMMdd"), true) + " Jam Kerja");
            } else {
                tvServiceTime.setText(Util.prettyTimestamp(mContext, data.getServiceTimestamp()));
//                tvServiceTime.setText(Util.convertDateToString(new Date(data.getServiceTimestamp()), "dd MMM yyyy HH:mm"));
            }

//            if (Util.isExpiredBooking(obj.getTimestamp(), MitraUtil.getMobileSetup().getLastOrderMinutes())) {
//                obj.setStatusDetailId(EOrderDetailStatus.CANCELLED_BY_TIMEOUT.name());
//            }

            EOrderDetailStatus statusDetail = EOrderDetailStatus.convertValue(data.getStatusDetailId());

            lastStatus = statusDetail;

            tvOrderStatus.setText(statusDetail.name());
//            if (status == EOrderDetailStatus.CANCELLED_BY_TIMEOUT)
//            else
//                tvOrderStatus.setText(status == EOrderDetailStatus.CREATED ? "NEW" : status.name());
            tvHandledBy.setText(data.getTechnicianName() == null ? "Unhandled" : data.getTechnicianName());

            tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, android.R.color.primary_text_light));
            tvCounter.setVisibility(View.GONE);

            btnCallTech.setVisibility(View.VISIBLE);
            btnChangeTech.setVisibility(View.INVISIBLE);
            btnCancelOrder.setVisibility(View.INVISIBLE);
            btnCheckStatus.setVisibility(View.INVISIBLE);

            changeDisplayTextColor(true);

            int resIcon;
            switch (EOrderDetailStatus.convertValue(data.getStatusDetailId())) {
                case ASSIGNED:
                    resIcon = R.drawable.ic_assignment_ind_black_24dp;
                    tvHandledBy.setText("Awaiting " + data.getTechnicianName() + " to Start Working...");
                    btnCancelOrder.setVisibility(View.VISIBLE);
//                    btnChangeTech.setVisibility(View.VISIBLE);
                    break;
                case UNHANDLED:
                    resIcon = R.drawable.ic_assignment_late_black_24dp;
                    tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_red_light));
                    tvCounter.setVisibility(View.VISIBLE);
                    btnCallTech.setVisibility(View.INVISIBLE);
                    btnCancelOrder.setVisibility(View.VISIBLE);
                    break;
                case OTW:
                    resIcon = R.drawable.ic_directions_bike_black_24dp;
                    btnCancelOrder.setVisibility(View.VISIBLE);
                    break;
                case WORKING:
                    resIcon = R.drawable.ic_build_black_24dp;
                    break;
                case PAYMENT:
                    resIcon = R.drawable.ic_payment_black_24dp;
                    break;
                case PAID:
                    resIcon = R.drawable.ic_check_black_24dp;
                    break;
                case CANCELLED_BY_CUSTOMER:
                case CANCELLED_BY_TIMEOUT:
                case CANCELLED_BY_SERVER:
                    resIcon = R.drawable.ic_event_busy_black_24dp;
                    btnCallTech.setVisibility(View.INVISIBLE);

                    break;
                default:
                    resIcon = R.drawable.ic_fiber_new_black_24dp;
                    tvCounter.setVisibility(View.VISIBLE);
                    btnCallTech.setVisibility(View.INVISIBLE);
            }

            if (resIcon > -1) {
                Drawable drawable = AppCompatDrawableManager.get().getDrawable(mContext, resIcon);
                ivIconStatus.setImageDrawable(drawable);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onItemSelected(data);
                }
            });

            btnChangeTech.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onChangeTech(data);
                }
            });

            btnCancelOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Util.showInputDialog(mContext, "Alasan Cancel", false, new ListenerGetString() {
                        @Override
                        public void onSuccess(String value) {

                            data.setStatusComment(value);

                            if (mListener != null)
                                mListener.onCancelOrder(data);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

                }
            });

            btnCheckStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // sementara ga perlu krn timer udah disamain dengan customer.
                    // dimana customer.life_per_status_minute = orderbucket.minuteextra
//                    if (mListener != null)
//                        mListener.onCheckStatus(data);
                }
            });
        }

        // start timer dimulai sejak new order masuk, jika dalam 15 menit ga ada yg ambil masuk ke status UNHANDLED
        // caranya cek apakah ada assignment fight-nya, jika tdk ada maka status menjadi unhandled
        // btw, countdown timer hanya berlaku utk layanan segera, bukan layanan terjadwal ?
        public void startTimer(final OrderBucket obj) {

            // spy tdk infinite loop
            if (timer != null) {
                return;
            }

            // one minute adalah gap bahwa tdk ada teknisi yg accept
            // ntah knp new Date bisa kalah duluan sama obj.getupdatedtimestamp

            long startMillis = obj.getCreatedTimestamp() + (obj.getMinuteExtra() * DateUtil.TIME_ONE_MINUTE_MILLIS);
//            long startMillis = obj.getUpdatedTimestamp() + (obj.getMinuteExtra() * DateUtil.TIME_ONE_MINUTE_MILLIS);

            final long expirationMillis = startMillis - new Date().getTime();

            /*
            Date now = new Date();
            long selisih;
            if (obj.getUpdatedTimestamp() < now.getTime())
                selisih = now.getTime() - obj.getUpdatedTimestamp();
            else
                selisih = obj.getUpdatedTimestamp() - now.getTime();

            final long expirationMillis = selisih + DateUtil.TIME_TEN_MINUTE_MILLIS + DateUtil.TIME_ONE_MINUTE_MILLIS;
//            final long expirationMillis = (new Date().getTime() - obj.getOrderStartTimestamp()) + Const.TIME_TEN_MINUTE_MILLIS + Const.TIME_ONE_MINUTE_MILLIS;
//            final long expirationMillis = obj.getTimestamp() + Const.TIME_TEN_MINUTE_MILLIS + Const.TIME_ONE_MINUTE_MILLIS;
*/
            timer = new CountDownTimer(expirationMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String ss = DateUtil.formatMillisToMinutesSeconds(millisUntilFinished);
//                    String ss = Util.convertDateToString(new Date(millisUntilFinished), "mm:ss");
                    tvCounter.setText("Broadcast technicians [" + ss + "]");

                    if (expirationMillis < (60 * 1000)) {
                        ColorUtil.setTextColorAsRed(mContext, tvCounter);
                    }
//                    tvCounter.setText(DateUtil.formatDateToSimple(millisUntilFinished));
                }


                @Override
                public void onFinish() {
                    if (mList.size() < 1)
                        return;

                    // utk mencegah status jadi expired krn timer diproses di thread lainnya yg gw ga tau
                    boolean orderItemMasihAda = false;
                    try {
                        for (OrderBucket ob : mList) {

                            String _itemUid = ob.getUid();
                            String _uid = obj.getUid();

                            if (_itemUid.equals(_uid)) {
                                orderItemMasihAda = true;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // kalau udah ga ada ga perlu cek status lagi biar ga ada yg nyantol
                    if (!orderItemMasihAda)
                        return;

                    if (lastStatus != EOrderDetailStatus.CREATED)
                        return;

                    tvCounter.setText("No Technicians accept the offer.");

                    if (timer != null)
                        btnCheckStatus.performClick();

                    // dipindah ke cloud. jd timer disini cuma display doankg
//                    FBUtil.Order_SetStatus(mMitraId, obj.getCustomerId(), obj.getUid(), null, null, EOrderDetailStatus.UNHANDLED, String.valueOf(Const.USER_AS_MITRA), null);
                }
            }.start();

        }

        public void stopTimer(OrderBucket obj) {
            if (timer == null)
                return;

            timer.cancel();
        }
    }
}

