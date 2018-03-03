package com.elkana.teknisi.screen.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.elkana.dslibrary.activity.FirebaseActivity;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerGetAllData;
import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.pojo.mitra.Mitra;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.R;
import com.elkana.teknisi.component.RealmSearchView;
import com.elkana.teknisi.pojo.UserMitra;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import io.realm.Realm;

public class ActivityMitraList extends FirebaseActivity {

    private static final String TAG = ActivityMitraList.class.getSimpleName();

    private RVAdapterMitra mAdapter;
    RealmSearchView search_view;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        final AlertDialog dialog = Util.showProgressDialog(this);

        FBUtil.Mitra_getAllMitra(new ListenerGetAllData() {
            @Override
            public void onSuccess(List<?> list) {
                Realm r = Realm.getDefaultInstance();
                try{
                    r.beginTransaction();

                    for (int i = 0; i < list.size(); i++) {
                        if (!(list.get(i) instanceof Mitra))
                            continue;

                        Mitra _mitra = (Mitra)list.get(i);

                        r.copyToRealmOrUpdate(_mitra);
                    }

                    r.commitTransaction();

                    mAdapter.notifyDataSetChanged();
                }finally {
                    r.close();
                }

                dialog.dismiss();
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mitra_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(userFullName);
//            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
//            getSupportActionBar().setTitle(TAG);

//            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(mobileSetup.getTheme_color_default())));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                window.setStatusBarColor(Color.parseColor(mobileSetup.getTheme_color_default()));
            }

        }

        mAdapter = new RVAdapterMitra(this, realm, "name", new ListenerMitraList() {
            @Override
            public void onAddMitra() {

            }

            @Override
            public void onItemSelected(final Mitra obj) {

                // you already apply this mitra
                Realm r = Realm.getDefaultInstance();
                try{
                    long count = r.where(UserMitra.class)
                            .equalTo("email", obj.getEmail()).count();

                    if (count > 0) {
                        Toast.makeText(ActivityMitraList.this, "Sudah terpilih sebelumnya.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }finally {
                    r.close();
                }

                Util.showDialogConfirmation(ActivityMitraList.this, "Daftar Mitra", "Pilih " + obj.getName().toUpperCase() + " ?", new ListenerPositiveConfirmation() {
                    @Override
                    public void onPositive() {

                        Intent data = new Intent();
                        data.putExtra("mitra.id", obj.getUid());
                        data.putExtra("mitra.name", obj.getName());
                        data.putExtra("mitra.address", obj.getAddressByGoogle());
                        data.putExtra("mitra.email", obj.getEmail());
                        setResult(RESULT_OK, data);
                        finish();

                    }
                });
            }
        });

        search_view = findViewById(R.id.search_view);
        search_view.setAdapter(mAdapter);


    }

    @Override
    protected void onLoggedOff() {

    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {

    }


}
