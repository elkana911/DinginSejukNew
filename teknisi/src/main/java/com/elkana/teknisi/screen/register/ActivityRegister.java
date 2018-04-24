package com.elkana.teknisi.screen.register;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.map.Location;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.SharedPrefUtil;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.AFirebaseTeknisiActivity;
import com.elkana.teknisi.R;
import com.elkana.teknisi.pojo.MobileSetup;
import com.elkana.teknisi.util.TeknisiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

public class ActivityRegister extends AFirebaseTeknisiActivity {

    private static final String TAG = ActivityRegister.class.getSimpleName();

    EditText mNama, mEmailView, etPhone, mPassword;
    Button btnRegister, btnPleaseSignIn;
    ProgressBar progressBar;
    View coordinatorLayout;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Location.pleaseTurnOnGPS(this);

//        startActivityForResult(new Intent(this, ActivityMaps.class), REQUESTCODE_MAP); dont do this, firebaseactivity will finish automatically

        if (Util.TESTING_MODE) {
            mNama.setText("Lia");
            mEmailView.setText("liapharm@yahoo.com");
            mPassword.setText("liapharm");
            etPhone.setText("088888");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        MobileSetup mobileSetup = this.realm.where(MobileSetup.class).findFirst();

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        mEmailView = findViewById(R.id.email);
        mNama = findViewById(R.id.nama);
        mPassword = findViewById(R.id.password);
        etPhone = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
        btnPleaseSignIn = findViewById(R.id.btnPleaseSignIn);
        btnPleaseSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(userFullName);
//            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle(TAG);

            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(mobileSetup.getTheme_color_default())));

        }
        // if you want to center title https://stackoverflow.com/questions/18418635/how-to-align-title-at-center-of-actionbar-in-default-themetheme-holo-light
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.actionbar);

        // Set up the login form.
        mNama.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_person_outline_black_24dp, android.R.color.darker_gray), null, null, null);
        mPassword.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_lock_outline_black_24dp, android.R.color.darker_gray), null, null, null);
        mEmailView.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_mail_outline_black_24dp, android.R.color.darker_gray), null, null, null);
        etPhone.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_phone_black_24dp, android.R.color.darker_gray), null, null, null);

//        mPasswordView = (EditText) findViewById(R.id.password);
//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                    attemptRegister();
//                    return true;
//                }
//                return false;
//            }
//        });

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {

        boolean cancel = false;
        View focusView = null;
        // Reset errors.
        mNama.setError(null);
        mEmailView.setError(null);
        mPassword.setError(null);
        etPhone.setError(null);

        // Store values at the time of the login attempt.
        final String nama = mNama.getText().toString().trim();
        final String email = mEmailView.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        final String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(nama)) {
            mNama.setError(getString(R.string.error_field_required));
            focusView = mNama;
            cancel = true;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError(getString(R.string.error_field_required));
            focusView = etPhone;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        }

        if (!TextUtils.isEmpty(nama) && nama.length() < 2) {
            mNama.setError(getString(R.string.error_field_too_short));
            focusView = mNama;
            cancel = true;
        }
        if (!TextUtils.isEmpty(password) && password.length() < 6) {
            mPassword.setError(getString(R.string.error_field_too_short));
            focusView = mPassword;
            cancel = true;
        }

        if (!Util.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_email_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return;
        }

        final AlertDialog alertDialog = Util.showProgressDialog(this, "Registering " + email);
        btnRegister.setEnabled(false);
        btnPleaseSignIn.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        alertDialog.dismiss();

                        btnRegister.setEnabled(true);
                        btnPleaseSignIn.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_SHORT).show();

                            registerUser(nama, phone);
                            finish();
                        } else {
                            Snackbar.make(coordinatorLayout, getString(R.string.error_register) + "\n" + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                            Log.e(TAG, "Register failed.\n" + task.getException().getMessage());
                        }
                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void registerUser(final String nama, final String phone) {
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference ref = mDatabase.getReference(FBUtil.REF_TECHNICIAN_AC);

        final DatabaseReference userIdRef = ref.child(currentUser.getUid());

        Realm r = Realm.getDefaultInstance();
        try {
            r.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    BasicInfo user = new BasicInfo();
                    user.setUid(currentUser.getUid());
                    user.setName(nama.toUpperCase());
                    user.setUserType(Const.USER_AS_TECHNICIAN);

                    long timestamp = new Date().getTime();
                    user.setUpdatedTimestamp(timestamp);
                    user.setCreatedTimestamp(timestamp);
                    user.setPhone1(phone);

                    if (userIdRef != null) {
//                        userIdRef.child("basicInfo").setValue(user);

                        List<String> tokens = new ArrayList<>();
                        tokens.add(new SharedPrefUtil(getApplicationContext()).getString(Const.ARG_FIREBASE_TOKEN));
//                        userIdRef.child("firebaseToken").setValue(tokens);

                        Map<String, Object> keyVal = new HashMap<>();
                        keyVal.put("email", currentUser.getEmail());
                        keyVal.put("basicInfo", user);
                        keyVal.put("firebaseToken", tokens);

                        userIdRef.updateChildren(keyVal);

                    }

                    realm.copyToRealmOrUpdate(user);
                }
            });
        } finally {
            r.close();
        }

    }

}
