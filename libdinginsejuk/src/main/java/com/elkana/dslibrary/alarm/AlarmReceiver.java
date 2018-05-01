package com.elkana.dslibrary.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.elkana.dslibrary.util.NotificationUtils;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtils.showNotification(context, intent.getStringExtra("title"), intent.getStringExtra("message"), intent.getLongExtra("time", 0), intent);
    }
}