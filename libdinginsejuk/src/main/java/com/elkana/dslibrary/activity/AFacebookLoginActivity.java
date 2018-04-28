package com.elkana.dslibrary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;

public abstract class AFacebookLoginActivity extends FirebaseActivity {

    private static final String TAG = "AFacebookLoginActivity";

    protected CallbackManager facebookCallbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        facebookCallbackManager = CallbackManager.Factory.create();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void logout() {
        super.logout();

        LoginManager.getInstance().logOut();
    }
}
