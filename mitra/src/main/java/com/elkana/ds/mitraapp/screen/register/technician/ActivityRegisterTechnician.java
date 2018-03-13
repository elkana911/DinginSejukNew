package com.elkana.ds.mitraapp.screen.register.technician;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elkana.ds.mitraapp.R;
import com.elkana.dslibrary.activity.FirebaseActivity;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerGetBasicInfo;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.pojo.mitra.TechnicianReg;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.pojo.user.FirebaseToken;
import com.elkana.dslibrary.util.Util;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.List;

public class ActivityRegisterTechnician extends FirebaseActivity {
    private final String TAG = ActivityRegisterTechnician.class.getSimpleName();
    Button btnRegister;
    EditText etEmail, etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_technician);

        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(userFullName);
//            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle(TAG);
//            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(mobileSetup.getTheme_color_default())));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                window.setStatusBarColor(Color.parseColor(mobileSetup.getTheme_color_default()));
            }
        }

        etEmail = findViewById(R.id.email);
        etPhone = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

//        etEmail.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_mail_outline_black_24dp, android.R.color.white), null, null, null);
//        etPhone.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_phone_black_24dp, android.R.color.white), null, null, null);


        if (Util.DEVELOPER_MODE && Util.TESTING_MODE) {
            etEmail.setText("liapharm@yahoo.com");
            etPhone.setText("0812345");
        }
    }

    private void attemptRegister() {
        boolean cancel = false;
        View focusView = null;
        // Reset errors.
        etEmail.setError(null);
        etPhone.setError(null);

        // Store values at the time of the login attempt.
        final String email = etEmail.getText().toString();
        String phone = etPhone.getText().toString();

        if (!Util.isEmailValid(email)) {
            etEmail.setError(getString(R.string.error_email_required));
            focusView = etEmail;
            cancel = true;
        }

        if (!Util.isPhoneValid(phone)) {
            etPhone.setError(getString(R.string.error_field_required));
            focusView = etPhone;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return;
        }

        final AlertDialog dialog = Util.showProgressDialog(this, "Checking " + email);

        FBUtil.Technician_findByEmail(email, new ListenerGetBasicInfo() {
            @Override
            public void onFound(BasicInfo basicInfo, List<FirebaseToken> list) {
                dialog.dismiss();

                String techUid = basicInfo.getUid();

                final TechnicianReg techReg = new TechnicianReg();
                techReg.setTechId(techUid);
                techReg.setJoinDate(new Date().getTime());
                techReg.setSuspend(false);
                techReg.setOrderTodayCount(0);
                techReg.setName(basicInfo.getName());

                // register
                final AlertDialog _dialog = Util.showProgressDialog(ActivityRegisterTechnician.this, "Registering " + email);

                FBUtil.Mitra_registerTech(mAuth.getCurrentUser().getUid(), techReg, new ListenerModifyData(){
                    @Override
                    public void onSuccess() {
                        _dialog.dismiss();
                        // send  notification to technicians
                        Util.showDialog(ActivityRegisterTechnician.this, "Register Teknisi","Register "+ techReg.getName() + " sukses.");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, e.getMessage(),e);
                        _dialog.dismiss();
                        Util.showErrorDialog(ActivityRegisterTechnician.this, "Register Gagal", e.getMessage());
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, e.getMessage(),e);
                dialog.dismiss();
                Toast.makeText(ActivityRegisterTechnician.this, e.getMessage(),  Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onLoggedOff() {

    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {

    }
}
