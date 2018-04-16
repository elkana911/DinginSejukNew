package com.elkana.dslibrary.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

public class ColorUtil {

    public static void setTextColorAsRed(Context ctx, TextView tv){
        tv.setTextColor(ContextCompat.getColor(ctx, android.R.color.holo_red_light));
    }

}
