package com.elkana.ds.mitraapp.screen.register;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.elkana.ds.mitraapp.R;
import com.elkana.dslibrary.pojo.user.UserAddress;
import com.elkana.dslibrary.util.Util;

import java.util.List;


/**
 * Created by Eric on 27-Oct-17.
 */

//    https://stackoverflow.com/questions/29106484/how-to-add-a-button-at-the-end-of-recyclerview
public class RVAdapterUserAddress extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_CELL = 1;
    private final int VIEW_TYPE_FOOTER = 2;

    private Context ctx;
    private List<UserAddress> mList;
    private ListenerAddressList mListener;

    public RVAdapterUserAddress(Context context, List<UserAddress> list, ListenerAddressList listener) {
        this.ctx = context;
        this.mList = list;
        this.mListener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CELL) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_useraddress, parent, false);
            return new MyViewHolder(itemView);
        } else {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_useraddress_add, parent, false);
            return new MyAddAddressHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (position == mList.size()) {
            // cukup 1 alamat saja utk mitra
            ((MyAddAddressHolder) holder).btnAddAddress.setVisibility(mList.size() > 0 ? View.INVISIBLE : View.VISIBLE);
        } else {

            final UserAddress obj = mList.get(position);
//            ((MyViewHolder) holder).tvLabelAddress.setHint(obj.getLabel());
            ((MyViewHolder) holder).tvLabelAddress.setText(obj.getLabel());

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
        public EditText tvLabelAddress;
        public ImageButton btnDelete;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvLabelAddress = itemView.findViewById(R.id.tvLabelAddress);
//            tvLabelAddress.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(ctx, R.drawable.ic_home_black_24dp, android.R.color.white), null, null, null);

            btnDelete = itemView.findViewById(R.id.btnDeleteAddress);
            btnDelete.setImageResource(R.drawable.ic_indeterminate_check_box_black_24dp);
            btnDelete.setColorFilter(Color.parseColor("#FFFFFF"));

        }
    }

    class MyAddAddressHolder extends RecyclerView.ViewHolder {
        public Button btnAddAddress;


        public MyAddAddressHolder(View itemView) {
            super(itemView);

            btnAddAddress = itemView.findViewById(R.id.btnAddAddress);
//            btnAddAddress.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(ctx, R.drawable.ic_add_black_24dp, android.R.color.white), null, null, null);


            //            btnAddAddress.setImageResource(R.drawable.ic_add_black_24dp);
//            btnAddAddress.setColorFilter(Color.parseColor("#FFFFFF"));
            btnAddAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onAddAddress();
                    }
//                    startActivityForResult(new Intent(ActivityRegister.this, ActivityMaps.class), 66);
//                        Toast.makeText(ctx, "You click add", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

}
