package com.elkana.teknisi.screen.order;

import android.app.AlertDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.pojo.mitra.NotifyNewOrderItem;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.DateUtil;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.R;
import com.elkana.teknisi.pojo.MobileSetup;
import com.elkana.teknisi.util.TeknisiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eric on 01-Dec-17.
 */

public class RVAdapterNotifyNewOrderList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = RVAdapterNotifyNewOrderList.class.getSimpleName();

    private Context mContext;
    private String mMitraId;
    private String mTechId;
    private String mTechName;

    private List<NotifyNewOrderItem> mList = new ArrayList<>();

    private ChildEventListener mNotifyNewOrderListener;
    private DatabaseReference mNotifyNewOrderRef;
    private ListenerNotifyNewOrderList mListener;

    public RVAdapterNotifyNewOrderList(Context ctx, String mitraId, String techId, String techName, ListenerNotifyNewOrderList listener) {
        mContext = ctx;
        mListener = listener;
        mMitraId = mitraId;
        mTechId = techId;
        mTechName = techName;

        // listen to all notify_new_order
        mNotifyNewOrderListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                NotifyNewOrderItem _obj = dataSnapshot.getValue(NotifyNewOrderItem.class);
                Log.e(TAG, ">>>>>child added " + _obj);

                MobileSetup mobileSetup = TeknisiUtil.getMobileSetup();
                //disable krn msh testing new order
                // hanya ambil yg belum expired. dikurangi 1 menit utk toleransi klik/koneksi. misal diinfo 10 menit timer, tp sebenere cuma dikasih 9 menit utk tampil di layar
//                if (Util.isExpiredBooking(_obj.getOrderTimestamp(), mobileSetup.getExpiryLastOrderMinutes() - 1)) {
                    // delete
//                    FBUtil.TechnicianReg_DeleteNotifyNewOrder(mMitraId, mTechId, _obj.getOrderId(), null);
//                    return;
//                }

                mList.add(_obj);
                notifyDataSetChanged();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, ">>>>>>>>>child changed");

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                NotifyNewOrderItem _obj = dataSnapshot.getValue(NotifyNewOrderItem.class);
                Log.e(TAG, ">>>>>>>>child removed " + _obj);

                boolean changed = false;
                for (int i = 0; i < mList.size(); i++) {
                    if (!mList.get(i).getOrderId().equals(_obj.getOrderId()))
                        continue;

                    mList.remove(i);

                    changed = true;
                    break;
                }
                notifyDataSetChanged();

                if (changed && mListener != null)
                    mListener.onOrderRemoved(_obj);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, ">>>>>>>>>>>child moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
            }
        };

        mNotifyNewOrderRef = FBUtil.Mitra_GetTechnicianRef(mitraId, techId)
                .child("notify_new_order");

        mNotifyNewOrderRef.addChildEventListener(mNotifyNewOrderListener);

    }

    public void cleanUpListener() {
        if (mNotifyNewOrderListener != null) {
            mNotifyNewOrderRef.removeEventListener(mNotifyNewOrderListener);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notify_new_order_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final NotifyNewOrderItem obj = mList.get(position);

        ((MyViewHolder) holder).setData(obj);

        ((MyViewHolder) holder).startTimer(obj);

//
//        if (EOrderDetailStatus.convertValue(obj.getStatusDetailId()) == EOrderDetailStatus.CANCELLED_BY_TIMEOUT) {
//            ((MyViewHolder) holder).tvOrderRemaining.setText("Expired!!");
//        }
//
//        if (Util.isExpiredBooking(obj.getTimestamp(), 0)) {
//            ((MyViewHolder) holder).tvOrderRemaining.setText("Expired!!");
//
//            // TODO: update firebase db
//
//            return;
//        }


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNewOrderMsg, tvCounter, tvPleaseAcceptNewOrder, tvOrderInfo;
        public View view;
        public FloatingActionButton btnDenyOrder, btnTakeOrder, btnCloseExpired;

        CountDownTimer timer;

        public MyViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            tvNewOrderMsg = itemView.findViewById(R.id.tvNewOrderMsg);
            tvPleaseAcceptNewOrder = itemView.findViewById(R.id.tvPleaseAcceptNewOrder);
            tvCounter = itemView.findViewById(R.id.tvCounter);
            tvOrderInfo = itemView.findViewById(R.id.tvOrderInfo);

//            tvAddress.setCompoundDrawablePadding(10);
//            tvAddress.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(itemView.getContext(), R.drawable.ic_home_black_24dp, android.R.color.holo_blue_dark), null, null, null);
            btnDenyOrder = itemView.findViewById(R.id.btnDenyOrder);

            btnTakeOrder = itemView.findViewById(R.id.btnTakeOrder);

            btnCloseExpired = itemView.findViewById(R.id.btnCloseExpired);

//            btnGiveUpOrDo = itemView.findViewById(R.id.btnGiveUpOrDo);


        }

        public void setData(final NotifyNewOrderItem data) {

            tvNewOrderMsg.setText("Pesanan baru datang !");
//            tvAddress.setText(data.getAddressByGoogle());
//            tvCustomerName.setText(data.getCustomerName());
//            tvOrderTime.setText(Util.convertDateToString(new Date(data.getTimestamp()), "dd MMM yyyy HH:mm"));
            StringBuilder sb = new StringBuilder();
            sb.append("Jadwal : ").append(DateUtil.displayTimeInJakarta(data.getOrderTimestamp(), "dd MMM yyyy HH:mm")).append("\n");
            sb.append("Jumlah AC : ").append(data.getAcCount()).append("\n");
//            sb.append("Alamat : ").append(data.getAddress()).append("\n");
            sb.append(data.getAddress()).append("\n");
            sb.append(data.getCustomerName()).append("\n");
//            sb.append("MitraTimestamp : ").append(DateUtil.displayTimeInJakarta(data.getMitraTimestamp(), "dd MMM yyyy HH:mm:ss")).append(data.getMitraTimestamp()).append("\n");
//            sb.append("Timestamp : ").append(DateUtil.displayTimeInJakarta(data.getTimestamp(), "dd MMM yyyy HH:mm:ss")). append(data.getTimestamp()).append("\n");
//            sb.append("Nama : ").append(data.getCustomerName()).append("\n");
            tvOrderInfo.setText(sb.toString());

            btnCloseExpired.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FBUtil.TechnicianReg_deleteNotifyNewOrder(mMitraId, mTechId, data.getOrderId(), new ListenerModifyData() {
                        @Override
                        public void onSuccess() {
                            if (mListener == null)
                                return;

                            mListener.onDeny(data);
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            btnDenyOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Util.showDialogConfirmation(mContext, "Tolak Order", "Anda yakin tolak dan coba lain kali ?", new ListenerPositiveConfirmation() {
                        @Override
                        public void onPositive() {
                            timer.cancel();

                            btnCloseExpired.performClick();
                        }
                    });
                }
            });

            btnTakeOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog alertDialog = Util.showProgressDialog(mContext, "Taking Order");

                    // chek dulu apkh uda ambil order di jam yg sama? caranya cek di slot per teknisi

                    //check already taken ? assume device is offline intention
                    final DatabaseReference _fightRef = FBUtil.Assignment_fight(data.getOrderId())
                            .child("techId");

                    _fightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // kalo ada brarti udah keambil

                            if (dataSnapshot.exists()) {
                                alertDialog.dismiss();
                                return;
                            }

                            //cocokkan dengan FightInfo, pake manual krn butuh timestampnya
                            final Map<String, Object> keyValOrder = new HashMap<>();
                            keyValOrder.put("techId", mTechId);
                            keyValOrder.put("techName", mTechName);
                            keyValOrder.put("orderId", data.getOrderId());
                            keyValOrder.put("custId", data.getCustomerId());
                            keyValOrder.put("timestamp", ServerValue.TIMESTAMP);

                            FBUtil.Assignment_fight(data.getOrderId())
                                    .setValue(keyValOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    alertDialog.dismiss();
                                    // stop timer
                                    timer.cancel();

                                    if (mListener != null)
                                        mListener.onAccept(data);

                                    // hapus notify_new_order, dipindah di mitra saja krn lbh stabil inetnya
                                    /*
                                    FBUtil.TechnicianReg_DeleteNotifyNewOrder(mMitraId, mTechId, data.getOrderId(), new ListenerModifyData() {
                                        @Override
                                        public void onSuccess() {
                                            if (mListener == null)
                                                return;

                                            mListener.onAccept(data);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    */
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            alertDialog.dismiss();
                            Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                        }
                    });
                }
            });

        }

        public void startTimer(NotifyNewOrderItem obj) {
//            final long expirationMillis = Const.TIME_TEN_MINUTE_MILLIS;
//            final long expirationMillis = 2 * 60 * 1000;

            if (timer != null)
                return;

            // harusnya dari sisi teknisi dikurangi 1 menit
            long selisih = (obj.getTimestamp() - new Date().getTime());
//            long selisih = (obj.getUpdaMitraTimestamp() - new Date().getTime());
            final long expirationMillis = selisih + DateUtil.TIME_TEN_MINUTE_MILLIS;
//            final long expirationMillis = (obj.getTimestamp() - new Date().getTime()) + Const.TIME_TEN_MINUTE_MILLIS + Const.TIME_ONE_MINUTE_MILLIS;
            String start = DateUtil.formatMillisToMinutesSeconds(expirationMillis);

//            final long expirationMillis = (new Date().getTime() - obj.getTimestamp());

            timer = new CountDownTimer(expirationMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String ss = Util.convertDateToString(new Date(millisUntilFinished), "mm:ss");
                    tvCounter.setText(ss);

                    if (expirationMillis < (5 * 1000)) {
                        tvCounter.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_red_light));
                    }
                }

                @Override
                public void onFinish() {
                    tvCounter.setText("Expired!!");
                    btnDenyOrder.setVisibility(View.INVISIBLE);
                    btnTakeOrder.setVisibility(View.INVISIBLE);
                    btnCloseExpired.setVisibility(View.VISIBLE);

                    if (mListener != null)
                        mListener.onTimesUp();

                }
            }.start();

            if (mListener != null)
                mListener.onTimerStart();
        }
    }
}

