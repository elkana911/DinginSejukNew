package com.elkana.customer.screen.order;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import com.elkana.customer.R;
import com.elkana.customer.screen.AFirebaseCustomerActivity;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.pojo.Movement;
import com.elkana.dslibrary.util.Util;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityTechOtwMap extends AFirebaseCustomerActivity implements OnMapReadyCallback {

    private static final String TAG = ActivityTechOtwMap.class.getSimpleName();

    public static final String PARAM_ORDER_ID = "order.id";
    public static final String PARAM_TECH_NAME = "technician.name";

    protected static final float MAP_ZOOM_DEFAULT = 13;
    String mOrderId, mTechName;

    private DatabaseReference movementsRef;
    private GoogleMap mMap;
//    private ValueEventListener mChildEventListener;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_otw_map);

        mOrderId = getIntent().getStringExtra(PARAM_ORDER_ID);
        mTechName = getIntent().getStringExtra(PARAM_TECH_NAME);

        if (Util.TESTING_MODE && mOrderId == null) {
            mOrderId = "-L-srlSFRLdoqFLzRbHV";
            mTechName = "LIA";
        }

        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(userFullName);
//            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(mTechName + " Position");

            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(mobileSetup.getTheme_color_default())));
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

/*
        mChildEventListener = new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                GenericTypeIndicator<ArrayList<Movement>> t = new GenericTypeIndicator<ArrayList<Movement>>() {
                };

                List<Movement> movements = dataSnapshot.getValue(t);


                String time = dataSnapshot.getKey();
                Movement _obj = dataSnapshot.getValue(Movement.class);

                if (_obj == null || _obj.getLatitude() == null )
                    return;

                double lat = Double.parseDouble(_obj.getLatitude());
                double lng = Double.parseDouble(_obj.getLongitude());
                LatLng latLng = new LatLng(lat, lng);

                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng, MAP_ZOOM_DEFAULT));
                mMap.moveCamera(cameraUpdate);

                Date date = new Date(Long.parseLong(time));
//                https://stackoverflow.com/questions/42365658/custom-marker-in-google-maps-in-android-with-vector-asset-icon
                //marker using vector
                // Inside The Circle
                MarkerOptions title = new MarkerOptions().position(new LatLng(lat, lng)).title(Util.convertDateToString(date, "HH:mm:ss"))
                        .icon(bitmapDescriptorFromVector(ActivityTechOtwMap.this, R.drawable.ic_motorcycle_black_24dp));
                Marker marker = mMap.addMarker(title);

//                if (mobileSetup.isMap_show_vendor_title())
//                    marker.showInfoWindow();

                String time = dataSnapshot.getKey();
                Movement _obj = dataSnapshot.getValue(Movement.class);

                if (_obj == null || _obj.getLatitude() == null )
                    return;

                double lat = Double.parseDouble(_obj.getLatitude());
                double lng = Double.parseDouble(_obj.getLongitude());
                LatLng latLng = new LatLng(lat, lng);

                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng, MAP_ZOOM_DEFAULT));
                mMap.moveCamera(cameraUpdate);

                Date date = new Date(Long.parseLong(time));
//                https://stackoverflow.com/questions/42365658/custom-marker-in-google-maps-in-android-with-vector-asset-icon
                //marker using vector
                // Inside The Circle
                MarkerOptions title = new MarkerOptions().position(new LatLng(lat, lng)).title(Util.convertDateToString(date, "HH:mm:ss"))
                        .icon(bitmapDescriptorFromVector(ActivityTechOtwMap.this, R.drawable.ic_motorcycle_black_24dp));
                Marker marker = mMap.addMarker(title);

//                if (mobileSetup.isMap_show_vendor_title())
//                    marker.showInfoWindow();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        */
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String time = dataSnapshot.getKey();
                Movement _obj = dataSnapshot.getValue(Movement.class);

                if (_obj == null || _obj.getLatitude() == null)
                    return;

                double lat = Double.parseDouble(_obj.getLatitude());
                double lng = Double.parseDouble(_obj.getLongitude());
                LatLng latLng = new LatLng(lat, lng);

                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng, MAP_ZOOM_DEFAULT));
                mMap.moveCamera(cameraUpdate);

                Date date = new Date(Long.parseLong(time));
//                https://stackoverflow.com/questions/42365658/custom-marker-in-google-maps-in-android-with-vector-asset-icon
                //marker using vector
                // Inside The Circle
                MarkerOptions title = new MarkerOptions().position(new LatLng(lat, lng)).title(Util.convertDateToString(date, "HH:mm:ss"))
                        .icon(bitmapDescriptorFromVector(ActivityTechOtwMap.this, R.drawable.ic_motorcycle_black_24dp));
                Marker marker = mMap.addMarker(title);

//                if (mobileSetup.isMap_show_vendor_title())
//                    marker.showInfoWindow();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        movementsRef = mDatabase.getReference(FBUtil.REF_MOVEMENTS)
                .child(mOrderId);

//        movementsRef.addValueEventListener(mChildEventListener);
        movementsRef.addChildEventListener(mChildEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        movementsRef.removeEventListener(mChildEventListener);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (mMap == null)
            return;


//        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


//        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}
