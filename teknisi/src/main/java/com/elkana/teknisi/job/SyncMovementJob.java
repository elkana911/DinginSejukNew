package com.elkana.teknisi.job;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.elkana.dslibrary.map.Location;
import com.elkana.dslibrary.pojo.Movement;
import com.elkana.dslibrary.util.NetUtil;
import com.elkana.teknisi.pojo.MobileSetup;
import com.elkana.teknisi.util.TeknisiUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Eric on 12-Sep-16.
 */
public class SyncMovementJob extends BroadcastReceiver {

    public static void sync_Location(Context context, String technicianId, String orderId, Movement movement, boolean offline) {

        if (!NetUtil.isConnected(context)) {
            return;
        }
        // technicianId uat jaga2 nanti kalo butuh
        FirebaseDatabase.getInstance().getReference(TeknisiUtil.REF_MOVEMENTS)
                .child(orderId)
                .child(String.valueOf(new Date().getTime()))
                .setValue(movement);

    }

    public static void markLocation(Context ctx) {
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

            final double[] gps = Location.getGPS(ctx);

            String latitude = String.valueOf(gps[0]);
            String longitude = String.valueOf(gps[1]);

            if (latitude.equals("0.0") || longitude.equals("0.0"))
                return;

            // kalo sama dgn posisi terakhir abaikan saja
            RealmResults<Movement> allSorted = r.where(Movement.class).findAllSorted("id", Sort.DESCENDING);

            Movement lastMovement = null;
            if (allSorted.size() > 0) {
                lastMovement = allSorted.first();
            }

            if (lastMovement != null && lastMovement.getLatitude().equals(latitude)
                    && lastMovement.getLongitude().equals(longitude)) {
                return;
            }

            lastMovement = new Movement(latitude, longitude);
            r.beginTransaction();
            r.copyToRealm(lastMovement);
            r.commitTransaction();

            sync_Location(ctx, currentUser.getUid(), orderId, lastMovement, false);
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

    @Override
    public void onReceive(final Context context, Intent intent) {
        markLocation(context);
    }
}
