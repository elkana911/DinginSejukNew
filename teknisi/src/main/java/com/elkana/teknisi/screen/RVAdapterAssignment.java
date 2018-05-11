package com.elkana.teknisi.screen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.elkana.dslibrary.alarm.AlarmReceiver;
import com.elkana.dslibrary.firebase.FBFunction_BasicCallableRecord;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.pojo.mitra.Assignment;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.DateUtil;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.R;
import com.elkana.teknisi.pojo.MobileSetup;
import com.elkana.teknisi.pojo.ReminderAssignment;
import com.elkana.teknisi.util.TeknisiUtil;
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
import java.util.Random;

import io.realm.Realm;

/**
 * Created by Eric on 13-Nov-17.
 */
public class RVAdapterAssignment extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = RVAdapterAssignment.class.getSimpleName();

    private Context mContext;
    private List<Assignment> mList = new ArrayList<>();
//    private List<ReminderAssignment> listReminder = new ArrayList<>();

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
                Realm r = Realm.getDefaultInstance();
                try{

                    MobileSetup ms = r.where(MobileSetup.class).findFirst();
                    // berisi list assignment
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        // ambil informasi assign
                        Assignment assignment = postSnapshot.child("assign").getValue(Assignment.class);

                        // ambil informasi items

                        Log.e(TAG, assignment.toString());
                        mList.add(assignment);

                        long minMillisOtw = ms.getMin_minutes_otw() * DateUtil.TIME_ONE_MINUTE_MILLIS;
                        long timeBeforeOtw = DateUtil.compileDateAndTime(assignment.getDateOfService(), assignment.getTimeOfService()) - minMillisOtw;
                        // misalkan minimal 160 menit dr jam layanan, maka remindernya 160 menit - 1 jam
                        long remindMeMillis = timeBeforeOtw - DateUtil.TIME_ONE_HOUR_MILLIS;

                        // TODO custom with arrays.xml reminder_technician
                        r.beginTransaction();

                        ReminderAssignment re = new ReminderAssignment();
                        re.setUid(assignment.getUid());
                        re.setUniqueCode(new Random().nextInt(1000000));
                        re.setRemindType(0);    // default 1 jam (sebelum jam service dikurangi min_minutes_otw)

                        switch (re.getRemindType()) {
                            case 0:
                                break;
                            case 1:
                                remindMeMillis = timeBeforeOtw - (2 * DateUtil.TIME_ONE_HOUR_MILLIS);   // 2jam
                                break;
                            case 2:
                                remindMeMillis = timeBeforeOtw - (10 * DateUtil.TIME_ONE_MINUTE_MILLIS);   // 10menit
                                break;
                            case 3:
                                remindMeMillis = timeBeforeOtw;
                                break;
                        }

                        re.setReminderTime(remindMeMillis);
                        r.copyToRealmOrUpdate(re);
                        r.commitTransaction();
                    }


                }finally {
                    r.close();
                }

                if (mList.size() > 0) {

                    Collections.sort(mList, new Comparator<Assignment>() {
                        @Override
                        public int compare(Assignment s1, Assignment s2) {
                            //1. compare by status first. kalo sukses brarti 1. masalahnya versi 0.5.0 blm ada statusId jd masih ngandalin statusdetailid
                            int compareStatus = 0;
                            EOrderDetailStatus s1DetailStatus = EOrderDetailStatus.convertValue(s1.getStatusDetailId());
                            EOrderDetailStatus s2DetailStatus = EOrderDetailStatus.convertValue(s2.getStatusDetailId());

                            if (s1DetailStatus != s2DetailStatus) {

                                boolean is_s1StatusIsDone = s1DetailStatus == EOrderDetailStatus.PAID
                                        || s1DetailStatus == EOrderDetailStatus.CANCELLED_BY_CUSTOMER
                                        || s1DetailStatus == EOrderDetailStatus.CANCELLED_BY_SERVER
                                        || s1DetailStatus == EOrderDetailStatus.CANCELLED_BY_TIMEOUT;

                                boolean is_s2StatusIsDone = s2DetailStatus == EOrderDetailStatus.PAID
                                        || s2DetailStatus == EOrderDetailStatus.CANCELLED_BY_CUSTOMER
                                        || s2DetailStatus == EOrderDetailStatus.CANCELLED_BY_SERVER
                                        || s2DetailStatus == EOrderDetailStatus.CANCELLED_BY_TIMEOUT;

                                if (is_s1StatusIsDone && is_s2StatusIsDone) {
                                } else {
                                    if (is_s1StatusIsDone)
                                        compareStatus = -1;
                                    else
                                        compareStatus = 1;
                                }

                            }

                            if (compareStatus != 0)
                                return compareStatus;

                            //2. compare by timestamp
                            if (s1.getUpdatedTimestamp() < s2.getUpdatedTimestamp())
                                return 1;   // DESCENDING
                            else if (s1.getUpdatedTimestamp() > s2.getUpdatedTimestamp())
                                return -1;
                            else
                                return 0;

                        }
                    });
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
        public TextView tvAddress, tvCustomerName, tvInvoiceNo, tvMitra, tvDateOfService, tvReminder;
        public View view;
        public ImageView ivMap, ivIconStatus, ivReminder;
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
            ivReminder = itemView.findViewById(R.id.ivReminder);
            tvReminder = itemView.findViewById(R.id.tvReminder);
            btnPickTime = itemView.findViewById(R.id.btnPickTime);
        }

        public void setData(final Assignment data) {

            boolean showMap = false;
            int resIcon = -1;
            ivReminder.setVisibility(View.GONE);
            tvReminder.setVisibility(View.GONE);

            MobileSetup mobileSetup = TeknisiUtil.getMobileSetup();

            switch (EOrderDetailStatus.convertValue(data.getStatusDetailId())) {
                case ASSIGNED:
                    showMap = true;

                    // reminder sepenuhnya di atur di client, ga perlu update ke cloud krn masalah quota
                    if (!data.getTimeOfService().equals("99:99") && mobileSetup.isReminderToOtw()) {
                        ivReminder.setVisibility(View.VISIBLE);
                        tvReminder.setVisibility(View.VISIBLE);

                        Realm _r = Realm.getDefaultInstance();
                        try{
                            ReminderAssignment ra = _r.where(ReminderAssignment.class).equalTo("uid", data.getUid())
                                    .findFirst();

                            if (new Date().getTime() > ra.getReminderTime()) {

                            } else if (ra != null) {
                                Intent i = new Intent(mContext, AlarmReceiver.class);
                                i.putExtra("title", "Time To Go Reminder");
                                i.putExtra("message", data.getCustomerAddress());
                                i.putExtra("time", ra.getReminderTime());

                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(ra.getReminderTime());

                                Toast.makeText(mContext, "Reminder for " + data.getInvoiceNo() + " set at " + DateUtil.displayTimeInJakarta(ra.getReminderTime(), "dd-MMM-yyyy HH:mm"), Toast.LENGTH_SHORT).show();

                                DateUtil.setAlarm(mContext, i, cal, ra.getUniqueCode());
                            }

                        }finally {
                            _r.close();
                        }

                    }

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

            ivReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Under construction", Toast.LENGTH_SHORT).show();

                    final String[] time_services = mContext.getResources().getStringArray(R.array.reminder_technician);

                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
                    builder.setTitle("Pilih Waktu Pengingat");

                    builder.setItems(time_services, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String pick = time_services[which];

                            final String[] _short_time_services = mContext.getResources().getStringArray(R.array.short_reminder_technician);

                            String label = _short_time_services[which];
                            // calculate timeservice then register reminder
                            tvReminder.setText(label);

                            // cocokin dulu sama data di local
                            Realm _r = Realm.getDefaultInstance();
                            try{

                                MobileSetup ms = _r.where(MobileSetup.class).findFirst();

                                ReminderAssignment _ra = _r.where(ReminderAssignment.class).equalTo("uid", data.getUid()).findFirst();

                                if (_ra != null) {
                                    if (_ra.getRemindType() == which)
                                        return;

                                    long minMillisOtw = ms.getMin_minutes_otw() * DateUtil.TIME_ONE_MINUTE_MILLIS;
                                    long timeBeforeOtw = DateUtil.compileDateAndTime(data.getDateOfService(), data.getTimeOfService()) - minMillisOtw;
                                    // misalkan minimal 160 menit dr jam layanan, maka remindernya 160 menit - 1 jam
                                    long remindMeMillis = timeBeforeOtw - DateUtil.TIME_ONE_HOUR_MILLIS;

                                    switch (which) {
                                        case 0:
                                            break;
                                        case 1:
                                            remindMeMillis = timeBeforeOtw - (2 * DateUtil.TIME_ONE_HOUR_MILLIS);   // 2jam
                                            break;
                                        case 2:
                                            remindMeMillis = timeBeforeOtw - (10 * DateUtil.TIME_ONE_MINUTE_MILLIS);   // 10menit
                                            break;
                                        case 3:
                                            remindMeMillis = timeBeforeOtw;
                                            break;
                                    }


                                    _r.beginTransaction();

                                    _ra.setUniqueCode(which);
                                    _ra.setReminderTime(remindMeMillis);
                                    _r.copyToRealmOrUpdate(_ra);

                                    _r.commitTransaction();

                                    // replace notification id
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTimeInMillis(_ra.getReminderTime());

                                    Intent i = new Intent(mContext, AlarmReceiver.class);
                                    i.putExtra("title", "Time To Go Reminder");
                                    i.putExtra("message", data.getCustomerAddress());
                                    i.putExtra("time", _ra.getReminderTime());

                                    DateUtil.setAlarm(mContext, i, cal, _ra.getUniqueCode());

                                }

                            }finally {
                                _r.close();
                            }

//                            buildReminder();

                        }
                    });

                    android.support.v7.app.AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EOrderDetailStatus detailStatus = EOrderDetailStatus.convertValue(data.getStatusDetailId());

                    if (detailStatus == EOrderDetailStatus.ASSIGNED) {
                        if (data.getTimeOfService().equals("99:99")) {
                            Toast.makeText(mContext, "Mohon " + btnPickTime.getText().toString(), Toast.LENGTH_SHORT).show();
                        } else if (mListener != null)
                            mListener.onItemSelected(data);
                    } else if (mListener != null)
                        mListener.onItemSelected(data);
                }
            });

        }
    }
}
