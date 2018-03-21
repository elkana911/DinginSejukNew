package com.elkana.teknisi.fcm;

import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.SharedPrefUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Eric on 23-Oct-17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        new SharedPrefUtil(getApplicationContext()).saveString(Const.ARG_FIREBASE_TOKEN, refreshedToken);
    }
}
