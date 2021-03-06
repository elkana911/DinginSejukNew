package com.elkana.teknisi;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.elkana.dslibrary.activity.FirebaseActivity;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.pojo.MobileSetup;
import com.elkana.teknisi.util.TeknisiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eric on 13-Mar-18.
 */

public abstract class AFirebaseTeknisiActivity extends FirebaseActivity{

    protected MobileSetup mobileSetup = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mobileSetup = TeknisiUtil.getMobileSetup();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(mobileSetup.getTheme_color_default()));
        }

    }

    @Override
    protected void onLoggedOff() {

    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {

    }

    /*
called by teknisi only
 */
    protected void Assignment_setStatus(final String mitraId, final String technicianId, final String assignmentId, final String customerId, final String orderId, final EOrderDetailStatus newStatus, final ListenerModifyData listener) {

        Map<String, Object> keyValAssignment = new HashMap<>();
        keyValAssignment.put("statusDetailId", newStatus.name());
        keyValAssignment.put("updatedTimestamp", ServerValue.TIMESTAMP);

        // kalo status = OTW, add to mitra/.../technicianId/jobs, key format is yyyyMMdd
        // spy dapat diketahui 1 hari ada brp order
        if (newStatus == EOrderDetailStatus.ASSIGNED || newStatus == EOrderDetailStatus.OTW) {

            Date today = new Date();
            //dipindah ke sisi server spy tanggal ga dimanipulasi
//            FBUtil.Mitra_GetTechnicianRef(mitraId, technicianId)
//                    .child("jobs_assigned")
//                    .child(Util.convertDateToString(today, "yyyyMMddHHmm"))
//                    .child(String.valueOf(today.getTime()))
//                    .setValue(orderId);
        }

        FBUtil.Assignment_getPendingRef(technicianId, assignmentId)
                .child("assign")
                .updateChildren(keyValAssignment, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        // also update the main order
                        FBUtil.Order_SetStatus(mitraId, customerId, orderId, assignmentId, technicianId, newStatus, String.valueOf(Const.USER_AS_TECHNICIAN), new ListenerModifyData() {
                            @Override
                            public void onSuccess() {

                                if (newStatus == EOrderDetailStatus.PAID
                                        || newStatus == EOrderDetailStatus.CANCELLED_BY_CUSTOMER
                                        || newStatus == EOrderDetailStatus.CANCELLED_BY_SERVER
                                        || newStatus == EOrderDetailStatus.CANCELLED_BY_TIMEOUT
                                        /*|| newStatus == EOrderDetailStatus.RESCHEDULED*/) {
                                    // harusnya hanya delete assignment 1 atau 2 hr yg lalu
//                                    Assignment_delete(technicianId, assignmentId, null);
                                }

                                // just force as success
                                listener.onSuccess();
                            }

                            @Override
                            public void onError(Exception e) {
                                if (listener != null)
                                    listener.onError(e);
                            }
                        });

                    }
                });

    }

}
