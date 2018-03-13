package com.elkana.ds.mitraapp.screen.assign;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.elkana.ds.mitraapp.R;
import com.elkana.dslibrary.pojo.mitra.TechnicianReg;

import co.moonmonkeylabs.realmsearchview.RealmSearchAdapter;
import co.moonmonkeylabs.realmsearchview.RealmSearchViewHolder;
import io.realm.Realm;

/**
 * Created by Eric on 05-Oct-17.
 */
// RV: adapter for RecyclerView
// Due to Searching capability, dont use firebasedatabase, use local Realm DB only.
public class RSVAdapterTechnicianReg extends RealmSearchAdapter<TechnicianReg, RSVAdapterTechnicianReg.MyViewHolder> {
    private static final String TAG = RSVAdapterTechnicianReg.class.getSimpleName();

    private Context mContext;
    private ListenerTechnicianList mListener;

    public RSVAdapterTechnicianReg(@NonNull Context context, @NonNull Realm realm, @NonNull String filterKey, ListenerTechnicianList listener) {
        super(context, realm, filterKey);
        this.mContext = context;
        this.mListener = listener;

//        FirebaseDatabase.getInstance().getReference(DataUtil.REF_MI)
    }


    @Override
    public MyViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_technician, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindRealmViewHolder(MyViewHolder holder, int position) {
        final TechnicianReg obj = realmResults.get(position);
//        final Mitra obj = theList.get(position);

//        holder.tvLabel.setTextColor(ContextCompat.getColor(context, obj.isEnabled() ? R.color.cardTextColor : R.color.cardTextColorDisabled));
        holder.tvLabel.setText(obj.getName());
        holder.tvOrderTodayCount.setText(mContext.getString(R.string.label_order_today_count, obj.getOrderTodayCount()));
        /*
        holder.tvAddress.setText(obj.getAddress());
        holder.tvWorkingHours.setText(obj.getWorkingHourStart() + ":00 - " + obj.getWorkingHourEnd() + ":00");
        holder.tvPhone.setText(obj.getPhone1());
        holder.tvRating.setText("Rating: " + String.valueOf(obj.getRating()));

        holder.view.setEnabled(obj.isEnable());
        holder.tvLabel.setTextColor(obj.isEnable() ? Color.BLACK : Color.LTGRAY);
        holder.tvAddress.setTextColor(obj.isEnable() ? Color.BLACK : Color.LTGRAY);
        holder.tvPhone.setTextColor(obj.isEnable() ? Color.BLACK : Color.LTGRAY);
        holder.tvRating.setTextColor(obj.isEnable() ? Color.BLACK : Color.LTGRAY);
        holder.tvWorkingHours.setTextColor(obj.isEnable() ? Color.BLACK : Color.LTGRAY);
        */
    }

    public class MyViewHolder extends RealmSearchViewHolder {
        public TextView tvLabel, tvAddress, tvRating, tvPhone, tvWorkingHours, tvOrderTodayCount;
        public View view;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            tvLabel = view.findViewById(R.id.tvLabel);
            tvAddress = view.findViewById(R.id.tvAddress);
            tvRating = view.findViewById(R.id.tvRating);
            tvPhone = view.findViewById(R.id.tvPhone);
            tvWorkingHours = view.findViewById(R.id.tvWorkingHours);
            tvOrderTodayCount = view.findViewById(R.id.tvOrderTodayCount);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // Check if an item was deleted, but the user clicked it before the UI removed it
                    if (position == RecyclerView.NO_POSITION) return;

                    TechnicianReg obj = realmResults.get(position);

                    if (obj.isSuspend()){
                        Toast.makeText(mContext, mContext.getString(R.string.error_technician_not_available), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (mListener != null) {
                        mListener.onItemSelected(obj);
                    }

                }
            });
        }
    }

}

