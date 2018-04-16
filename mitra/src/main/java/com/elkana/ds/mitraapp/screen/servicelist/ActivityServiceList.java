package com.elkana.ds.mitraapp.screen.servicelist;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.elkana.ds.mitraapp.AFirebaseMitraActivity;
import com.elkana.ds.mitraapp.R;
import com.elkana.ds.mitraapp.util.MitraUtil;
import com.elkana.dslibrary.listener.ListenerGetAllData;
import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.pojo.mitra.SubServiceType;
import com.elkana.dslibrary.util.Util;

import java.util.List;

public class ActivityServiceList extends AFirebaseMitraActivity {
    private static final String TAG = ActivityServiceList.class.getSimpleName();

    boolean changes = false;
    RecyclerView rvServiceList;
    RVAdapterServiceList mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

//        MobileSetup mobileSetup = MitraUtil.getMobileSetup();
        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(userFullName);
//            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle(TAG);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                window.setStatusBarColor(Color.parseColor(mobileSetup.getTheme_color_default()));
            }

        }
        rvServiceList = findViewById(R.id.rvServiceList);
        rvServiceList.setLayoutManager(new LinearLayoutManager(this));

        // cek data masternya, kalo blm ada tarik lagi
        MitraUtil.syncServices(new ListenerGetAllData() {
            @Override
            public void onSuccess(List<?> list) {
                mAdapter = new RVAdapterServiceList(ActivityServiceList.this, mAuth.getCurrentUser().getUid(), (List<SubServiceType>) list, new ListenerGetAllData() {
                    @Override
                    public void onSuccess(List<?> list) {

                    }

                    @Override
                    public void onError(Exception e) {

                    }

                    @Override
                    public void onPrepare() {
                    }
                });

                rvServiceList.setAdapter(mAdapter);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ActivityServiceList.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onPrepare() {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAdapter != null)
            mAdapter.cleanUpListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_service_list, menu);

        Drawable drawable = menu.findItem(R.id.action_submit).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, android.R.color.white));
        menu.findItem(R.id.action_submit).setIcon(drawable);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_submit) {

            Util.showDialogConfirmation(this, "Data Change", "Submit data ?", new ListenerPositiveConfirmation() {
                @Override
                public void onPositive() {

                    if (mAdapter.saveData()) {
                        ActivityServiceList.this.changes = false;
                        setResult(RESULT_OK);
                        finish();
                    }

                }
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
