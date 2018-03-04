package com.elkana.ds.mitraapp.screen.register.technician;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elkana.ds.mitraapp.R;
import com.elkana.ds.mitraapp.pojo.TechnicianReg;
import com.elkana.ds.mitraapp.util.DataUtil;
import com.elkana.dslibrary.activity.FirebaseActivity;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

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
            getSupportActionBar().setTitle(TAG);
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

        if (!isEmailValid(email)) {
            etEmail.setError(getString(R.string.error_email_required));
            focusView = etEmail;
            cancel = true;
        }

        if (!isPhoneValid(phone)) {
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

        // technican was registered ?
        database.getReference(DataUtil.REF_TECHNICIAN_AC).orderByChild("email").equalTo(email).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialog.dismiss();
                if (!dataSnapshot.exists()) {
                    Toast.makeText(ActivityRegisterTechnician.this, getString(R.string.error_technician_not_found), Toast.LENGTH_SHORT).show();
                    return;
                }

//                Map<String,Object> keyVal = (Map<String, Object>) dataSnapshot.getChildren().iterator().next().getValue();
//                BasicInfo basicInfo = (BasicInfo) keyVal.get("basicInfo");

                BasicInfo basicInfo = dataSnapshot.getChildren().iterator().next().getChildren().iterator().next().getValue(BasicInfo.class);
//                BasicInfo basicInfo = (BasicInfo) dataSnapshot.getChildren().iterator().next().child("basicInfo").getValue();
                String techUid = basicInfo.getUid();

                TechnicianReg techReg = new TechnicianReg();
                techReg.setTechId(techUid);
                techReg.setJoinDate(new Date().getTime());
                techReg.setSuspend(false);
                techReg.setOrderTodayCount(0);
                techReg.setName(basicInfo.getName());

                database.getReference(DataUtil.REF_MITRA_AC)
                        .child(mAuth.getUid())
                        .child("technicians")
                        .child(techUid)
                        .setValue(techReg).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            finish();
                        } else {
                            Toast.makeText(ActivityRegisterTechnician.this, getString(R.string.error_add_tech), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//                Map<String,Object> keyVal = (Map<String, Object>) dataSnapshot.getChildren().iterator().next().getValue();
//                keyVal.get("basicInfo")
//                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();
                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
            }
        });


    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && email.contains("@") && email.length() > 4;
    }

    private boolean isPhoneValid(String phone) {
        return !TextUtils.isEmpty(phone) && phone.length() > 4;
    }

    @Override
    protected void onLoggedOff() {

    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {

    }
}
