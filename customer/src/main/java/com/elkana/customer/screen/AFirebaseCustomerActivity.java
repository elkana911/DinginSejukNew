package com.elkana.customer.screen;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.elkana.customer.pojo.MobileSetup;
import com.elkana.customer.util.CustomerUtil;
import com.elkana.dslibrary.activity.FirebaseActivity;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Eric on 15-Mar-18.
 */

public abstract class AFirebaseCustomerActivity extends FirebaseActivity{
    protected MobileSetup mobileSetup = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mobileSetup = CustomerUtil.getMobileSetup();

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
}
