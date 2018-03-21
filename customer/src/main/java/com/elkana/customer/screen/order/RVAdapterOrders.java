package com.elkana.customer.screen.order;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.elkana.customer.R;
import com.elkana.customer.util.CustomerUtil;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.mitra.Mitra;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.EOrderStatus;
import com.elkana.dslibrary.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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

    private Context ctx;
    private String mCustomerId;
    private List<OrderHeader> mList = new ArrayList<>();
    private ListenerOrderList mListener;

    /*
    public RVAdapterOrders(Context context, List<OrderHeader> list, ListenerOrderList listener) {
        this.ctx = context;
        this.mListener = listener;

        this.mList.addAll(list);
    }*/

    public RVAdapterOrders(Context context, DatabaseReference ref, String customerId, ListenerOrderList listener) {
        this.ctx = context;

        mDatabaseReference = ref;
        mListener = listener;
        mCustomerId = customerId;

        fontFace = Typeface.createFromAsset(this.ctx.getAssets(),
                "fonts/DinDisplayProLight.otf");

        getDataLocal();

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
                    .notEqualTo("statusId", EOrderStatus.FINISHED.name())
                    .findAll();

            mList.addAll(r.copyFromRealm(all));
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

            ((MyViewHolder) holder).tvLabel.setText(ctx.getString(R.string.row_label_ac_service, obj.getJumlahAC()));
            ((MyViewHolder) holder).tvAddress.setText(obj.getAddressId());
            ((MyViewHolder) holder).tvDateOfService.setText(ctx.getString(R.string.prompt_schedule) + ": " + Util.prettyTimestamp(ctx, obj.getTimestamp()));

            Realm r = Realm.getDefaultInstance();
            try{

                Mitra mitra = CustomerUtil.lookUpMitraById(r, obj.getPartyId());
//                Mitra mitra = CustomerUtil.lookUpMitra(r, Long.parseLong(obj.getPartyId()));

                if (mitra != null) {
                    ((MyViewHolder) holder).tvMitra.setText(ctx.getString(R.string.row_order_mitra, mitra.getName()));
                }


                EOrderDetailStatus orderStatus = EOrderDetailStatus.convertValue(obj.getStatusDetailId());

                ((MyViewHolder) holder).tvStatus.setText(CustomerUtil.getMessageStatusDetail(ctx, orderStatus));

            }finally {
                r.close();
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onItemOrderSelected(obj);
                }
            });
            ((MyViewHolder) holder).fabCancelOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onCancelOrder(obj);

                }
            });

            /*
            ((MyViewHolder) holder).tvLabelAddress.setText(obj.getAddress());

            ((MyViewHolder) holder).btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(ctx)
                            .setTitle(ctx.getString(R.string.title_delete_address))
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
        public TextView tvLabel, tvAddress, tvStatus, tvMitra, tvDateOfService;
//        public ImageButton btnCancelOrder;
        public FloatingActionButton fabCancelOrder;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvLabel = itemView.findViewById(R.id.tvLabel);
            tvLabel.setTypeface(fontFace);
//            tvLabelAddress.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(ctx, R.drawable.ic_home_black_24dp, android.R.color.white), null, null, null);
            tvMitra = itemView.findViewById(R.id.tvMitra);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvStatus.setTypeface(fontFace);
            tvDateOfService = itemView.findViewById(R.id.tvDateOfService);

//            btnCancelOrder = (ImageButton) itemView.findViewById(R.id.btnCancelOrder);
//            btnCancelOrder.setImageResource(R.drawable.ic_indeterminate_check_box_black_24dp);
//            btnCancelOrder.setColorFilter(Color.parseColor("#000000"));
            fabCancelOrder = itemView.findViewById(R.id.fabCancelOrder);

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
            btn.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(ctx, R.drawable.ic_add_black_24dp, android.R.color.black), null, null, null);


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
