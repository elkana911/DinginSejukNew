package com.elkana.customer.screen;

import com.elkana.dslibrary.activity.FirebaseActivity;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Eric on 15-Mar-18.
 */

public abstract class AFirebaseActivity extends FirebaseActivity{

    @Override
    protected void onLoggedOff() {

    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {

    }
}
