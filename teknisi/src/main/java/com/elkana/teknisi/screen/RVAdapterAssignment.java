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

import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.pojo.mitra.Assignment;
import com.elkana.dslibrary.util.DateUtil;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        mContext = context;
        mListener = listener;

        if (technicianId == null)
            return;

        mAssignmentRefValueEventListener = new ValueEventListener() {
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

                if (mList.size() > 0)
                    Collections.sort(mList, new Comparator<Assignment>() {
                        @Override
                        public int compare(Assignment s1, Assignment s2) {
                            if (s1.getUpdatedTimestamp() < s2.getUpdatedTimestamp())
                                return 1;   // DESCENDING
                            else if (s1.getUpdatedTimestamp() > s2.getUpdatedTimestamp())
                                return -1;
                            else
                                return 0;
                        }
                    });

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

        assignmentRef = FirebaseDatabase.getInstance().getReference(FBUtil.REF_ASSIGNMENTS_PENDING)
                .child(technicianId);

        assignmentRef.addValueEventListener(mAssignmentRefValueEventListener);

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

        ((MyViewHolder) holder).setData(obj);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void cleanUpListener() {
        assignmentRef.removeEventListener(mAssignmentRefValueEventListener);
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

        public void setData(final Assignment obj) {

            boolean showMap = false;
            int resIcon = -1;
            switch (EOrderDetailStatus.convertValue(obj.getStatusDetailId())) {
                case ASSIGNED:
                    showMap = true;
                    // use default icon
                    break;
                case OTW:
                    showMap = true;
                    resIcon = R.drawable.ic_directions_bike_black_24dp;
                    break;
                case WORKING:
                    showMap = true;
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
                ivIconStatus.setImageDrawable(drawable);
            }

            tvAddress.setText(obj.getCustomerAddress());
            tvCustomerName.setText(obj.getCustomerName());
            tvMitra.setText(mContext.getString(R.string.label_mitra, obj.getMitraName()));

            String tos = Util.prettyTimestamp(mContext, DateUtil.compileDateAndTime(obj.getDateOfService(), obj.getTimeOfService()));
//            String tos = Util.convertDateToString(Util.convertStringToDate(obj.getDateOfService(), "yyyyMMdd"), "dd MMM yyyy") + " " + obj.getTimeOfService();

            tvDateOfService.setText(mContext.getString(R.string.label_serviceDate) + " " + tos);

            final ImageView thumbnailMap = ivMap;
            thumbnailMap.setVisibility(View.VISIBLE);
            if (obj.getLatitude() == null || obj.getLongitude() == null) {
                thumbnailMap.setVisibility(View.GONE);
            } else {

                if (showMap) {
                    StringBuilder map_static_url = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap?size=400x200&zoom=15");
                    map_static_url.append("&markers=color:red|label:A|")
                            .append(obj.getLatitude())
                            .append(",")
                            .append(obj.getLongitude());

//        https://maps.googleapis.com/maps/api/staticmap?size=600x300&markers=color:red|label:A|-6.24415,106.6357&zoom=15
//            Picasso.with(mContext).load(map_static_url.toString()).networkPolicy(NetworkPolicy.OFFLINE).into(((MyViewDefaultHolder) holder).ivMap);
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

                } else {
                    thumbnailMap.setVisibility(View.GONE);
                }
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onItemSelected(obj);
                }
            });

        }
    }
}
