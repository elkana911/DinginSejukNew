package com.elkana.ds.mitraapp.screen.register;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elkana.ds.mitraapp.R;
import com.elkana.ds.mitraapp.pojo.MobileSetup;
import com.elkana.dslibrary.activity.FirebaseActivity;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.map.Location;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.pojo.user.UserAddress;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.SharedPrefUtil;
import com.elkana.dslibrary.util.Util;
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

public class ActivityRegister extends FirebaseActivity {

    private static final String TAG = "Register";
    private static final int REQUESTCODE_MAP = 66;

    // berbeda dengan TopCustomer, UserAddress ditiadakan. akan digabung dengan basicInfo karena mitra cuma butuh 1 alamat per cabang.
    private List<UserAddress> mListAddress = new ArrayList<>();
    private RVAdapterUserAddress mAdapter;

    RecyclerView rvAddress;
    EditText mNama, mEmailView, etPhone, mPassword;
    Button btnRegister, btnPleaseSignIn;
//    LoginButton btnLogin;
    View coordinatorLayout;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Location.pleaseTurnOnGPS(this);

//        startActivityForResult(new Intent(this, ActivityMaps.class), REQUESTCODE_MAP); dont do this, firebaseactivity will finish automatically

        if (Util.TESTING_MODE) {
            mNama.setText("PT. Eric Bersabda");
            mEmailView.setText("eric.elkana@ppu.co.id");
            mPassword.setText("eric.elkana");
            etPhone.setText("0777777");
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
//        btnLogin = findViewById(R.id.login_facebook);
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
//                startActivity(new Intent(ActivityRegister.this, ActivityLogin.class));
                finish();
            }
        });

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

        mAdapter = new RVAdapterUserAddress(this, mListAddress, new ListenerAddressList() {
            @Override
            public void onSelectAddress(UserAddress address) {

            }

            @Override
            public void onAddAddress() {
                startActivityForResult(new Intent(ActivityRegister.this, ActivityMapsUserAddress.class), REQUESTCODE_MAP);
            }
        });

        rvAddress = findViewById(R.id.rvAddress);
        rvAddress.setAdapter(mAdapter);
        rvAddress.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onLoggedOff() {

    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {
//        setResult(RESULT_OK);
//        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUESTCODE_MAP)
            return;

        if (resultCode != RESULT_OK)
            return;

        //brarti user cancel poa, maka harus direset isinya
        String selectedLatitude = data.getStringExtra("latitude");
        String selectedLongitude = data.getStringExtra("longitude");
        String selectedAddr = data.getStringExtra("address");
        String selectedLabel = data.getStringExtra("label");

        if (selectedLatitude == null) {
            Toast.makeText(this, getString(R.string.error_gps), Toast.LENGTH_SHORT).show();
            return;
        }

        boolean _found = false;
        for (UserAddress ua : mListAddress) {
            if (ua.getLabel().equalsIgnoreCase(selectedLabel)) {
                _found = true;
                break;
            }
        }

        if (_found) {
            Toast.makeText(this, getString(R.string.error_label_conflict), Toast.LENGTH_SHORT).show();
        } else {
            UserAddress addr = new UserAddress();
            addr.setLabel(selectedLabel.toUpperCase());
            addr.setAddress(selectedAddr);
            addr.setLatitude(selectedLatitude);
            addr.setLongitude(selectedLongitude);
            addr.setTimestamp(new Date().getTime());

            mListAddress.add(addr);
            mAdapter.notifyDataSetChanged();
        }
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

        if (mListAddress.size() < 1) {
            Snackbar.make(coordinatorLayout, getString(R.string.error_address_required), Snackbar.LENGTH_SHORT).show();
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

                            registerUser(nama, phone, mListAddress.get(0));

                            finish();
                        } else {
                            Log.e(TAG, "Register failed.\n" + task.getException().getMessage(), task.getException());
                            Snackbar.make(coordinatorLayout, getString(R.string.error_register), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    public void registerUser(final String nama, final String phone, final UserAddress address) {
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference ref = mDatabase.getReference(FBUtil.REF_MITRA_AC);

        final DatabaseReference userIdRef = ref.child(currentUser.getUid());

        Realm r = Realm.getDefaultInstance();
        try {
            r.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    BasicInfo user = new BasicInfo();
                    user.setUid(currentUser.getUid());
                    user.setName(nama.toUpperCase());
                    user.setUserType(Const.USER_AS_MITRA);

                    user.setEnable(true);
                    user.setVisible(true);
                    user.setRating(50);
                    user.setAddressLabel(address.getLabel());
                    user.setAddressByGoogle(address.getAddress());
                    user.setLatitude(address.getLatitude());
                    user.setLongitude(address.getLongitude());
                    user.setWorkingHourStart(8);
                    user.setWorkingHourEnd(17);
                    user.setEmail(currentUser.getEmail());

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
