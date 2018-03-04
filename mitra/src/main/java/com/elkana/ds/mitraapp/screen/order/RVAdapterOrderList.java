package com.elkana.ds.mitraapp.screen.order;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.elkana.ds.mitraapp.R;
import com.elkana.ds.mitraapp.util.DataUtil;
import com.elkana.dslibrary.pojo.OrderBucket;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 01-Dec-17.
 */

public class RVAdapterOrderList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = RVAdapterOrderList.class.getSimpleName();

    private DatabaseReference orders4MitraRef;
    private Context mContext;
    private List<OrderBucket> mList = new ArrayList<>();

    private ValueEventListener mValueEventListener;
    private ListenerOrderList mListener;

    public RVAdapterOrderList(Context ctx, String mitraId, ListenerOrderList listener) {
        mContext = ctx;

        if (mitraId == null) {
            return;
        }

        mListener = listener;

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();

//                if (!dataSnapshot.exists())
//                    return;

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    OrderBucket _obj = postSnapshot.getValue(OrderBucket.class);
                    Log.e(TAG, "DataChange:" + _obj.toString());

                    mList.add(_obj);
                }

                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        orders4MitraRef = FirebaseDatabase.getInstance().getReference(DataUtil.REF_ORDERS_MITRA_AC_PENDING)
                .child(mitraId);

        orders4MitraRef.addValueEventListener(mValueEventListener);

    }

    public void cleanUpListener() {
        if (mValueEventListener != null) {
            orders4MitraRef.removeEventListener(mValueEventListener);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_new_order_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final OrderBucket obj = mList.get(position);

        ((MyViewHolder) holder).tvAddress.setText(obj.getAddressByGoogle());
        ((MyViewHolder) holder).tvCustomerName.setText(obj.getCustomerName());

        if (Util.isExpiredOrder(obj.getTimestamp(), DataUtil.getMobileSetup().getLastOrderMinutes())) {
            obj.setStatusDetailId(EOrderDetailStatus.CANCELLED_BY_TIMEOUT.name());
        }

        EOrderDetailStatus status = EOrderDetailStatus.convertValue(obj.getStatusDetailId());

        if (status == EOrderDetailStatus.CANCELLED_BY_TIMEOUT)
            ((MyViewHolder) holder).tvOrderStatus.setText("EXPIRED");
        else
            ((MyViewHolder) holder).tvOrderStatus.setText(status == EOrderDetailStatus.CREATED ? "NEW" : status.name());

        int resIcon = -1;
        switch (EOrderDetailStatus.convertValue(obj.getStatusDetailId())) {
            case ASSIGNED:
                resIcon = R.drawable.ic_assignment_ind_black_24dp;
                break;
            case OTW:
                resIcon = R.drawable.ic_directions_bike_black_24dp;
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
                break;
            default:
                resIcon = R.drawable.ic_fiber_new_black_24dp;
        }
        if (resIcon > -1) {
            Drawable drawable = AppCompatDrawableManager.get().getDrawable(mContext, resIcon);
            ((MyViewHolder) holder).ivIconStatus.setImageDrawable(drawable);
        }

        ((MyViewHolder) holder).tvHandledBy.setText(obj.getTechnicianName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onItemSelected(obj);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvAddress, tvCustomerName, tvHandledBy, tvOrderStatus;
        public View view;
        public ImageView ivIconStatus;

        public MyViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvHandledBy = itemView.findViewById(R.id.tvHandledBy);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            ivIconStatus = itemView.findViewById(R.id.ivIconStatus);
        }
    }
}

