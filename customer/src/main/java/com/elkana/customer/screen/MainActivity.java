package com.elkana.customer.screen;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.elkana.customer.R;
import com.elkana.customer.screen.order.FragmentMitraListInRange;
import com.elkana.customer.screen.order.FragmentOrderAC;
import com.elkana.customer.screen.order.FragmentOrderList;
import com.elkana.customer.screen.order.FragmentSummaryOrder;
import com.elkana.customer.screen.profile.ActivityProfile;
import com.elkana.customer.screen.register.ActivityWelcomeNewUser;
import com.elkana.customer.pojo.MobileSetup;
import com.elkana.customer.util.DataUtil;
import com.elkana.dslibrary.activity.FirebaseActivity;
import com.elkana.dslibrary.fragment.AdapterFragments;
import com.elkana.dslibrary.listener.ListenerSync;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.mitra.TmpMitra;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.pojo.user.FirebaseToken;
import com.elkana.dslibrary.pojo.user.UserAddress;
import com.elkana.dslibrary.util.EOrderStatus;
import com.elkana.dslibrary.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends FirebaseActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , FragmentOrderList.OnFragmentOrderListInteractionListener
        , FragmentOrderAC.OnFragmentOrderACInteractionListener
        , FragmentSummaryOrder.OnFragmentSOInteractionListener
        , FragmentMitraListInRange.OnFragmentMitraListInRangeInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String ORDER_NEW_ID = "order.new.id";

    private static final int PAGE_ORDER_LIST = 0;
    private static final int PAGE_ORDER_DETAIL = 1;
    private static final int PAGE_SERVICE_CHOICE = 2;

    private boolean disableSwipeScreen = true;

    private AdapterFragments pageAdapter;

    View coordinatorLayout;
    AHBottomNavigation bottomNavigation;
    ViewPager viewPager;

    private MenuItem menuEditProfile, menuLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileSetup mobileSetup = this.realm.where(MobileSetup.class).findFirst();

        boolean firstTime = false;
        if (firstTime)
            startActivity(new Intent(this, ActivityWelcomeNewUser.class));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.container);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.viewpager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Call HOTLine", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        List<Fragment> fList = new ArrayList<Fragment>();
        // PAGE_ORDER_LIST
        fList.add(FragmentOrderList.newInstance(currentUser == null ? null : currentUser.getUid(), null));
        // PAGE_ORDER_DETAIL
        fList.add(FragmentSummaryOrder.newInstance());
        // PAGE_SERVER_CHOICE
        fList.add(FragmentOrderAC.newInstance(currentUser == null ? null : currentUser.getUid(), null));
//        fList.add(FragmentSvcChoice.newInstance(currentUser == null ? null : currentUser.getUid(), null));

        pageAdapter = new AdapterFragments(getSupportFragmentManager(), fList);

        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                bottomNavigation.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (disableSwipeScreen) {
            viewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.title_orders, R.drawable.ic_filter_list_black_24dp, android.R.color.white);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.title_status, R.drawable.ic_panorama_fish_eye_black_24dp, android.R.color.white);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.title_add_service, R.drawable.ic_add_black_24dp, android.R.color.white);

// Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

//        bottomNavigation.setDefaultBackgroundColor(Color.parseColor(mobileSetup.getTheme_color_ac()));    pending
//        bottomNavigation.setAccentColor(Color.parseColor(mobileSetup.getTheme_color_ac_accent()));
//        bottomNavigation.setInactiveColor(Color.parseColor(mobileSetup.getTheme_color_ac_inactive()));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
//        bottomNavigation.setColored(true);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

//                int x = getSupportFragmentManager().getBackStackEntryCount();
//
//                return gotoScreen(position);
                int x = viewPager.getCurrentItem();

                if (x != position)
                    onGoToScreen(position, true, true);

                return true;
            }
        });


        // get user info
        DatabaseReference usersRef = database.getReference("users").child(currentUser.getUid());
        DatabaseReference basicInfoRef = usersRef.child("basicInfo");
        basicInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    logout();

//                    Snackbar.make(coordinatorLayout, getString(R.string.message_reregister), Snackbar.LENGTH_LONG)
//                            .show();

                    return;
                }

                final BasicInfo basicInfo = dataSnapshot.getValue(BasicInfo.class);

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

        DatabaseReference userAddressesRef = usersRef.child("address");
        userAddressesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }

                final List<UserAddress> list = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    list.add(postSnapshot.getValue(UserAddress.class));
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

        DatabaseReference tokensRef = usersRef.child("firebaseToken");
        tokensRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

        if (menuLogout != null) {
            menuLogout.setVisible(currentUser != null);
        }
        if (menuEditProfile != null) {
            menuEditProfile.setVisible(currentUser != null);
        }

        prepareScreen(false);


    }

    @Override
    protected void onLoggedOff() {
        if (menuLogout != null) {
            menuLogout.setVisible(FirebaseAuth.getInstance().getCurrentUser() != null);
        }
        if (menuEditProfile != null)
            menuEditProfile.setVisible(FirebaseAuth.getInstance().getCurrentUser() != null);
//        DataUtil.initiateOfflineData();

//        prepareScreen();
    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {
//        dont put any view logic here krn bisa dipanggil sebelum activity ready after login. let oncreate handle this
        final AlertDialog dialog = Util.showProgressDialog(this, "Loading user information...");

        DataUtil.syncUserInformation(this.realm);

        DataUtil.syncMitra(this, new ListenerSync() {
            @Override
            public void onPostSync(Exception e) {
                if (e == null) {
                    DataUtil.syncOrders(MainActivity.this, mAuth.getCurrentUser().getUid(), new ListenerSync() {
                        @Override
                        public void onPostSync(Exception e) {
                            prepareScreen(true);
                            dialog.dismiss();

                        }
                    });
                } else {
                    prepareScreen(true);
                    dialog.dismiss();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        menuLogout = menu.findItem(R.id.action_logout);
        menuEditProfile = menu.findItem(R.id.action_editProfile);

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
        } else if (id == R.id.action_logout) {
            logout();

            finish();

            return true;
        } else if (id == R.id.action_editProfile) {
//            Intent i = new Intent(this, ActivityProfile.class);
//            i.putExtra("mode", ActivityProfile.MODE_EDIT_ADDRESS);
//            startActivity(i);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void cleanTransactionData() {
        super.cleanTransactionData();
        // TODO: clean any transaction data here

    }

    private void prepareScreen(boolean forceRefresh) {

        long orderCount = this.realm.where(OrderHeader.class)
//                .equalTo("customerId", customerId)
                .equalTo("statusId", EOrderStatus.PENDING.name())
                .count();

        if (orderCount < 1) {
//            onGoToScreen(PAGE_SERVICE_CHOICE, true, false);
            onGoToScreen(PAGE_ORDER_LIST, forceRefresh, false);
        } else if (orderCount == 1) {
            onGoToScreen(PAGE_ORDER_DETAIL, forceRefresh, false);
        } else {
            onGoToScreen(PAGE_ORDER_LIST, forceRefresh, false);
        }
        bottomNavigation.setVisibility(orderCount > 0 ? View.VISIBLE : View.GONE);

    }

    public void onGoToScreen(int pageIndex, boolean refresh, boolean animation) {

        if (pageIndex + 1 > viewPager.getAdapter().getCount()) {
            Toast.makeText(this, "Page " + pageIndex + " not available. Check your page", Toast.LENGTH_SHORT).show();
            return;
        }

        viewPager.setCurrentItem(pageIndex, animation);

        if (refresh) {

            Fragment _fragment = pageAdapter.getItem(pageIndex);
            if (pageIndex == PAGE_ORDER_LIST) {
                if (_fragment instanceof FragmentOrderList) {
                    if (mAuth != null && mAuth.getCurrentUser() != null)
                        ((FragmentOrderList) _fragment).reInitiate(this, mAuth.getCurrentUser().getUid());
                }

            } else if (pageIndex == PAGE_ORDER_DETAIL) {
                if (_fragment instanceof FragmentSummaryOrder) {
                    ((FragmentSummaryOrder) _fragment).reInitiate(mAuth.getCurrentUser().getUid(), null);
//                    ((FragmentSummaryOrder) _fragment).displayOrder(mAuth.getCurrentUser().getUid(), null);
                }

            } else if (pageIndex == PAGE_SERVICE_CHOICE) {
                if (_fragment instanceof FragmentOrderAC) {
                    if (mAuth != null && mAuth.getCurrentUser() != null)
                        ((FragmentOrderAC) _fragment).reInitiate(mAuth.getCurrentUser().getUid(), null);
                }
            }

            // cek bottomnav
            long orderCount = this.realm.where(OrderHeader.class)
//                .equalTo("customerId", customerId)
                    .equalTo("statusId", EOrderStatus.PENDING.name())
                    .count();
            bottomNavigation.setVisibility(orderCount > 0 ? View.VISIBLE : View.GONE);
        }

    }

    @Override
    public void onItemOrderSelected(OrderHeader order) {
        Fragment fragment = pageAdapter.getItem(PAGE_ORDER_DETAIL);

        if (fragment instanceof FragmentSummaryOrder) {
            ((FragmentSummaryOrder) fragment).reInitiate(order);
        }

        onGoToScreen(PAGE_ORDER_DETAIL, false, true);
    }

    @Override
    public void onAddOrder() {
        onGoToScreen(PAGE_SERVICE_CHOICE, true, false);
    }

    @Override
    public void onUpdateOrder(String customerId) {
        Realm r = Realm.getDefaultInstance();
        try {
            long count = r.where(OrderHeader.class)
                    .equalTo("customerId", customerId)
                    .equalTo("statusId", EOrderStatus.PENDING.name())
                    .count();

            bottomNavigation.setNotification(String.valueOf(count), PAGE_ORDER_LIST);
        } finally {
            r.close();
        }
    }

    @Override
    public void onCancelOrder(OrderHeader order) {
        Toast.makeText(this, "You will cancel " + order.getUid(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOrderCancelled(int serviceType, String invoiceNo) {
        final Snackbar snackbar = Snackbar.make(coordinatorLayout, getString(R.string.message_order_cancelled, DataUtil.getServiceTypeLabel(this, serviceType), invoiceNo), Snackbar.LENGTH_LONG);
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();

        onGoToScreen(PAGE_ORDER_LIST, true, true);
    }

    @Override
    public void onOrderRescheduled(int serviceType, String invoiceNo) {
        final Snackbar snackbar = Snackbar.make(coordinatorLayout, getString(R.string.message_order_rescheduled, DataUtil.getServiceTypeLabel(this, serviceType), invoiceNo), Snackbar.LENGTH_LONG);
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();

        onGoToScreen(PAGE_ORDER_LIST, true, true);
    }

    @Override
    public void onClickSelectMitra(List<TmpMitra> mitraInRange) {
        Realm r = Realm.getDefaultInstance();
        try {
            r.beginTransaction();
            r.where(TmpMitra.class)
                    .findAll().deleteAllFromRealm();

            r.copyToRealmOrUpdate(mitraInRange);
            r.commitTransaction();
        } finally {
            r.close();
        }

        DialogFragment d = new FragmentMitraListInRange();
        Bundle bundle = new Bundle();
//        bundle.putString(FragmentMitraList.ARG_PARAM1, this.collectorId);
//        bundle.putString(FragmentMitraList.PARAM_LDV_NO, this.ldvNo);
        d.setArguments(bundle);

        d.show(getSupportFragmentManager(), "dialog");

    }

    @Override
    public void onEditAddress() {
        Intent i = new Intent(this, ActivityProfile.class);
        i.putExtra("mode", ActivityProfile.MODE_EDIT_ADDRESS);
        startActivity(i);
    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOrderCreated(OrderHeader newOrder) {
        // force summaryorder to display latest order
        Fragment _fragment = pageAdapter.getItem(PAGE_ORDER_DETAIL);
        if (_fragment instanceof FragmentSummaryOrder) {
            ((FragmentSummaryOrder) _fragment).reInitiate(newOrder);
        }

        onGoToScreen(PAGE_ORDER_DETAIL, true, true);

        Util.showDialog(this, null, getString(R.string.message_order_created));

    }

    @Override
    public void onMitraSelected(TmpMitra mitra) {
        Log.e(TAG, "You select " + mitra.toString());
        Fragment fragment = pageAdapter.getItem(viewPager.getCurrentItem());

        if (fragment instanceof FragmentOrderAC) {
            ((FragmentOrderAC) fragment).etSelectMitra.setText(mitra.getName());
        }

    }
}
