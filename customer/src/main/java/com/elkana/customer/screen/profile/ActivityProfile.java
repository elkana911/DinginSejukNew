package com.elkana.customer.screen.profile;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elkana.customer.R;
import com.elkana.customer.screen.AFirebaseCustomerActivity;
import com.elkana.customer.screen.register.ActivityMapsUserAddress;
import com.elkana.customer.screen.register.ListenerAddressList;
import com.elkana.customer.screen.register.RVAdapterUserAddress;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.pojo.user.UserAddress;
import com.elkana.dslibrary.util.Util;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityProfile extends AFirebaseCustomerActivity {

    private static final String TAG = "Profile";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 413;
    private static final int RESULTCODE_MAP = 66;

    public static final String MODE_EDIT_ADDRESS = "editAddress";

    private List<UserAddress> mList = new ArrayList<>();
    private RVAdapterUserAddress mAdapter;
    EditText etNama, etEmail, etPhone;

    RecyclerView rvAddress;

    Button btnUpdate;

    /*
    @BindView(R.id.nama)
    EditText etNama;

    @BindView(R.id.email)
    EditText etEmail;

    @BindView(R.id.etPhone)
    EditText etPhone;

    @BindView(R.id.rvAddress)
    RecyclerView rvAddress;

    @BindView(R.id.btnUpdate)
    Button btnUpdate;
*/

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                },
                MY_PERMISSIONS_REQUEST_LOCATION);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

//        ButterKnife.bind(this);
        etNama = findViewById(R.id.nama);
        etEmail = findViewById(R.id.email);
        etPhone = findViewById(R.id.etPhone);

        rvAddress = findViewById(R.id.rvAddress);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
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

        String mode = getIntent().getStringExtra("mode");

        if (mode.equals(MODE_EDIT_ADDRESS)) {
            etNama.setEnabled(false);
            etEmail.setEnabled(false);
            etPhone.setEnabled(false);
        }

        BasicInfo basicInfo = this.realm.where(BasicInfo.class).findFirst();

        etNama.setText(basicInfo.getName());
        etPhone.setText(basicInfo.getPhone1());
        etEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        etNama.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_person_outline_black_24dp, android.R.color.darker_gray), null, null, null);
        etEmail.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_mail_outline_black_24dp, android.R.color.darker_gray), null, null, null);
        etPhone.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_phone_black_24dp, android.R.color.darker_gray), null, null, null);


        RealmResults<UserAddress> addresses = this.realm.where(UserAddress.class)
                .findAll();
        mList.addAll(this.realm.copyFromRealm(addresses));

        mAdapter = new RVAdapterUserAddress(this, mList, new ListenerAddressList() {
            @Override
            public void onSelectAddress(UserAddress address) {

            }

            @Override
            public void onAddAddress() {
                startActivityForResult(new Intent(ActivityProfile.this, ActivityMapsUserAddress.class), RESULTCODE_MAP);
            }
        });
        rvAddress.setAdapter(mAdapter);
        rvAddress.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void updateProfile() {
        // dont use dialog, blm ada logic yg cocok krn mau looping setValue takut infinite dialog
//        final AlertDialog dialog = new SpotsDialog(this);
//        dialog.show();
        btnUpdate.setEnabled(false);

        DatabaseReference usersRef = mDatabase.getReference("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("address");

        // alamat boleh dihapus meski pernah order krn sudah distore di OrderHeader
        usersRef.setValue(null);


        for (int i = 0; i < mList.size(); i++) {
            final UserAddress _ua = mList.get(i);

            // yg row pertama adalah default address
            _ua.setDefaultAddress(i == 0);

            final int finalI = i;

//            usersRef.child(_ua.getLabel()).setValue(_ua).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
            usersRef.child(_ua.getUid()).setValue(_ua).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    if (finalI == mList.size() - 1) {

                        Realm _r = Realm.getDefaultInstance();
                        try {
                            _r.beginTransaction();
                            _r.where(UserAddress.class).findAll().deleteAllFromRealm();
                            _r.copyToRealmOrUpdate(mList);
                            _r.commitTransaction();
                        } finally {
                            _r.close();
                        }

                        btnUpdate.setEnabled(true);
//                        dialog.dismiss();
                        Util.showDialog(ActivityProfile.this,getString(R.string.action_edit_profile), getString(R.string.message_profile_updated));
                    }

                }
            });
        }

        /*
        usersRef.setValue(mList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                btnUpdate.setEnabled(true);
                dialog.dismiss();

                if (task.isSuccessful()) {
                    Realm _r = Realm.getDefaultInstance();
                    try {
                        _r.beginTransaction();
                        _r.copyToRealmOrUpdate(mList);
                        _r.commitTransaction();
                    } finally {
                        _r.close();
                    }

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityProfile.this);
                    alertDialogBuilder.setTitle(getString(R.string.action_edit_profile));
                    alertDialogBuilder.setMessage(getString(R.string.message_profile_updated));
                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertDialogBuilder.show();
                }
            }
        });*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULTCODE_MAP) {
            if (resultCode != RESULT_OK)
                return;

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
                Toast.makeText(this, getString(R.string.error_label_conflict), Toast.LENGTH_SHORT).show();
            } else {
                UserAddress addr = new UserAddress();
                addr.setUid(String.valueOf(new Date().getTime()));
                addr.setLabel(selectedLabel);
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
