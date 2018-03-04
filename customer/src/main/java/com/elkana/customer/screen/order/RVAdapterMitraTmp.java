package com.elkana.customer.screen.order;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.elkana.customer.R;
import com.elkana.dslibrary.pojo.mitra.TmpMitra;

import co.moonmonkeylabs.realmsearchview.RealmSearchAdapter;
import co.moonmonkeylabs.realmsearchview.RealmSearchViewHolder;
import io.realm.Realm;

/**
 * Created by Eric on 05-Oct-17.
 */
// RV: adapter for RecyclerView
public class RVAdapterMitraTmp extends RealmSearchAdapter<TmpMitra, RVAdapterMitraTmp.MyViewHolder> {

    private Context ctx;
    private FragmentMitraListInRange.OnFragmentMitraListInRangeInteractionListener listener;

    public RVAdapterMitraTmp(@NonNull Context context, @NonNull Realm realm, @NonNull String filterKey, FragmentMitraListInRange.OnFragmentMitraListInRangeInteractionListener listener) {
        super(context, realm, filterKey);
        this.ctx = context;
        this.listener = listener;
    }


    @Override
    public MyViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_mitra, viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindRealmViewHolder(MyViewHolder holder, int position) {
        final TmpMitra obj = realmResults.get(position);
//        final Mitra obj = theList.get(position);

//        holder.tvLabel.setTextColor(ContextCompat.getColor(context, obj.isEnabled() ? R.color.cardTextColor : R.color.cardTextColorDisabled));
        holder.tvLabel.setText(obj.getName());
        holder.tvAddress.setText(obj.getAddressLabel());
        holder.tvPhone.setText(obj.getPhone1());
        holder.tvRating.setText("Rating: " + String.valueOf(obj.getRating()));
        holder.tvWorkingHours.setText(obj.getWorkingHourStart() + ":00 - " + obj.getWorkingHourEnd() + ":00");

        holder.view.setEnabled(obj.isEnable());
        holder.tvLabel.setTextColor(obj.isEnable() ? Color.BLACK : Color.LTGRAY);
        holder.tvAddress.setTextColor(obj.isEnable() ? Color.BLACK : Color.LTGRAY);
        holder.tvPhone.setTextColor(obj.isEnable() ? Color.BLACK : Color.LTGRAY);
        holder.tvRating.setTextColor(obj.isEnable() ? Color.BLACK : Color.LTGRAY);
        holder.tvWorkingHours.setTextColor(obj.isEnable() ? Color.BLACK : Color.LTGRAY);
    }

    public class MyViewHolder extends RealmSearchViewHolder {
        public TextView tvLabel, tvAddress, tvRating, tvPhone, tvWorkingHours;
        public View view;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            tvLabel = view.findViewById(R.id.tvLabel);
            tvAddress = view.findViewById(R.id.tvAddress);
            tvRating = view.findViewById(R.id.tvRating);
            tvPhone = view.findViewById(R.id.tvPhone);
            tvWorkingHours = view.findViewById(R.id.tvWorkingHours);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // Check if an item was deleted, but the user clicked it before the UI removed it
                    if (position == RecyclerView.NO_POSITION) return;

                    TmpMitra obj = realmResults.get(position);

                    if (!obj.isEnable()){
                        Toast.makeText(ctx, ctx.getString(R.string.error_mitra_not_available, obj.getName()), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (listener != null) {
                        listener.onMitraSelected(obj);
                    }

                }
            });
        }
    }

}

