package com.elkana.teknisi.screen.profile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elkana.dslibrary.pojo.mitra.Mitra;
import com.elkana.teknisi.R;

import co.moonmonkeylabs.realmsearchview.RealmSearchAdapter;
import co.moonmonkeylabs.realmsearchview.RealmSearchViewHolder;
import io.realm.Realm;

/**
 * Created by Eric on 14-Dec-17.
 */
// Due to Searching capability, dont use firebasedatabase, use local Realm DB only.
public class RSVAdapterMitra extends RealmSearchAdapter<Mitra, RSVAdapterMitra.MyViewHolder> {

    private Context ctx;
    private ListenerMitraList listener;

    public RSVAdapterMitra(@NonNull Context context, @NonNull Realm realm, @NonNull String filterKey, ListenerMitraList listener) {
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
        final Mitra obj = realmResults.get(position);

//        holder.tvLabel.setTextColor(ContextCompat.getColor(context, obj.isEnabled() ? R.color.cardTextColor : R.color.cardTextColorDisabled));
        holder.tvLabelName.setText(obj.getName());
        holder.tvLabelAddress.setText(obj.getAddressByGoogle());
        holder.tvLabelWorkHours.setText(obj.getWorkingHourStart() + " - " + obj.getWorkingHourEnd());
//        holder.tvOrderTodayCount.setText(ctx.getString(R.string.label_order_today_count, obj.getOrderTodayCount()));
    }

    public class MyViewHolder extends RealmSearchViewHolder {
        public TextView tvLabelName, tvLabelAddress, tvLabelWorkHours;
        public View view;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            tvLabelName = view.findViewById(R.id.tvLabelName);
            tvLabelAddress = view.findViewById(R.id.tvLabelAddress);
            tvLabelWorkHours = view.findViewById(R.id.tvLabelWorkHours);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // Check if an item was deleted, but the user clicked it before the UI removed it
                    if (position == RecyclerView.NO_POSITION) return;

                    Mitra obj = realmResults.get(position);

                    if (listener != null) {
                        listener.onItemSelected(obj);
                    }

                }
            });
        }
    }

}

