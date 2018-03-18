package com.elkana.teknisi.screen.payment;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.pojo.technician.ServiceItem;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 13-Nov-17.
 */

public class RVAdapterPayment extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = RVAdapterPayment.class.getSimpleName();

    private Context mContext;
    private List<ServiceItem> mList = new ArrayList<>();

    private ListenerPaymentList mListener;

    public RVAdapterPayment(Context context, String technicianId, String assignmentId, ListenerPaymentList listener) {
        mContext = context;

        if (assignmentId == null || technicianId == null)
            return;

        mListener = listener;

        final AlertDialog dialog = Util.showProgressDialog(mContext, "Saving Items");

        FBUtil.Assignment_getServiceItemsRef(technicianId,assignmentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialog.dismiss();

                // bisa saja kosong karena ga ada yg diservice.konfirmasi ke bisnis gmn maunya apa perlu kena charge ?
                if (!dataSnapshot.exists()) {
                    if (mListener != null)
                        mListener.onCalculateTotalFare(0L);
                    return;
                }

                GenericTypeIndicator<ArrayList<ServiceItem>> t = new GenericTypeIndicator<ArrayList<ServiceItem>>() {
                };
                final List<ServiceItem> list = (List<ServiceItem>) dataSnapshot.getValue(t);


                double sum = 0;

                for (ServiceItem obj : list) {
                    sum += new Double(obj.getCount()) * obj.getRate();
                    mList.add(obj);
                }

                if (mListener != null)
                    mListener.onCalculateTotalFare(new Double(sum).longValue());

                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();

                Log.e(TAG, databaseError.getMessage(), databaseError.toException());

            }
        });

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_ac_payment_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ServiceItem obj = mList.get(position);

        ((MyViewHolder) holder).etServiceItem.setText(obj.getServiceLabel());
        ((MyViewHolder) holder).tvQuantity.setText(mContext.getString(R.string.label_quantity, obj.getCount()));
        ((MyViewHolder) holder).tvPricePerItem.setText("@" + String.valueOf(Util.convertLongToRupiah((long)obj.getRate())));

        Double sum =  obj.getCount() * obj.getRate();

        ((MyViewHolder) holder).tvPrice.setText("Harga: " + Util.convertLongToRupiah(sum.longValue()));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public EditText etServiceItem;
        public TextView tvQuantity;
        public TextView tvPricePerItem;
        public TextView tvPrice;

        public MyViewHolder(View itemView) {
            super(itemView);

            etServiceItem = itemView.findViewById(R.id.etServiceItem);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPricePerItem = itemView.findViewById(R.id.tvPricePerItem);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
