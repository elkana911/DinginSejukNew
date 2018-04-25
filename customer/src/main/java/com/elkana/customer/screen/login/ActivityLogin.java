package com.elkana.customer.screen.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elkana.customer.BuildConfig;
import com.elkana.customer.R;
import com.elkana.customer.pojo.MobileSetup;
import com.elkana.customer.screen.MainActivity;
import com.elkana.customer.screen.intro.ActivityIntro;
import com.elkana.customer.screen.register.ActivityRegister;
import com.elkana.customer.util.CustomerUtil;
import com.elkana.dslibrary.activity.FirebaseActivity;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.SharedPrefUtil;
import com.elkana.dslibrary.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import io.realm.Realm;

/*
 utk login sbg layar utama jgn extend AfirebaseCustomerActivity krn akan force close di mobilesetup
  */
public class ActivityLogin extends FirebaseActivity {
    private static final String TAG = ActivityLogin.class.getSimpleName();
//    private static final int MY_PERMISSIONS_REQUEST_LOCATION_PHONE_STORAGE = 413;
//    private static final int MY_PERMISSIONS_REQUEST_LOCATION_PHONE_CAMERA_STORAGE = 413;  camera dipisah aja krn buat photo profile yg bukan mandatory

    AutoCompleteTextView mEmailView;
    EditText mPassword;
    Button btnLogin, btnRegister, btnForgotPwd;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        ActivityCompat.requestPermissions(this, new String[]{
//                        android.Manifest.permission.ACCESS_FINE_LOCATION
//                        , android.Manifest.permission.CALL_PHONE
////                        , android.Manifest.permission.CAMERA
//                        , android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//                },
//                MY_PERMISSIONS_REQUEST_LOCATION_PHONE_STORAGE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // having mobilesetup is mandatory. no userid needed
        mDatabase.getReference(CustomerUtil.REF_MASTER_SETUP)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final MobileSetup mobileSetup = dataSnapshot.getValue(MobileSetup.class);

                        Realm r = Realm.getDefaultInstance();
                        try {
                            r.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(mobileSetup);
                                }
                            });

                        } finally {
                            r.close();
                        }

                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(mobileSetup.getTheme_color_default())));
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(Color.parseColor(mobileSetup.getTheme_color_default()));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                        finish();   // quit app
                    }
                });

        mEmailView = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);

        btnForgotPwd = findViewById(R.id.btnForgetPwd);
        btnForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityLogin.this, "Unhandled", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityLogin.this, ActivityRegister.class));
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        mEmailView.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_mail_outline_black_24dp, android.R.color.darker_gray), null, null, null);
        mPassword.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_lock_outline_black_24dp, android.R.color.darker_gray), null, null, null);

        boolean firstTime = false;
        if (firstTime)
            startActivity(new Intent(this, ActivityIntro.class));

        if (Util.DEVELOPER_MODE && Util.TESTING_MODE) {
            mEmailView.setText("elkana911@yahoo.com");
            mPassword.setText("elkana911");
        }
    }

    @Override
    protected void onLoggedOff() {
        btnLogin.setEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_LOCATION_PHONE_STORAGE:
//                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
//                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
////                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
//                        ) {
////                    signIn();
//                } else {
//                    Toast.makeText(this, "You need to allow requested access to continue.", Toast.LENGTH_LONG).show();
//                    finish();
//                }
//                break;
//        }
    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {
        btnLogin.setEnabled(false);
//        Toast.makeText(this, "You are logged on as " + user.getEmail(), Toast.LENGTH_LONG).show();
        // reset token device
        FBUtil.Customer_addToken(user.getUid(), new SharedPrefUtil(getApplicationContext()).getString(Const.ARG_FIREBASE_TOKEN));

        final AlertDialog alertDialog = Util.showProgressDialog(this, "Check version");

        // check valid version ?
        int versionCode = BuildConfig.VERSION_CODE;
        final String versionName = BuildConfig.VERSION_NAME;

        CustomerUtil.CheckVersions(versionName, new ListenerModifyData(){

            @Override
            public void onSuccess() {
                alertDialog.dismiss();
                startActivity(new Intent(ActivityLogin.this, MainActivity.class));
                finish();
            }

            @Override
            public void onError(Exception e) {
                alertDialog.dismiss();
                Toast.makeText(ActivityLogin.this, "Mohon update aplikasi terbaru.", Toast.LENGTH_SHORT).show();
                logout();
                finish();
            }
        });

    }

    /*
        private void promptAllPermission() {
            if (!mayRequestPermissions())
                return;

            Log.i(TAG, "All permission granted !");
        }
        private boolean mayRequestPermissions() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return true;
            }

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.CALL_PHONE
                            , Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_LOCATION_PHONE);
            return false;
        }
    */
    private void attemptLogin(){
        boolean cancel = false;
        View focusView = null;
        // Reset errors.
        mEmailView.setError(null);
        mPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPassword.getText().toString();

        if (!Util.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_email_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return;
        }

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                progressBar.setVisibility(View.VISIBLE);
//            }
//        });
//
        final AlertDialog alertDialog = Util.showProgressDialog(this, "Login as " + email);

        btnLogin.setEnabled(false);
        btnRegister.setEnabled(false);
        btnForgotPwd.setEnabled(false);
//        loginFacebook.setEnabled(false);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                alertDialog.dismiss();

                btnLogin.setEnabled(true);
                btnRegister.setEnabled(true);
                btnForgotPwd.setEnabled(true);
//                loginFacebook.setEnabled(true);

                if (!task.isSuccessful()) {
                    Log.e(TAG, task.getException().getMessage(), task.getException());

                    Util.showErrorDialog(ActivityLogin.this, null, task.getException().getMessage());
//                    final Snackbar snackbar = Snackbar.make(coordinatorLayout, , Snackbar.LENGTH_LONG);
//                    snackbar.setAction(getString(R.string.action_register), new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            onClickPleaseRegister();
//                            snackbar.dismiss();
//                        }
//                    });
//                    snackbar.show();
                } else {

                }
            }
        });


    }
}
