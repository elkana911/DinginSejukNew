package com.elkana.teknisi.screen.profile;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elkana.dslibrary.activity.FirebaseActivity;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.pojo.mitra.Mitra;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.AFirebaseTeknisiActivity;
import com.elkana.teknisi.BuildConfig;
import com.elkana.teknisi.R;
import com.elkana.teknisi.pojo.MitraReg;
import com.elkana.teknisi.pojo.UserMitra;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityProfile extends AFirebaseTeknisiActivity {

    private static final String TAG = "Profile";
    private static final int REQUEST_CODE_MITRA = 32;
    private static final int REQUEST_CODE_SCANKTP = 6;

    private String mCurrentCameraPhotoPath;

    private EditText etNama, etEmail, etPhone;
    private Button btnUpdate;

    private List<UserMitra> mList = new ArrayList<>();
    private RVAdapterLamarMitra mAdapter;

    private RecyclerView rvMitra;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        FBUtil.Technician_GetMitraRef(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Realm r = Realm.getDefaultInstance();

                try {
                    r.beginTransaction();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        MitraReg _obj = postSnapshot.getValue(MitraReg.class);
//                            Log.e(TAG, _obj.toString());

                        r.copyToRealmOrUpdate(_obj);
                    }

                    r.commitTransaction();
                } finally{
                    r.close();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(userFullName);
//            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle(TAG);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(mobileSetup.getTheme_color_default())));
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
//                btnUpdate.setEnabled(false);

                BasicInfo copyFromRealm = realm.copyFromRealm(basicInfo);

//                DatabaseReference usersRef = database.getReference(TeknisiUtil.REF_TECHNICIAN_AC)
//                        .child(mAuth.getCurrentUser().getUid())
//                        .child("address");
//
//                // alamat boleh dihapus meski pernah order krn sudah distore di OrderHeader
//                usersRef.setValue(null);

            }
        });

        Button btnScanKTP = findViewById(R.id.btnScanKTP);
        btnScanKTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Ensure that there's a camera activity to handle the intent
                if (takePicture.resolveActivity(getPackageManager()) != null) {

                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = Util.createTempFileForCamera(ActivityProfile.this);  // /storage/emulated/0/Android/data/com.example.eric.cameranougat/files/Pictures/JPEG_20170318_113520_474001886.jpg
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        return;
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {

                        mCurrentCameraPhotoPath = photoFile.getAbsolutePath();

                        Uri photoURI = FileProvider.getUriForFile(ActivityProfile.this,
                                BuildConfig.APPLICATION_ID + ".provider",
                                photoFile);

                        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                        // fix kitkat
                        List<ResolveInfo> resInfoList = ActivityProfile.this.getPackageManager().queryIntentActivities(takePicture, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            ActivityProfile.this.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }

                        startActivityForResult(takePicture, REQUEST_CODE_SCANKTP);

                    }

                    /*
                    File cacheFile = new File(Environment.getExternalStorageDirectory().toString() + "/TopCache/" + "1.jpg");

                    Uri uriSavedImage = null; // content://id.co.ppu.collectionfast2.provider/external_files/RadanaCache/cache/poaDefault_demo_71000000008115.jpg
                    uriSavedImage = FileProvider.getUriForFile(ActivityProfile.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            cacheFile);

                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                    startActivityForResult(takePicture, REQUEST_CODE_SCANKTP);
                    */
                }
            }
        });


        etNama.setText(basicInfo.getName());
        etPhone.setText(basicInfo.getPhone1());
        etEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        RealmResults<UserMitra> addresses = this.realm.where(UserMitra.class)
                .findAll();
        mList.addAll(this.realm.copyFromRealm(addresses));

        mAdapter = new RVAdapterLamarMitra(this, mList, new ListenerMitraList(){

            @Override
            public void onAddMitra() {
                startActivityForResult(new Intent(ActivityProfile.this, ActivityMitraList.class), REQUEST_CODE_MITRA);

            }

            @Override
            public void onItemSelected(Mitra obj) {

            }

        });
// sementara pendaftaran teknisi dilakukan  di mitra
        rvMitra = findViewById(R.id.rvMitra);
        rvMitra.setAdapter(mAdapter);
        rvMitra.setLayoutManager(new LinearLayoutManager(this));

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

        if (requestCode != REQUEST_CODE_MITRA) {
            return;
        }

        if (resultCode != RESULT_OK)
            return;

        if (requestCode == REQUEST_CODE_MITRA) {
            String selectedUid = data.getStringExtra("mitra.uid");
            String selectedName = data.getStringExtra("mitra.name");
            String selectedEmail = data.getStringExtra("mitra.email");
            String selectedAddr = data.getStringExtra("mitra.address");

            for (UserMitra um : mList) {
                if (um.getEmail().equalsIgnoreCase(selectedEmail)) {
                    Toast.makeText(ActivityProfile.this, "Sudah ada.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            UserMitra userMitra = new UserMitra();
            userMitra.setUid(selectedUid);
            userMitra.setName(selectedName);
            userMitra.setEmail(selectedEmail);
            userMitra.setAddress(selectedAddr);

            mList.add(userMitra);
            mAdapter.notifyDataSetChanged();

        } else if (requestCode == REQUEST_CODE_SCANKTP) {
            // Show the thumbnail on ImageView
            final Uri imageUri = Uri.parse(mCurrentCameraPhotoPath); // /storage/emulated/0/Android/data/com.example.eric.cameranougat/files/Pictures/JPEG_20170318_201833_1993305013.jpg
            File file = new File(imageUri.getPath());                 // /storage/emulated/0/Android/data/com.example.eric.cameranougat/files/Pictures/JPEG_20170318_201833_1993305013.jpg
            try {
                InputStream ims = new FileInputStream(file);

//                View v = navigationView.getHeaderView(0);
//                final ImageView imageView = ButterKnife.findById(v, R.id.imageView);
//
//                imageView.setImageBitmap(BitmapFactory.decodeStream(ims));

            } catch (FileNotFoundException e) {
                return;
            }

//            Storage.savePref(Storage.KEY_USER_PHOTO_PROFILE_URI, imageUri.toString());

//            if (activeFrag instanceof HomeFragment) {
//                ((HomeFragment)activeFrag).refreshProfile();
//            }

        }
    }

}
