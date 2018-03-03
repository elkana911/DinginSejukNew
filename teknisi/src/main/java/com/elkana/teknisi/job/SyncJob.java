package com.elkana.teknisi.job;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.elkana.dslibrary.map.Location;
import com.elkana.dslibrary.util.NetUtil;
import com.elkana.teknisi.pojo.MobileSetup;
import com.elkana.teknisi.pojo.Movement;
import com.elkana.teknisi.util.DataUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import io.realm.Realm;

/**
 * Created by Eric on 12-Sep-16.
 */
public class SyncJob extends BroadcastReceiver {

    public static void sync_Location(Context context, String technicianId, String orderId, Movement movement, boolean offline) {

        if (!NetUtil.isConnected(context)) {
            return;
        }
        // technicianId uat jaga2 nanti kalo butuh
        FirebaseDatabase.getInstance().getReference(DataUtil.REF_MOVEMENTS)
                .child(orderId)
                .child(String.valueOf(new Date().getTime()))
                .setValue(movement);

    }


    @Override
    public void onReceive(final Context context, Intent intent) {

        String orderId;
        Realm r = Realm.getDefaultInstance();
        try{
            MobileSetup setup = r.where(MobileSetup.class).findFirst();

            if (!setup.isTrackingGps()) {
                return;
            }

            orderId = setup.getTrackingOrderId();

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                return;
            }

            final double[] gps = Location.getGPS(context);

            String latitude = String.valueOf(gps[0]);
            String longitude = String.valueOf(gps[1]);

            if (latitude.equals("0.0") || longitude.equals("0.0"))
                return;

            sync_Location(context, currentUser.getUid(), orderId, new Movement(latitude, longitude), false);
        }finally{
            r.close();
        }

//        if (Utility.isWorkingHours(new Date(), 8, 17)) {
//            try {
//                NetUtil.refreshRVBFromServer(context);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

    }
}
