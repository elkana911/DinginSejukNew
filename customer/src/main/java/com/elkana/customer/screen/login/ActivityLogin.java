package com.elkana.customer.screen.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elkana.customer.R;
import com.elkana.customer.screen.AFirebaseActivity;
import com.elkana.customer.screen.MainActivity;
import com.elkana.customer.screen.intro.ActivityIntro;
import com.elkana.customer.screen.register.ActivityRegister;
import com.elkana.dslibrary.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class ActivityLogin extends AFirebaseActivity {
    private static final String TAG = ActivityLogin.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_LOCATION_PHONE_CAMERA_STORAGE = 413;

    AutoCompleteTextView mEmailView;
    EditText mPassword;
    Button btnLogin, btnRegister, btnForgotPwd;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                        ,android.Manifest.permission.CALL_PHONE
                        , android.Manifest.permission.CAMERA
                        , android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                MY_PERMISSIONS_REQUEST_LOCATION_PHONE_CAMERA_STORAGE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION_PHONE_CAMERA_STORAGE:
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
                break;
        }
    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {
        finish();
        startActivity(new Intent(this, MainActivity.class));
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
