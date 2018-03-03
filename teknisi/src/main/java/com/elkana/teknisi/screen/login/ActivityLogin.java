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
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.screen.MainActivity;
import com.elkana.teknisi.R;
import com.elkana.teknisi.pojo.MobileSetup;
import com.elkana.teknisi.screen.register.ActivityRegister;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class ActivityLogin extends FirebaseActivity {
    private static final String TAG = ActivityLogin.class.getSimpleName();

//    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 411;
//    public static final int MY_PERMISSIONS_REQUEST_LOCATION_PHONE = 412;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION_PHONE_CAMERA = 413;

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
                        , android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.CALL_PHONE
                        , Manifest.permission.CAMERA},
                MY_PERMISSIONS_REQUEST_LOCATION_PHONE_CAMERA);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        MobileSetup mobileSetup = this.realm.where(MobileSetup.class).findFirst();

        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(userFullName);
//            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(TAG);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(mobileSetup.getTheme_color_default())));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor(mobileSetup.getTheme_color_default()));
            }
        }
        // if you want to center title https://stackoverflow.com/questions/18418635/how-to-align-title-at-center-of-actionbar-in-default-themetheme-holo-light
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.actionbar);

        // Set up the login form.
        if (Util.DEVELOPER_MODE && Util.TESTING_MODE) {
            mEmailView.setText("elkana911@yahoo.com");
            mPassword.setText("elkana911");
        }

//        mEmailView.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_mail_outline_black_24dp, android.R.color.white), null, null, null);
//        mPassword.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_lock_outline_black_24dp, android.R.color.white), null, null, null);

//        mPasswordView = (EditText) findViewById(R.id.password);
//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });

//        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
//        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                attemptLogin();
//            }
//        });

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
            case MY_PERMISSIONS_REQUEST_LOCATION_PHONE_CAMERA: {
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

    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
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
