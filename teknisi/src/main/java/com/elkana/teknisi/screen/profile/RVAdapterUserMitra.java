package com.elkana.teknisi.screen.profile;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.R;
import com.elkana.teknisi.pojo.UserMitra;

import java.util.List;

/**
 * Created by Eric on 14-Dec-17.
 */
public class RVAdapterUserMitra extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_CELL = 1;
    private final int VIEW_TYPE_FOOTER = 2;

    private Context ctx;
    private List<UserMitra> mList;
    private ListenerMitraList mListener;

    public RVAdapterUserMitra(Context context, List<UserMitra> list, ListenerMitraList listener) {
        this.ctx = context;
        this.mList = list;
        this.mListener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CELL) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_usermitra, parent, false);
            return new MyViewHolder(itemView);
        } else {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_usermitra_add, parent, false);
            return new MyAddAddressHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (position == mList.size()) {
            // enable kalau dilimit cukup 1 saja
//            ((MyAddAddressHolder) holder).btnAddAddress.setVisibility(mList.size() > 0 ? View.INVISIBLE : View.VISIBLE);
        } else {

            final UserMitra obj = mList.get(position);
            ((MyViewHolder) holder).tvLabelName.setHint(obj.getName());
            ((MyViewHolder) holder).tvLabelAddress.setText(obj.getAddress());

            ((MyViewHolder) holder).btnStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Util.showDialog(ctx, "Apply Status", "Menunggu Konfirmasi dari Mitra");
                }
            });

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

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvLabelName, tvLabelAddress;
        public ImageButton btnStatus;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvLabelName = itemView.findViewById(R.id.tvLabelName);
            tvLabelAddress = itemView.findViewById(R.id.tvLabelAddress);
//            tvLabelAddress.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(ctx, R.drawable.ic_store_black_24dp, android.R.color.white), null, null, null);

            btnStatus = itemView.findViewById(R.id.btnStatus);
            btnStatus.setImageResource(R.drawable.ic_query_builder_black_24dp);
            btnStatus.setColorFilter(Color.parseColor("#FFFFFF"));

        }
    }

    class MyAddAddressHolder extends RecyclerView.ViewHolder {
        public Button btnAddAddress;


        public MyAddAddressHolder(View itemView) {
            super(itemView);

            btnAddAddress = itemView.findViewById(R.id.btnAddAddress);
            btnAddAddress.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(ctx, R.drawable.ic_add_black_24dp, android.R.color.white), null, null, null);


            //            btnAddAddress.setImageResource(R.drawable.ic_add_black_24dp);
//            btnAddAddress.setColorFilter(Color.parseColor("#FFFFFF"));
            btnAddAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onAddMitra();
                    }
//                    startActivityForResult(new Intent(ActivityRegister.this, ActivityMaps.class), 66);
//                        Toast.makeText(ctx, "You click add", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

}

