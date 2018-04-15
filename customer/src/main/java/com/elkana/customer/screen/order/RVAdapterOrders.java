package com.elkana.customer.screen.order;

import android.content.Context;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elkana.customer.R;
import com.elkana.customer.pojo.MobileSetup;
import com.elkana.customer.util.CustomerUtil;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.mitra.Mitra;
import com.elkana.dslibrary.util.ColorUtil;
import com.elkana.dslibrary.util.DateUtil;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.EOrderStatus;
import com.elkana.dslibrary.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Eric on 05-Oct-17.
 */
// RV: adapter for RecyclerView
//    https://stackoverflow.com/questions/29106484/how-to-add-a-button-at-the-end-of-recyclerview
public class RVAdapterOrders extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = RVAdapterOrders.class.getSimpleName();

    private final int VIEW_TYPE_CELL = 1;
    private final int VIEW_TYPE_FOOTER = 2;
    private final Typeface fontFace;
    private DatabaseReference mDatabaseReference;

    private ValueEventListener mValueEventListener;

    private Context mContext;
    private String mCustomerId;
    private List<OrderHeader> mList = new ArrayList<>();
    private ListenerOrderList mListener;

    /*
    public RVAdapterOrders(Context context, List<OrderHeader> list, ListenerOrderList listener) {
        this.mContext = context;
        this.mListener = listener;

        this.mList.addAll(list);
    }*/

    public RVAdapterOrders(Context context, DatabaseReference ref, String customerId, ListenerOrderList listener) {
        mContext = context;

        mDatabaseReference = ref;
        mListener = listener;
        mCustomerId = customerId;

        fontFace = Typeface.createFromAsset(this.mContext.getAssets(),
                "fonts/DinDisplayProLight.otf");

        getDataLocal();   //jgn disable meskpipun risikonya dipanggil 2x. spy tdk flicker

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    return;

                if (Util.TESTING_MODE)
                    Log.e(TAG, "valueEventListener.DataChange for RVAdapterOrders");

                final Realm r = Realm.getDefaultInstance();
                try {
                    r.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                OrderHeader _obj = postSnapshot.getValue(OrderHeader.class);
                                Log.e(TAG, "DataChange:" + _obj.toString());

                                realm.copyToRealmOrUpdate(_obj);
                            }
                        }
                    });

                } finally{
                    r.close();
                }

                getDataLocal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
            }
        };

        ref.addValueEventListener(valueEventListener);

        mValueEventListener = valueEventListener;
    }

    private void getDataLocal() {
        mList.clear();

        Realm r = Realm.getDefaultInstance();

        try{
            RealmResults<OrderHeader> all = r.where(OrderHeader.class)
                    .equalTo("customerId", mCustomerId)
//                    .notEqualTo("statusId", EOrderStatus.FINISHED.name())
                    .findAll();

            MobileSetup config = r.where(MobileSetup.class).findFirst();

            for (int i = 0; i < all.size(); i++){
                OrderHeader obj = all.get(i);

                EOrderStatus status = EOrderStatus.convertValue(obj.getStatusId());

                if (status == EOrderStatus.FINISHED) {

                    long _expiredTime = DateUtil.isExpiredTime(obj.getUpdatedTimestamp(), config.getRemove_order_age_hours() * DateUtil.TIME_ONE_HOUR_MINUTES);

                    if (_expiredTime > 0)
                        continue;
                }
                mList.add(obj);
            }

//            mList.addAll(r.copyFromRealm(all));
        }finally {
            r.close();
        }

        notifyDataSetChanged();

        if (mListener != null)
            mListener.onUpdateOrder(mCustomerId);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CELL) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_ac_order_list, parent, false);
            return new MyViewHolder(itemView);
        } else {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_ac_order_list_add, parent, false);
            return new MyAddOrderHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (position == mList.size()) {
        } else {

            final OrderHeader obj = mList.get(position);
            ((MyViewHolder) holder).setData(obj);

            EOrderDetailStatus detailStatus = EOrderDetailStatus.convertValue(obj.getStatusDetailId());

            /* TODO membingungkan, gmn cara start timer tiap status baru berubah ?
            if (detailStatus == EOrderDetailStatus.CREATED)
                // masih experiment
                ((MyViewHolder) holder).startTimer(obj);
            else
                ((MyViewHolder) holder).stopTimer(obj);
                */
        }

    }

    @Override
    public int getItemViewType(int position) {
        return (position == mList.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL; // super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    public void cleanUpListener() {
        if (mValueEventListener != null) {
            mDatabaseReference.removeEventListener(mValueEventListener);
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvLabel, tvAddress, tvStatus, tvMitra, tvDateOfService, tvTimer;
//        public ImageButton btnCancelOrder;
        public FloatingActionButton fabCancelOrder;

        private boolean showTimer;

        CountDownTimer timer;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvLabel = itemView.findViewById(R.id.tvLabel);
            tvLabel.setTypeface(fontFace);
//            tvLabelAddress.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(mContext, R.drawable.ic_home_black_24dp, android.R.color.white), null, null, null);
            tvMitra = itemView.findViewById(R.id.tvMitra);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvStatus.setTypeface(fontFace);
            tvDateOfService = itemView.findViewById(R.id.tvDateOfService);
            tvTimer = itemView.findViewById(R.id.tvTimer);

//            btnCancelOrder = (ImageButton) itemView.findViewById(R.id.btnCancelOrder);
//            btnCancelOrder.setImageResource(R.drawable.ic_indeterminate_check_box_black_24dp);
//            btnCancelOrder.setColorFilter(Color.parseColor("#000000"));
            fabCancelOrder = itemView.findViewById(R.id.fabCancelOrder);

        }

        public void setData(final OrderHeader data){
            tvLabel.setText(mContext.getString(R.string.row_label_ac_service, data.getJumlahAC()));
            tvAddress.setText(data.getAddressId());
            tvDateOfService.setText(mContext.getString(R.string.prompt_schedule) + ": " + Util.prettyTimestamp(mContext, data.getBookingTimestamp()));

            Realm r = Realm.getDefaultInstance();
            try{

                Mitra mitra = CustomerUtil.lookUpMitraById(r, data.getPartyId());
//                Mitra mitra = CustomerUtil.lookUpMitra(r, Long.parseLong(obj.getPartyId()));

                if (mitra != null) {
                    tvMitra.setText(mContext.getString(R.string.row_order_mitra, mitra.getName()));
                }

                EOrderDetailStatus orderStatus = EOrderDetailStatus.convertValue(data.getStatusDetailId());

                MobileSetup config = r.where(MobileSetup.class).findFirst();
                // too long ?
                long _expiredTime = DateUtil.isExpiredTime(data.getUpdatedTimestamp(), config.getStatus_unhandled_minutes());

                if (orderStatus == EOrderDetailStatus.UNHANDLED && _expiredTime > 0) {
                    tvStatus.setText(mContext.getString(R.string.status_unhandled_timeout));
                } else
                    tvStatus.setText(CustomerUtil.getMessageStatusDetail(mContext, orderStatus));


                showTimer = config.isShow_timer();

                tvTimer.setVisibility(View.INVISIBLE);  // kondisi awal
            }finally {
                r.close();
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onItemOrderSelected(data);
                }
            });
            fabCancelOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onCancelOrder(data);

                }
            });

            /*
            ((MyViewHolder) holder).tvLabelAddress.setText(obj.getAddress());

            ((MyViewHolder) holder).btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(mContext)
                            .setTitle(mContext.getString(R.string.title_delete_address))
                            .setMessage(obj.getAddress())
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mList.remove(position);

                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }
            });
            */

        }

        public void startTimer(OrderHeader obj) {
            if (timer != null) {
                return;
            }

            // if somehow customer have a right to cancel, lets say 60 minutes
            long startMillis = obj.getUpdatedTimestamp() + (obj.getLife_per_status_minute() * DateUtil.TIME_ONE_MINUTE_MILLIS);

            final long expirationMillis = startMillis - new Date().getTime();

            timer = new CountDownTimer(expirationMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String ss = DateUtil.formatMillisToMinutesSeconds(millisUntilFinished);
//                    String ss = Util.convertDateToString(new Date(millisUntilFinished), "mm:ss");
                    tvTimer.setText(ss);

                    if (expirationMillis < (5 * 1000)) {
                        ColorUtil.setTextColorAsRed(mContext, tvTimer);
                    }
                }


                @Override
                public void onFinish() {
                    if (mList.size() < 1)
                        return;

                    // dipindah ke cloud. jd timer disini cuma display doankg
//                    FBUtil.Order_SetStatus(mMitraId, obj.getCustomerId(), obj.getUid(), null, null, EOrderDetailStatus.UNHANDLED, String.valueOf(Const.USER_AS_MITRA), null);
                }
            }.start();

            if (showTimer)
                tvTimer.setVisibility(View.VISIBLE);
        }

        public void stopTimer(OrderHeader obj) {
            if (timer == null)
                return;

            timer.cancel();

            tvTimer.setVisibility(View.INVISIBLE);
        }
    }

    class MyAddOrderHolder extends RecyclerView.ViewHolder {
//        public Button btn;
        public FloatingActionButton fab;

        public MyAddOrderHolder(View itemView) {
            super(itemView);

            fab = itemView.findViewById(R.id.fabAddService);
            /*
            btn = itemView.findViewById(R.id.btnAddService);
            btn.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(mContext, R.drawable.ic_add_black_24dp, android.R.color.black), null, null, null);


            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onAddOrder();
                    }
                }
            });
            */

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onAddOrder();
                    }
                }
            });
        }
    }

}
