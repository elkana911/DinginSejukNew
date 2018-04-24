package com.elkana.customer.screen.register;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elkana.customer.R;
import com.elkana.customer.pojo.MobileSetup;
import com.elkana.customer.screen.AFirebaseCustomerActivity;
import com.elkana.customer.util.CustomerUtil;
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

public class ActivityRegister extends AFirebaseCustomerActivity {
    private static final String TAG = ActivityRegister.class.getSimpleName();
    private static final int REQUESTCODE_MAP = 66;

    private RVAdapterUserAddress mAdapter;
    private List<UserAddress> mList = new ArrayList<>();;

    RecyclerView rvAddress;
    EditText mNama, mEmail, mPassword, mPhone;
    Button btnRegister, btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mNama = findViewById(R.id.nama);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.etPhone);

        btnSignIn = findViewById(R.id.btnPleaseSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(userFullName);
//            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(TAG);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(mobileSetup.getTheme_color_default())));
        }
        // if you want to center title https://stackoverflow.com/questions/18418635/how-to-align-title-at-center-of-actionbar-in-default-themetheme-holo-light
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.actionbar);

        // Set up the login form.
        mNama.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_person_outline_black_24dp, android.R.color.darker_gray), null, null, null);
        mPassword.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_lock_outline_black_24dp, android.R.color.darker_gray), null, null, null);
        mEmail.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_mail_outline_black_24dp, android.R.color.darker_gray), null, null, null);
        mPhone.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_phone_black_24dp, android.R.color.darker_gray), null, null, null);

        mAdapter = new RVAdapterUserAddress(this, mList, new ListenerAddressList(){

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

        if (Util.DEVELOPER_MODE && Util.TESTING_MODE) {
            mNama.setText("elkana911");
            mPassword.setText("elkana911");
            mEmail.setText("elkana911@yahoo.com");
            mPhone.setText("087886283377");
        }
    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUESTCODE_MAP) {

            if (resultCode == RESULT_OK) {
                //brarti user cancel poa, maka harus direset isinya
                String selectedLatitude = data.getStringExtra("latitude");
                String selectedLongitude = data.getStringExtra("longitude");
                String selectedAddr = data.getStringExtra("address");
                String selectedLabel = data.getStringExtra("label");

                boolean _found = false;
                for (UserAddress ua : mList) {
                    if (ua.getLabel().equalsIgnoreCase(selectedLabel)) {
                        _found = true;
                        break;
                    }
                }

                if (_found) {
                    Toast.makeText(this, "Label sudah ada. Mohon gunakan yang lainnya.", Toast.LENGTH_SHORT).show();
                } else {
                    UserAddress addr = new UserAddress();
                    addr.setUid(String.valueOf(new Date().getTime()));
                    addr.setLabel(selectedLabel.toUpperCase());
                    addr.setAddress(selectedAddr);
                    addr.setLatitude(selectedLatitude);
                    addr.setLongitude(selectedLongitude);
                    addr.setTimestamp(new Date().getTime());

                    mList.add(addr);
                    mAdapter.notifyDataSetChanged();
                }
            }

        }

    }

    private void attemptRegister(){
        boolean cancel = false;
        View focusView = null;
        // Reset errors.
        mNama.setError(null);
        mEmail.setError(null);
        mPassword.setError(null);
        mPhone.setError(null);

        // Store values at the time of the login attempt.
        final String nama = mNama.getText().toString().trim();
        final String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        final String phone = mPhone.getText().toString().trim();

        if (TextUtils.isEmpty(nama)) {
            mNama.setError(getString(R.string.error_field_required));
            focusView = mNama;
            cancel = true;
        }
        if (TextUtils.isEmpty(phone)) {
            mPhone.setError(getString(R.string.error_field_required));
            focusView = mPhone;
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
            mEmail.setError(getString(R.string.error_email_required));
            focusView = mEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return;
        }

        if (mList.size() < 1) {
//                    Snackbar.make(coordinatorLayout, getString(R.string.error_address_required), Snackbar.LENGTH_SHORT).show();
            Util.showErrorDialog(this, "Tambah Alamat", getString(R.string.error_address_required));
            return;
        }

        final AlertDialog alertDialog = Util.showProgressDialog(ActivityRegister.this, "Create account...");
        btnSignIn.setEnabled(false);
        btnRegister.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        alertDialog.dismiss();

                        btnSignIn.setEnabled(true);
                        btnRegister.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_SHORT).show();

                            registerUser(nama.toUpperCase(), phone, mList);
                            finish();
                        } else {
                            task.getException().printStackTrace();
                            Util.showDialog(ActivityRegister.this, null, getString(R.string.error_register));
                        }
                    }
                });

    }

    public void registerUser(final String nama, final String phone, final List<UserAddress> address) {
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference ref = mDatabase.getReference().child("users");

        final DatabaseReference userIdRef = ref.child(currentUser.getUid());

        Realm r = Realm.getDefaultInstance();
        try {
            r.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    BasicInfo user = new BasicInfo();
                    user.setUid(currentUser.getUid());
                    user.setName(nama.toUpperCase());
                    user.setUserType(Const.USER_AS_COSTUMER);

                    long timestamp = new Date().getTime();
                    user.setUpdatedTimestamp(timestamp);
                    user.setCreatedTimestamp(timestamp);
                    user.setPhone1(phone);

                    if (userIdRef != null) {
//                        userIdRef.child("basicInfo").setValue(user);

                        List<String> tokens = new ArrayList<>();
                        tokens.add(new SharedPrefUtil(getApplicationContext()).getString(Const.ARG_FIREBASE_TOKEN));

//                        userIdRef.child("firebaseToken").setValue(tokens);
//                        userIdRef.child("address").setValue(address);

                        for (UserAddress ua : address) {
                            // TODO: pastikan isi ua.getLabel tidak berisi tanda / krn akan dianggap path :( see https://stackoverflow.com/questions/19132867/adding-firebase-data-dots-and-forward-slashes
//                            userIdRef.child("address").child(ua.getLabel()).setValue(ua);
                            userIdRef.child("address").child(ua.getUid()).setValue(ua);
                        }

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


