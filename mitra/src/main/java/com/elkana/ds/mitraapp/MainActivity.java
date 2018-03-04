package com.elkana.ds.mitraapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.elkana.ds.mitraapp.screen.assign.FragmentTechnicianList;
import com.elkana.ds.mitraapp.screen.login.ActivityLogin;
import com.elkana.ds.mitraapp.screen.order.FragmentOrderList;
import com.elkana.ds.mitraapp.screen.profile.ActivityProfile;
import com.elkana.ds.mitraapp.screen.register.technician.ActivityRegisterTechnician;
import com.elkana.ds.mitraapp.util.DataUtil;
import com.elkana.dslibrary.activity.FirebaseActivity;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends FirebaseActivity
        implements NavigationView.OnNavigationItemSelectedListener
        ,FragmentOrderList.OnFragmentOrderListInteractionListener
        ,FragmentTechnicianList.OnFragmentTechnicianListInteractionListener{

    public static final String SELECTED_NAV_MENU_KEY = "selected_nav_menu_key";
    private static final String TAG = "MainActivity";
    private static final int REQUESTCODE_LOGIN = 33;

    private boolean viewIsAtHome;
    protected int mSelectedNavMenuIndex = 0;

    NavigationView navigationView;
    DrawerLayout drawer;
    MenuItem menuLogout, menuEditProfile;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        refreshToolbar();

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.title_orders, R.drawable.ic_filter_list_black_24dp, android.R.color.white);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.title_status, R.drawable.ic_panorama_fish_eye_black_24dp, android.R.color.white);
//        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.title_add_service, R.drawable.ic_add_black_24dp, android.R.color.white);

// Add items
        AHBottomNavigation bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);

//        bottomNavigation.setDefaultBackgroundColor(Color.parseColor(mobileSetup.getTheme_color_ac()));
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
//                int x = viewPager.getCurrentItem();
//
//                if (x != position)
//                    onGoToScreen(position, true, true);

                return true;
            }
        });

    }

    private void refreshToolbar() {
        if (menuLogout != null) {
            menuLogout.setVisible(mAuth.getCurrentUser() != null);
        }
        if (menuEditProfile != null)
            menuEditProfile.setVisible(mAuth.getCurrentUser() != null);

    }


    @Override
    protected void onLoggedOff() {
        refreshToolbar();

//        DataUtil.cleanTransactionData();

        startActivityForResult(new Intent(this, ActivityLogin.class), REQUESTCODE_LOGIN);
        finish();
    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {
        displayView(R.id.nav_order_new);

        refreshToolbar();

        View headerView = navigationView.getHeaderView(0);

        TextView tvProfileName = headerView.findViewById(R.id.tvProfileName);
        tvProfileName.setText(user.getDisplayName());
        TextView tvProfileEmail = headerView.findViewById(R.id.tvProfileEmail);
        tvProfileEmail.setText(user.getEmail());

        DataUtil.syncUserInformation();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
        }
        int x = getSupportFragmentManager().getBackStackEntryCount();

        if (!viewIsAtHome) {
            if (x > 0) {
                getSupportFragmentManager().popBackStackImmediate();
            } else
                displayView(R.id.nav_order_new);
        } else {
            //display logout dialog
//            logout();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        menuLogout = menu.findItem(R.id.action_logout);
        menuEditProfile = menu.findItem(R.id.action_editProfile);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        return super.onPrepareOptionsMenu(menu);
        refreshToolbar();

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
            return true;
        } else if (id == R.id.action_editProfile) {
            Intent i = new Intent(this, ActivityProfile.class);
            i.putExtra("mode", ActivityProfile.MODE_EDIT_ADDRESS);
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

        /*
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
        */

        if (id == R.id.nav_tech_new) {
            startActivity(new Intent(this, ActivityRegisterTechnician.class));
            return false;
        }

        displayView(id);
        return true;
    }

    protected void displayView(int viewId) {

        if (mSelectedNavMenuIndex == viewId) {
            return;
        }

        Fragment fragment = null;
        String title = null;
        viewIsAtHome = false;

        navigationView.setCheckedItem(viewId);

        title = getString(R.string.app_name);

        if (viewId == R.id.nav_order_new) {

            fragment = FragmentOrderList.newInstance(mAuth.getCurrentUser().getUid(), null);

            viewIsAtHome = true;

//            fab.show();
        } else if (viewId == R.id.nav_tech_list) {

            fragment = FragmentTechnicianList.newInstance(mAuth.getCurrentUser().getUid(), null);
        }

        /*else if (viewId == R.id.nav_loa) {
            fab.setImageDrawable(AppCompatDrawableManager.get().getDrawable(MainActivity.this, R.drawable.ic_sync_black_24dp));

            fragment = new FragmentLKPList();

            Bundle bundle = new Bundle();
            bundle.putString(FragmentLKPList.ARG_PARAM1, collCode);
            fragment.setArguments(bundle);

            title = "LKP List";
        } else if (viewId == R.id.nav_summaryLKP) {
            fragment = new FragmentSummaryLKP();

            Bundle bundle = new Bundle();
            bundle.putString(FragmentSummaryLKP.ARG_PARAM1, collCode);
            fragment.setArguments(bundle);

            title = "Summary LKP";
        } else if (viewId == R.id.nav_chats) {
            fab.setImageDrawable(AppCompatDrawableManager.get().getDrawable(MainActivity.this, R.drawable.ic_edit_black_24dp));

            fragment = new FragmentChatActiveContacts();

            Bundle bundle = new Bundle();
            bundle.putString(FragmentChatActiveContacts.PARAM_USERCODE, collCode);
            fragment.setArguments(bundle);

            title = "Chats";

        }*/

        if (viewId != R.id.nav_order_new) {
//            fab.hide();
        }

        mSelectedNavMenuIndex = viewId;

        if (fragment != null) {

            int x = getSupportFragmentManager().getBackStackEntryCount();

            if (x > 0)
                getSupportFragmentManager().popBackStackImmediate();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            ft.replace(R.id.content_frame, fragment);

//            if (viewId != R.id.nav_order_new)
//                ft.addToBackStack(title);

            ft.commit();
        }


        if (getSupportActionBar() != null) {
//            final String userFullName = Storage.getPref(Storage.KEY_USER_FULLNAME, null);
            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(userFullName);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        drawer.closeDrawer(GravityCompat.START);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUESTCODE_LOGIN)
            return;

        if (resultCode != RESULT_OK) {
            finish();
            return;
        }


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
