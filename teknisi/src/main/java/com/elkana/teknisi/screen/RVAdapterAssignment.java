package com.elkana.teknisi.screen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.elkana.dslibrary.firebase.FBFunction_BasicCallableRecord;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.pojo.mitra.Assignment;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.DateUtil;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        public TextView tvAddress, tvCustomerName, tvInvoiceNo, tvMitra, tvDateOfService;
        public View view;
        public ImageView ivMap, ivIconStatus;
        public Button btnPickTime;

        public MyViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvInvoiceNo = itemView.findViewById(R.id.tvInvoiceNo);
            tvMitra = itemView.findViewById(R.id.tvMitra);
            tvDateOfService = itemView.findViewById(R.id.tvDateOfService);
            ivMap = itemView.findViewById(R.id.ivMap);
            ivIconStatus = itemView.findViewById(R.id.ivIconStatus);
            btnPickTime = itemView.findViewById(R.id.btnPickTime);
        }

        public void setData(final Assignment data) {

            boolean showMap = false;
            int resIcon = -1;
            switch (EOrderDetailStatus.convertValue(data.getStatusDetailId())) {
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

            tvInvoiceNo.setText("Invoice : " + data.getInvoiceNo());
            tvAddress.setText(data.getCustomerAddress());
            tvCustomerName.setText(data.getCustomerName());
            tvMitra.setText(mContext.getString(R.string.label_mitra, data.getMitraName()));

//            String tos = Util.prettyTimestamp(mContext, DateUtil.compileDateAndTime(obj.getDateOfService(), obj.getTimeOfService()));

//            tvDateOfService.setText(mContext.getString(R.string.label_serviceDate) + " " + tos);


            if (data.getTimeOfService().equals("99:99")) {
                btnPickTime.setVisibility(View.VISIBLE);
                tvDateOfService.setText(mContext.getString(R.string.prompt_schedule) + ": " + Util.prettyDate(mContext, Util.convertStringToDate(data.getDateOfService(), "yyyyMMdd"), true));
            } else {
                btnPickTime.setVisibility(View.GONE);
                tvDateOfService.setText(mContext.getString(R.string.prompt_schedule) + ": " + Util.prettyTimestamp(mContext, DateUtil.compileDateAndTime(data.getDateOfService(), data.getTimeOfService())));
            }

            btnPickTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // filter waktu buka berdasarkan hari service
                    int openTime = data.getMitraOpenTime();
                    int closeTime = data.getMitraCloseTime();
                    int offsetHour = 2;
                    String nextDayYYYYMMDD = data.getDateOfService();

                    Date now = new Date();
                    String today = Util.convertDateToString(now, "yyyyMMdd");
                    if (nextDayYYYYMMDD.equals(today)) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(now);

                        int currentHour = c.get(Calendar.HOUR_OF_DAY);
                        if (currentHour > openTime)
                            openTime = currentHour + offsetHour;
                    }

                    // show working hours of selected mitra
                    final String[] time_services = DateUtil.generateWorkingHours(openTime, closeTime, 15);

                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
                    builder.setTitle("Pilih Jam Pengerjaan");

                    builder.setItems(time_services, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final String pick = time_services[which];
                            data.setTimeOfService(pick);

                            Util.showDialogConfirmation(mContext, "Konfirmasi Jam Pengerjaan", "Perhatian, Jam tidak dapat diubah lagi.\nAnda yakin di jam " + pick + "?", new ListenerPositiveConfirmation() {
                                @Override
                                public void onPositive() {

                                    final AlertDialog dialog = Util.showProgressDialog(mContext, "Submit Jam Pengerjaan");

                                    final Map<String, Object> keyVal = new HashMap<>();
                                    keyVal.put("actionName", FBUtil.FUNCTION_TECHNICIAN_ACTION_SUBMIT_SERVICE_TIME);
                                    keyVal.put("timeOfService", pick);
                                    keyVal.put("assignmentId", data.getUid());
                                    keyVal.put("orderId", data.getOrderId());
                                    keyVal.put("technicianId", data.getTechnicianId());
                                    keyVal.put("custId", data.getCustomerId());
                                    keyVal.put("mitraId", data.getMitraId());
                                    keyVal.put("requestBy", String.valueOf(Const.USER_AS_TECHNICIAN));

                                    long serviceTimestamp = DateUtil.compileDateAndTime(data.getDateOfService(), data.getTimeOfService());
                                    keyVal.put("serviceTimestamp", serviceTimestamp);
//                                    keyVal.put("timestamp", ServerValue.TIMESTAMP);

                                    FirebaseFunctions.getInstance().getHttpsCallable(FBUtil.FUNCTION_TECHNICIAN_ACTION)
                                            .call(keyVal)
                                            .continueWith(new FBFunction_BasicCallableRecord())
                                            .addOnCompleteListener(new OnCompleteListener<Map<String, Object>>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Map<String, Object>> task) {
                                                    dialog.dismiss();

                                                    if (!task.isSuccessful()) {
                                                        Log.e(TAG, task.getException().getMessage(), task.getException());
                                                        Toast.makeText(mContext, FBUtil.friendlyTaskNotSuccessfulMessage(task.getException()), Toast.LENGTH_LONG).show();
                                                        return;
                                                    }

                                                    for (int i = 0; i < mList.size(); i++) {
                                                        if (data.getOrderId().equals(mList.get(i).getOrderId())) {
                                                            mList.set(i, data);

                                                            // TODO coba cek apakah tampilan berubah ?
                                                            notifyDataSetChanged();
                                                            break;
                                                        }
                                                    }

                                                }
                                            });
//                                    btnPickTime.setVisibility(View.GONE);

                                }
                            });


                        }
                    });

                    android.support.v7.app.AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });

            final ImageView thumbnailMap = ivMap;
            thumbnailMap.setVisibility(View.VISIBLE);
            if (data.getLatitude() == null || data.getLongitude() == null) {
                thumbnailMap.setVisibility(View.GONE);
            } else {

                if (showMap) {
                    StringBuilder map_static_url = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap?size=400x200&zoom=15");
                    map_static_url.append("&markers=color:red|label:A|")
                            .append(data.getLatitude())
                            .append(",")
                            .append(data.getLongitude());

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

                    EOrderDetailStatus detailStatus = EOrderDetailStatus.convertValue(data.getStatusDetailId());

                    if (detailStatus == EOrderDetailStatus.ASSIGNED) {
                        if (data.getTimeOfService().equals("99:99")) {
                            Toast.makeText(mContext, "Mohon " + btnPickTime.getText().toString(), Toast.LENGTH_SHORT).show();
                        } else
                            if (mListener != null)
                                mListener.onItemSelected(data);
                    } else
                        if (mListener != null)
                            mListener.onItemSelected(data);
                }
            });

        }
    }
}
