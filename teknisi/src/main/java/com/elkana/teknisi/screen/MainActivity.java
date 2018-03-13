package com.elkana.teknisi.screen;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.elkana.dslibrary.activity.FirebaseActivity;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.pojo.mitra.NotifyNewOrderItem;
import com.elkana.dslibrary.pojo.mitra.SubServiceType;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.pojo.user.FirebaseToken;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.teknisi.R;
import com.elkana.teknisi.job.SyncMovementJob;
import com.elkana.teknisi.pojo.MitraReg;
import com.elkana.teknisi.screen.login.ActivityLogin;
import com.elkana.teknisi.screen.order.ActivityNewOrder;
import com.elkana.teknisi.screen.profile.ActivityProfile;
import com.elkana.teknisi.util.DataUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends FirebaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MainActivityFragment.OnFragmentAssignmentistInteractionListener
{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUESTCODE_NEW_ORDER = 23;
    private View coordinatorLayout;
    TextView tvFullName;

    private MenuItem menuLogin;
    private MenuItem menuEditProfile;
    private MenuItem menuLogout;

//    private List<String> mitraIds = new ArrayList<>();

    private PendingIntent pendingIntent;

    // sementara hny listen dr 1 mitra dulu, ben ga pusing
    private DatabaseReference mNotifyNewOrderRef;
    private ValueEventListener mNotifyNewOrderListener;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        tvFullName = findViewById(R.id.tvFullName);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof MainActivityFragment) {
            ((MainActivityFragment) currentFragment).reInitiate(mAuth.getCurrentUser().getUid());
        }

        mNotifyNewOrderListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                }

                // TODO: sementara handle satu order dulu
                String orderId = null;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    NotifyNewOrderItem value = postSnapshot.getValue(NotifyNewOrderItem.class);
                    orderId = value.getOrderId();
                    break;
                }

                String mitraId = null;
                Realm r = Realm.getDefaultInstance();
                try{
                    RealmResults<MitraReg> all = r.where(MitraReg.class).findAll();
                    mitraId = all.get(0).getMitraId();
                }finally {
                    r.close();
                }

                if (TextUtils.isEmpty(mitraId) || TextUtils.isEmpty(orderId))
                    return;

                Intent i = new Intent(MainActivity.this, ActivityNewOrder.class);
                i.putExtra(ActivityNewOrder.PARAM_MITRA_ID, mitraId);
                i.putExtra(ActivityNewOrder.PARAM_ORDER_ID, orderId);
                startActivityForResult(i, REQUESTCODE_NEW_ORDER);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
            }
        };

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopJob();

        if (mNotifyNewOrderRef != null)
            mNotifyNewOrderRef.removeEventListener(mNotifyNewOrderListener);

    }

    @Override
    protected void onLoggedOff() {
        stopJob();

        if (menuLogin != null)
            menuLogin.setVisible(mAuth.getCurrentUser() == null);
        if (menuLogout != null) {
            menuLogout.setVisible(mAuth.getCurrentUser() != null);
        }
        if (menuEditProfile != null)
            menuEditProfile.setVisible(mAuth.getCurrentUser() != null);

        // TODO: inform fragments to cleanup listener if any
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof MainActivityFragment) {
            ((MainActivityFragment) currentFragment).cleanUpListener();
        }

        startActivity(new Intent(this, ActivityLogin.class));
//        startActivityForResult(new Intent(this, ActivityLogin.class), REQUESTCODE_LOGIN);
        finish();
    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {
        DatabaseReference userRef = database.getReference(DataUtil.REF_TECHNICIAN_AC)
                .child(user.getUid());

        userRef.child("basicInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    logout();

                    Snackbar.make(coordinatorLayout, getString(R.string.message_reregister), Snackbar.LENGTH_LONG)
                            .show();

                    return;
                }

                final BasicInfo basicInfo = dataSnapshot.getValue(BasicInfo.class);

                tvFullName.setText(basicInfo.getName());

                Realm r = Realm.getDefaultInstance();
                try {
                    r.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(basicInfo);
                        }
                    });
                } finally {
                    r.close();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
            }
        });

        userRef.child("firebaseToken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }

                final List<FirebaseToken> list = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String ss = postSnapshot.getValue(String.class);
                    FirebaseToken ft = new FirebaseToken();
                    ft.setToken(ss);
                    list.add(ft);
                }
                Realm r = Realm.getDefaultInstance();
                try{
                    r.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(list);
                        }
                    });
                }finally {
                    r.close();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
            }
        });

        userRef.child("mitra").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }
                Realm r = Realm.getDefaultInstance();

                try {
                    r.beginTransaction();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        MitraReg _obj = postSnapshot.getValue(MitraReg.class);
//                            Log.e(TAG, _obj.toString());

                        r.copyToRealmOrUpdate(_obj);

                        // to make sure cuma listen 1 mitra dulu
                        if (mNotifyNewOrderRef == null) {
                            mNotifyNewOrderRef = FBUtil.TechnicianReg_getNotifyNewOrderRef(_obj.getMitraId(), mAuth.getCurrentUser().getUid());
                            mNotifyNewOrderRef.addValueEventListener(mNotifyNewOrderListener);
                        }
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
//        mNotifyNewOrderRef = FBUtil.TechnicianReg_getNotifyNewOrder( mAuth.getCurrentUser().getUid());

        // download master data
        downloadMasterData();

        if (menuLogin != null) {
            menuLogin.setVisible(user == null);
        }
        if (menuLogout != null) {
            menuLogout.setVisible(user != null);
        }
        if (menuEditProfile != null) {
            menuEditProfile.setVisible(user != null);
        }

        startJob();

    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
            super.onBackPressed();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menuLogin = menu.findItem(R.id.action_login);
        menuLogout = menu.findItem(R.id.action_logout);
        menuEditProfile = menu.findItem(R.id.action_editProfile);

        menuLogin.setVisible(FirebaseAuth.getInstance().getCurrentUser() == null);
        menuLogout.setVisible(FirebaseAuth.getInstance().getCurrentUser() != null);
        menuEditProfile.setVisible(FirebaseAuth.getInstance().getCurrentUser() != null);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_login) {
//            startActivity(new Intent(this, ActivityLogin.class));
            return true;
        } else if (id == R.id.action_logout) {
            logout();

            finish();
            return true;
        } else if (id == R.id.action_editProfile) {
            Intent i = new Intent(this, ActivityProfile.class);
            startActivity(i);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Fragment getCurrentFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);

        return currentFragment;
    }

    /**
     * @see #stopJob()
     */
    public void startJob() {
        stopJob();

        Intent intentAlarm = new Intent(this, SyncMovementJob.class);

        if (pendingIntent == null) {
            pendingIntent = PendingIntent.getBroadcast(this, 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Calendar cal = Calendar.getInstance();
        // start 10 seconds from now
        cal.add(Calendar.SECOND, 10);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // repeat every 30 seconds
        alarmManager.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), 30 * 1000, pendingIntent);
        Log.i("fast.sync", "sync job started");

    }

    /**
     * @see #startJob()
     */
    public void stopJob() {

        if (pendingIntent == null) {
            return;
        }

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Log.i("fast.sync", "sync job stopped");

    }

    /**
     * Because it is important, dont terminate any at all. Internet is required !
     */
    public void downloadMasterData() {
//        final AlertDialog dialog = new SpotsDialog(this, "Loading Master Data");
//        dialog.show();

        // get subservice of ac
        database.getReference(DataUtil.REF_SUBSERVICEAC)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.exists())
                            return;

                        Realm _r = Realm.getDefaultInstance();
                        try{
                            _r.beginTransaction();
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                SubServiceType _obj = postSnapshot.getValue(SubServiceType.class);
                                Log.e(TAG, _obj.toString());

                                _r.copyToRealmOrUpdate(_obj);
                            }

                            _r.commitTransaction();

                        }finally {
                            _r.close();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
//                        dialog.dismiss();
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                    }
                });
    }

    @Override
    public void onStatusChange(EOrderDetailStatus status) {

    }
}
