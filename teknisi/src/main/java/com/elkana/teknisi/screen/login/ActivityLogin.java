package com.elkana.teknisi.screen.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elkana.dslibrary.activity.FirebaseActivity;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.SharedPrefUtil;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.BuildConfig;
import com.elkana.teknisi.screen.MainActivity;
import com.elkana.teknisi.R;
import com.elkana.teknisi.pojo.MobileSetup;
import com.elkana.teknisi.screen.register.ActivityRegister;
import com.elkana.teknisi.util.TeknisiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.realm.Realm;

/*
 utk login sbg layar utama jgn extend AfirebaseTeknisiActivity krn akan force close di mobilesetup
  */
public class ActivityLogin extends FirebaseActivity {
    private static final String TAG = ActivityLogin.class.getSimpleName();

//    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 411;
//    public static final int MY_PERMISSIONS_REQUEST_LOCATION_PHONE = 412;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION_PHONE_CAMERA_STORAGE = 413;

    View coordinatorLayout;
    EditText mPassword;
    AutoCompleteTextView mEmailView;
//    LoginButton loginFacebook;
    Button btnSignIn, btnPleaseRegister;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (Util.TESTING_MODE) {
            mEmailView.setText("liapharm@yahoo.com");
            mPassword.setText("liapharm");
        }

        ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                        , android.Manifest.permission.CALL_PHONE
                        , Manifest.permission.CAMERA
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                MY_PERMISSIONS_REQUEST_LOCATION_PHONE_CAMERA_STORAGE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // having mobilesetup is mandatory. no userid needed
        FirebaseDatabase.getInstance().getReference(TeknisiUtil.REF_MASTER_SETUP)
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

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        mEmailView = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        btnSignIn = findViewById(R.id.email_sign_in_button);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        btnPleaseRegister = findViewById(R.id.btnPleaseRegister);
        btnPleaseRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityLogin.this, ActivityRegister.class));
            }
        });

//        MobileSetup mobileSetup = this.realm.where(MobileSetup.class).findFirst();

        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(userFullName);
//            getSupportActionBar().setDisplayUseLogoEnabled(true);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle(TAG);
//            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(mobileSetup.getTheme_color_default())));
        }
        // if you want to center title https://stackoverflow.com/questions/18418635/how-to-align-title-at-center-of-actionbar-in-default-themetheme-holo-light
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.actionbar);

        // Set up the login form.
        if (Util.DEVELOPER_MODE && Util.TESTING_MODE) {
            mEmailView.setText("elkana911@yahoo.com");
            mPassword.setText("elkana911");
        }

        mEmailView.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_mail_outline_black_24dp, android.R.color.darker_gray), null, null, null);
        mPassword.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_lock_outline_black_24dp, android.R.color.darker_gray), null, null, null);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            /*
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        ) {
//                    signIn();
                } else {
                    Toast.makeText(this, "You need to allow requested access to continue.", Toast.LENGTH_LONG).show();
                    finish();
                }

                return;
            }
            case MY_PERMISSIONS_REQUEST_LOCATION_PHONE: {
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        ) {
//                    signIn();
                } else {
                    Toast.makeText(this, "You need to allow requested access to continue.", Toast.LENGTH_LONG).show();
                    finish();
                }

                return;
            }
            */
            case MY_PERMISSIONS_REQUEST_LOCATION_PHONE_CAMERA_STORAGE: {
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        ) {
//                    signIn();
                } else {
                    Toast.makeText(this, "You need to allow requested access to continue.", Toast.LENGTH_LONG).show();
                    finish();
                }

                return;
            }
        }
    }

    @Override
    protected void onLoggedOff() {
        btnSignIn.setEnabled(true);
    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {
        btnSignIn.setEnabled(false);

        // check valid version ?
        int versionCode = BuildConfig.VERSION_CODE;
        final String versionName = BuildConfig.VERSION_NAME;


        // reset token device
        FBUtil.Technician_addToken(user.getUid(), new SharedPrefUtil(getApplicationContext()).getString(Const.ARG_FIREBASE_TOKEN));

//                    List<String> tokens = new ArrayList<>();
//                    tokens.add(new SharedPrefUtil(getApplicationContext()).getString(Const.ARG_FIREBASE_TOKEN));

        final AlertDialog alertDialog = Util.showProgressDialog(this, "Check version");
        TeknisiUtil.CheckVersions(versionName, new ListenerModifyData(){

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

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

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

        btnSignIn.setEnabled(false);
        btnPleaseRegister.setEnabled(false);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                alertDialog.dismiss();

                btnSignIn.setEnabled(true);
                btnPleaseRegister.setEnabled(true);

                if (!task.isSuccessful()) {
                    Log.e(TAG, "Auth failed\n" + task.getException().getMessage(), task.getException());

                    final Snackbar snackbar = Snackbar.make(coordinatorLayout, task.getException().getMessage(), Snackbar.LENGTH_LONG);
                    snackbar.setAction(getString(R.string.action_register), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btnPleaseRegister.performClick();
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();

                } else {

                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

}
