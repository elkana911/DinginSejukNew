package com.elkana.teknisi.screen;

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

import com.elkana.dslibrary.pojo.mitra.Assignment;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.R;
import com.elkana.teknisi.util.DataUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 13-Nov-17.
 */

public class RVAdapterAssignment extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = RVAdapterAssignment.class.getSimpleName();

    private Context mContext;
    private List<Assignment> mList = new ArrayList<>();

    private ListenerAssignmentList mListener;

    private ValueEventListener mAssignmentRefValueEventListener;
    private DatabaseReference assignmentRef;

    public RVAdapterAssignment(Context context, String technicianId, final ListenerAssignmentList listener) {
        this.mContext = context;
        this.mListener = listener;

        if (technicianId == null)
            return;

//        final AlertDialog dialog = new SpotsDialog(mContext, "Check Orders");
//        dialog.show();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                dialog.dismiss();

                mList.clear();
                // berisi list assignment
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    // ambil informasi assign
                    Assignment assignment = postSnapshot.child("assign").getValue(Assignment.class);

                    // ambil informasi items

                    Log.e(TAG, assignment.toString());
                    mList.add(assignment);
                }

                notifyDataSetChanged();

                if (listener != null)
                    listener.onDataChanged(mList);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                dialog.dismiss();

                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
            }
        };

        mAssignmentRefValueEventListener = valueEventListener;

        assignmentRef = FirebaseDatabase.getInstance().getReference(DataUtil.REF_ASSIGNMENTS_PENDING)
                .child(technicianId);
        assignmentRef.addValueEventListener(mAssignmentRefValueEventListener);
//        assignmentRef.orderByChild("createdDate").addValueEventListener(mAssignmentRefValueEventListener);

        if (listener != null) {
            listener.onDataChanged(mList);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_assignment_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Assignment obj = mList.get(position);

        int resIcon = -1;
        switch (EOrderDetailStatus.convertValue(obj.getStatusDetailId())) {
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

        ((MyViewHolder) holder).tvAddress.setText(obj.getCustomerAddress());
        ((MyViewHolder) holder).tvCustomerName.setText(obj.getCustomerName());
        ((MyViewHolder) holder).tvMitra.setText(mContext.getString(R.string.label_mitra, obj.getMitraName()));

        String tos = Util.convertDateToString(Util.convertStringToDate(obj.getDateOfService(), "yyyyMMdd"), "dd MMM yyyy")
                + " " + obj.getTimeOfService();
        ((MyViewHolder) holder).tvDateOfService.setText(mContext.getString(R.string.label_serviceDate) + " " + tos);

        final ImageView thumbnailMap = ((MyViewHolder) holder).ivMap;
        thumbnailMap.setVisibility(View.VISIBLE);
        if (obj.getLatitude() == null || obj.getLongitude() == null) {
            thumbnailMap.setVisibility(View.GONE);
        } else {

            StringBuilder map_static_url = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap?size=400x200&zoom=15");
            map_static_url.append("&markers=color:red|label:A|")
                    .append(obj.getLatitude())
                    .append(",")
                    .append(obj.getLongitude());

//        https://maps.googleapis.com/maps/api/staticmap?size=600x300&markers=color:red|label:A|-6.24415,106.6357&zoom=15
//            Picasso.with(mContext).load(map_static_url.toString()).networkPolicy(NetworkPolicy.OFFLINE).into(((MyViewHolder) holder).ivMap);
            Picasso.with(mContext).load(map_static_url.toString())
                    .fit()
//                    .centerInside()
                    .into(thumbnailMap, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            thumbnailMap.setVisibility(View.GONE);
                        }
                    });

        }
        ((MyViewHolder) holder).view.setOnClickListener(new View.OnClickListener() {
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

    public void cleanUpListener() {
        if (assignmentRef != null && mAssignmentRefValueEventListener != null) {
            assignmentRef.removeEventListener(mAssignmentRefValueEventListener);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvAddress, tvCustomerName, tvMitra, tvDateOfService;
        public View view;
        public ImageView ivMap, ivIconStatus;

        public MyViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvMitra = itemView.findViewById(R.id.tvMitra);
            tvDateOfService = itemView.findViewById(R.id.tvDateOfService);
            ivMap = itemView.findViewById(R.id.ivMap);
            ivIconStatus = itemView.findViewById(R.id.ivIconStatus);
        }
    }
}