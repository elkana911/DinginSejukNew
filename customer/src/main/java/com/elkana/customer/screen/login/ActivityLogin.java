package com.elkana.customer.screen.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.elkana.dslibrary.activity.AFacebookLoginActivity;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerDataExists;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.SharedPrefUtil;
import com.elkana.dslibrary.util.Util;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;

/*
 utk login sbg layar utama jgn extend AfirebaseCustomerActivity krn akan force close di mobilesetup
  */
public class ActivityLogin extends AFacebookLoginActivity {
    private static final String TAG = ActivityLogin.class.getSimpleName();
//    private static final int MY_PERMISSIONS_REQUEST_LOCATION_PHONE_STORAGE = 413;
//    private static final int MY_PERMISSIONS_REQUEST_LOCATION_PHONE_CAMERA_STORAGE = 413;  camera dipisah aja krn buat photo profile yg bukan mandatory
    private final int REQUESTCODE_FACEBOOK = 500;
    private int penaltyCounter = -3;
    private static final int PENALTY_WAIT_MILLISECONDS = 60000;

    AutoCompleteTextView mEmailView;
    EditText mPassword;
    Button btnLogin, btnRegister, btnForgotPwd;
    LoginButton loginFacebook;
    View llSocialMedia;

    int failLoginCounter = 0;

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


    private void allButtons(boolean enabled) {
        btnLogin.setEnabled(enabled);
        loginFacebook.setEnabled(enabled);
        btnRegister.setEnabled(enabled);
        btnForgotPwd.setEnabled(enabled);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // to make sure facebook session also cleared when activitylogin called from mainactivity.logout
//        logout();

        mEmailView = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);

        llSocialMedia = findViewById(R.id.llSocialMedia);

        loginFacebook = findViewById(R.id.login_facebook);
        loginFacebook.setReadPermissions("email", "public_profile");
        loginFacebook.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                allButtons(false);
                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(ActivityLogin.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {// fungsi ini tidak akan dipanggil jika facebook sudah terdaftar di firebaseauth
                                if (!task.isSuccessful()) {
                                    // kalo pesannya InternalFirebaseAuth.FIREBASE_AUTH_API is not available on this device
                                    // brarti devicenya tdk ada google play service
                                    //
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                        Toast.makeText(ActivityLogin.this, "Akun sudah terdaftar sebelumnya. Jangan login sebagai Facebook.", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(ActivityLogin.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    LoginManager.getInstance().logOut();
                                    allButtons(true);
                                    return;
                                }

                                // facebook akan otomatis create akun di firebaseauth, so NO NEED to handle account because the process will return to onAuthStateChanged due to async process
                                //   FBUtil.IsPathExists(FBUtil.REF_CUSTOMER + "/" + mAuth.getCurrentUser().getUid(), listener);
                            }
                        });

            }

            @Override
            public void onCancel() {
                // user click cancel. so ignore
                allButtons(true);
            }

            @Override
            public void onError(FacebookException error) {
                allButtons(true);
                Log.e(TAG, error.getMessage(), error);
                // maybe timeout
                Toast.makeText(ActivityLogin.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        btnForgotPwd = findViewById(R.id.btnForgetPwd);
        btnForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.isEmailValid(mEmailView.getText().toString())) {
                    mEmailView.setError(getString(R.string.error_email_required));
                    mEmailView.requestFocus();
                    return;
                }

//                Toast.makeText(ActivityLogin.this, "Unhandled", Toast.LENGTH_SHORT).show();
                mAuth.sendPasswordResetEmail(mEmailView.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            btnForgotPwd.setVisibility(View.GONE);
                            Toast.makeText(ActivityLogin.this, "Email sent", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

                        llSocialMedia.setVisibility(mobileSetup.isEnableSocialMediaLogin() ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                        finish();   // quit app
                    }
                });

    }

    @Override
    protected void onLoggedOff() {
        allButtons(true);
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
    protected void onLoggedOn(final FirebaseUser user) {

        final AlertDialog alertDialog = Util.showProgressDialog(this);

        boolean logViaSocMed = false;
        for (UserInfo ui : user.getProviderData()) {
            if (ui.getProviderId().equals("facebook.com")) {
                //For linked facebook account
                Log.d(TAG, "User is signed in with Facebook");
                logViaSocMed = true;
                break;

            } else if (ui.getProviderId().equals("google.com")) {
                //For linked Google account
                Log.d(TAG, "User is signed in with Google");
                logViaSocMed = true;
                break;
            }

        }
        // tackle successful facebook login
        final boolean finalLogViaSocMed = logViaSocMed;
        FBUtil.IsPathExists(FBUtil.REF_CUSTOMER + "/" + user.getUid(), new ListenerDataExists() {
            @Override
            public void onFound() {
                allButtons(false);
//        Toast.makeText(this, "You are logged on as " + user.getEmail(), Toast.LENGTH_LONG).show();
                // reset token device
                FBUtil.Customer_addToken(user.getUid(), new SharedPrefUtil(getApplicationContext()).getString(Const.ARG_FIREBASE_TOKEN));

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

            @Override
            public void onNotFound() {
                alertDialog.dismiss();
//                Toast.makeText(ActivityLogin.this, "Not found", Toast.LENGTH_SHORT).show();

                if (finalLogViaSocMed) {
                    Intent i = new Intent(ActivityLogin.this, ActivityRegister.class);
                    i.putExtra("email", user.getEmail());
                    i.putExtra("display_name", user.getDisplayName());
                    i.putExtra("phone", user.getPhoneNumber());
                    i.putExtra("facebook_mode", "1");
                    // jaga2 sebelum logout, ambil dulu userId
                    i.putExtra("userId_social_media", mAuth.getCurrentUser().getUid());
                    startActivityForResult(i, REQUESTCODE_FACEBOOK);

//                    logout(); spy tidak entri facebook 2x

                }else {
                    logout();
                    Toast.makeText(ActivityLogin.this, "Not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                alertDialog.dismiss();
                Toast.makeText(ActivityLogin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                logout();
            }
        });

    }

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

        allButtons(false);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                alertDialog.dismiss();

                if (!task.isSuccessful()) {
                    allButtons(true);

                    Log.e(TAG, task.getException().getMessage(), task.getException());

                    Util.showErrorDialog(ActivityLogin.this, null, task.getException().getMessage());

                    failLoginCounter += 1;

                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        penaltyCounter += 1;
                    }

                    if (penaltyCounter >= 0) {
                        allButtons(false);

                        startTimer();
                    }

                    MobileSetup mobileSetup = CustomerUtil.getMobileSetup();
                    if (mobileSetup.isEnableForgotPwd()) {
                        if (Util.TESTING_MODE)
                            btnForgotPwd.setVisibility(View.VISIBLE);
                    }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case REQUESTCODE_FACEBOOK:
                if (resultCode != RESULT_OK) {
                    logout();
                    return;
                }
//                loginFacebook.performClick();
                break;
        }
    }

    private void startTimer() {
        new Handler().post(new Runnable() {
            public void run() {
                // count down timer start, ditambah jumlah penalty
                new CountDownTimer(PENALTY_WAIT_MILLISECONDS + (penaltyCounter *1000), 1000) {
                    public void onTick(long millisUntilFinished) {
                        long t = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                        btnLogin.setText(getString(R.string.action_penalty, String.valueOf(t)));
                    }

                    public void onFinish() {
                        btnLogin.setText(getString(R.string.action_sign_in_short));

                        allButtons(true);
                    }
                }.start();
            }
        });
    }

}
