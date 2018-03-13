package com.elkana.ds.mitraapp.screen.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elkana.ds.mitraapp.R;
import com.elkana.ds.mitraapp.screen.map.ActivityMaps;
import com.elkana.ds.mitraapp.screen.register.ListenerAddressList;
import com.elkana.ds.mitraapp.screen.register.RVAdapterUserAddress;
import com.elkana.ds.mitraapp.util.DataUtil;
import com.elkana.dslibrary.activity.FirebaseActivity;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.pojo.user.UserAddress;
import com.elkana.dslibrary.util.Util;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityProfile extends FirebaseActivity {

    private static final String TAG = "Profile";
    private static final int RESULTCODE_MAP = 66;
    public static final String MODE_EDIT_ADDRESS = "editAddress";

    private List<UserAddress> mList = new ArrayList<>();
    private RVAdapterUserAddress mAdapter;

    private EditText etNama, etEmail, etPhone;
    private Button btnUpdate;
    private RecyclerView rvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(userFullName);
//            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(TAG);
        }

        final BasicInfo basicInfo = this.realm.where(BasicInfo.class).findFirst();

        etNama = findViewById(R.id.nama);
        etEmail = findViewById(R.id.email);
        etPhone = findViewById(R.id.etPhone);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dont use dialog, blm ada logic yg cocok krn mau looping setValue takut infinite dialog
//        final AlertDialog dialog = new SpotsDialog(this);
//        dialog.show();
                btnUpdate.setEnabled(false);
                DatabaseReference usersRef = database.getReference(FBUtil.REF_MITRA_AC).child(mAuth.getCurrentUser().getUid()).child("address");

                // alamat boleh dihapus meski pernah order krn sudah distore di OrderHeader
                usersRef.setValue(null);


                for (int i = 0; i < mList.size(); i++) {
                    final UserAddress _ua = mList.get(i);

                    // yg row pertama adalah default address
                    _ua.setDefaultAddress(i == 0 ? true : false);

                    final int finalI = i;
                    usersRef.child(_ua.getLabel()).setValue(_ua).addOnSuccessListener(new OnSuccessListener<Void>() {
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


            }
        });

        etNama.setText(basicInfo.getName());
        etPhone.setText(basicInfo.getPhone1());
        etEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        RealmResults<UserAddress> addresses = this.realm.where(UserAddress.class)
                .findAll();
        mList.addAll(this.realm.copyFromRealm(addresses));

        rvAddress = findViewById(R.id.rvAddress);
        mAdapter = new RVAdapterUserAddress(this, mList, new ListenerAddressList() {
            @Override
            public void onSelectAddress(UserAddress address) {

            }

            @Override
            public void onAddAddress() {
                startActivityForResult(new Intent(ActivityProfile.this, ActivityMaps.class), RESULTCODE_MAP);
            }
        });
        rvAddress.setAdapter(mAdapter);
        rvAddress.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onLoggedOff() {

    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {

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
