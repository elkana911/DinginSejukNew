package com.elkana.dslibrary.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.elkana.dslibrary.R;

/**
 * Created by Eric on 01-Nov-17.
 */

public class NetUtil {
    public static boolean isConnected(Context ctx) {
        ConnectivityManager connec = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        return (connec.getActiveNetworkInfo() != null)
                && (connec.getActiveNetworkInfo().isAvailable())
                && (connec.getActiveNetworkInfo().isConnected());
    }

    public static boolean shownMessageWhenOffline(Context ctx) {
        if (isConnected(ctx))
            return false;

        Toast.makeText(ctx, ctx.getString(R.string.error_online_required), Toast.LENGTH_LONG).show();

        return true;
    }
}
