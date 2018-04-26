package com.elkana.ds.mitraapp.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.elkana.ds.mitraapp.R;
import com.elkana.ds.mitraapp.screen.login.ActivityLogin;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Eric on 13-Dec-16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    public static  int NOTIFICATION_ID = 1;

    public void broadcastMessage(String collFrom, @Nullable RemoteMessage.Notification notification
                                     , Map<String, String> data
    ) {
        //Displaying data in log
        //It is optional
        Log.e(TAG, "From: " + collFrom); //148963492070

        try {
            Intent pushNotification = new Intent(Const.PUSH_NOTIFICATION);
            pushNotification.putExtra("from", collFrom);

            if (notification != null) {
                Log.e(TAG, "Notification Message Title: " + notification.getTitle());//New Order from ELKANA911
                Log.e(TAG, "Notification Message Body: " + notification.getBody());  //Jl. Beryl Barat 2, Pakulonan Bar., Klp. Dua, Tangerang, Banten 15810, Indonesia  Indonesia

                pushNotification.putExtra("title", notification.getTitle());
                pushNotification.putExtra("body", notification.getBody());
            }

            Log.e(TAG, "Data Size: " + data.size());
            if (data.size() > 0) {

//                if (NewsUtil.isTitleIsNews(notification.getTitle())) {
//                    String key_uid = data.get(NewsUtil.KEY_UID);
//                    String key_from = data.get(NewsUtil.KEY_FROM);
//                    String key_to = data.get(NewsUtil.KEY_TO);
//                    String key_msg = data.get(NewsUtil.KEY_ARTICLE);
//                    String key_msgType = data.get(NewsUtil.KEY_MSG_TYPE);
//                    String key_timestamp = data.get(NewsUtil.KEY_BOOKING_TIMESTAMP);
//
//                    pushNotification.putExtra(NewsUtil.KEY_UID, key_uid);
//                    pushNotification.putExtra(NewsUtil.KEY_FROM, key_from);
//                    pushNotification.putExtra(NewsUtil.KEY_TO, key_to);
//                    pushNotification.putExtra(NewsUtil.KEY_ARTICLE, key_msg);
//                    pushNotification.putExtra(NewsUtil.KEY_MSG_TYPE, key_msgType);
//                    pushNotification.putExtra(NewsUtil.KEY_BOOKING_TIMESTAMP, key_timestamp);
//
//                } else {
//                    String key_from = data.get(Const.KEY_FROM);
//                    String key_uid = data.get(Const.KEY_UID);
//                    String key_msg = data.get(Const.KEY_MESSAGE);
//                    String key_status = data.get(Const.KEY_STATUS);
//                String key_seqno = remoteMessage.getData().get(ConstChat.KEY_SEQNO);
                    String key_booking_timestamp = data.get(Const.KEY_BOOKING_TIMESTAMP);

//                    pushNotification.putExtra(Const.KEY_UID, key_uid);
//                    pushNotification.putExtra(Const.KEY_FROM, key_from);
                    // replace previous chat_msg with real data
//                    pushNotification.putExtra(Const.KEY_MESSAGE, key_msg);
//                    pushNotification.putExtra(Const.KEY_STATUS, key_status);
//                pushNotification.putExtra(ConstChat.KEY_SEQNO, key_seqno);
                    pushNotification.putExtra(Const.KEY_BOOKING_TIMESTAMP, key_booking_timestamp);

//                }

            }

            Intent intent = new Intent(this, ActivityLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(notification.getTitle())
                    .setContentText(notification.getBody())
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (NOTIFICATION_ID > 1073741824) {
                NOTIFICATION_ID = 0;
            }
            notificationManager.notify(NOTIFICATION_ID++ , mNotifyBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null)
            return;
        if (NotificationUtils.isAppIsInBackground(this))
            broadcastMessage(remoteMessage.getFrom(), remoteMessage.getNotification(), remoteMessage.getData());
    }
}
