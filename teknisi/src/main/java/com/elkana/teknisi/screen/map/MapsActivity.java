package com.elkana.teknisi.screen.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerGetLong;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.DateUtil;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.EOrderStatus;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.AFirebaseTeknisiActivity;
import com.elkana.teknisi.R;
import com.elkana.teknisi.pojo.MobileSetup;
import com.elkana.teknisi.screen.MainActivity;
import com.elkana.teknisi.util.TeknisiUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

public class MapsActivity extends AFirebaseTeknisiActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();

    public static final String PARAM_TECHNICIAN_ID = "tech.id";
    public static final String PARAM_ASSIGNMENT_ID = "assignment.id";
    public static final String PARAM_ORDER_ID = "order.id";
    public static final String PARAM_CUSTOMER_ID = "customer.id";
    public static final String PARAM_LATITUDE_ID = "customer.latitude";
    public static final String PARAM_LONGITUDE_ID = "customer.longitude";
    public static final String PARAM_ADDRESS_ID = "customer.address";
    public static final String PARAM_MITRA_ID = "mitra.id";
    public static final String PARAM_SERVICE_TYPE = "order.serviceType";

    // The minimum distance to change Updates in meters
    private static final long MIN_UPDATE_DISTANCE = 100; // 100 meters utk pergerakan driver
    // The minimum time between updates in milliseconds
    private static final long MIN_UPDATE_TIME = 1000 * 12;  // 12 seconds
    private static final float MAP_ZOOM_DEFAULT = 13;
    private String mTechnicianId, mAssignmentId, mOrderId, mCustomerId, mCustomerAddress, mCustomerPhone, mLatitude, mLongitude, mMitraId;
    private int mServiceType;

    private OrderHeader orderInfo;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationUpdateListener listener;
    private Location lastLoc;
    private CameraUpdate cameraCurrentPos, cameraAddress;

    protected MobileSetup mobileSetup = null;

    private ValueEventListener alwaysListenOrderListener;
    private DatabaseReference alwaysListenOrderRef;

    private TextView tvAddress, tvACCount, tvOrderId, tvProblem, tvMitra, tvDateOfService, tvCustomerName, tvDateCancel;
    private Button btnStartWorking, btnStartOtw;
    private BottomSheetBehavior mBottomSheetBehavior1;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mobileSetup = TeknisiUtil.getMobileSetup();

        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(userFullName);
//            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle(getString(R.string.title_activity_maps));
        }

        tvACCount = findViewById(R.id.tvACCount);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvDateOfService = findViewById(R.id.tvDateOfService);
        tvMitra = findViewById(R.id.tvMitra);
        tvProblem = findViewById(R.id.tvProblem);
        tvDateCancel = findViewById(R.id.tvDateCancel);

        final CardView bottomSheetCardView = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheetCardView);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    fab.setImageDrawable(AppCompatDrawableManager.get().getDrawable(MapsActivity.this, R.drawable.ic_keyboard_arrow_up_black_24dp));
                } else if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                    fab.setImageDrawable(AppCompatDrawableManager.get().getDrawable(MapsActivity.this, R.drawable.ic_keyboard_arrow_down_black_24dp));
                } else if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    fab.setImageDrawable(AppCompatDrawableManager.get().getDrawable(MapsActivity.this, R.drawable.ic_keyboard_arrow_up_black_24dp));
                }
            }
        });

        final FloatingActionButton fabPhone = findViewById(R.id.fabPhone);
        fabPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCustomerPhone == null)
                    return;

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + mCustomerPhone));

                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);
            }
        });
        final FloatingActionButton fabSMS = findViewById(R.id.fabSMS);
        fabSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomerPhone == null)
                    return;

                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromParts("sms", mCustomerPhone, null);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        mTechnicianId = getIntent().getStringExtra(PARAM_TECHNICIAN_ID);
        mAssignmentId = getIntent().getStringExtra(PARAM_ASSIGNMENT_ID);
        mLatitude = getIntent().getStringExtra(PARAM_LATITUDE_ID);
        mLongitude = getIntent().getStringExtra(PARAM_LONGITUDE_ID);
        mCustomerId = getIntent().getStringExtra(PARAM_CUSTOMER_ID);
        mCustomerAddress = getIntent().getStringExtra(PARAM_ADDRESS_ID);
        mOrderId = getIntent().getStringExtra(PARAM_ORDER_ID);
        mMitraId = getIntent().getStringExtra(PARAM_MITRA_ID);
        mServiceType = getIntent().getIntExtra(PARAM_SERVICE_TYPE, Const.SERVICE_TYPE_SCHEDULED);

        if (Util.TESTING_MODE && mTechnicianId == null) {
            mCustomerId = "2Chlu5e44Ig95SkjQxVgGbVvysk2";
            mTechnicianId = "YFh65qe1BSPMJyhS8KkIrtPUYR32";
            mAssignmentId = "assignmentIdAbc123";
            mLatitude = "-6.275038065354676";
            mLongitude = "106.65709599852562";
            mCustomerAddress = "gii bsg isuzu";
            mOrderId = "-Kyhkq-AC9RrqNQAi4vb";
            mMitraId = "3";
        }

        TextView tvVerticalDots = findViewById(R.id.tvVerticalDots);
        tvVerticalDots.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_more_vert_black_24dp, android.R.color.darker_gray), null, null, null);

        TextView tvCurrentPosition = findViewById(R.id.tvCurrentPosition);
        tvCurrentPosition.setCompoundDrawablePadding(10);
        tvCurrentPosition.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_my_location_black_24dp, android.R.color.holo_blue_dark), null, null, null);
        tvCurrentPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null && cameraCurrentPos != null) {
                    mMap.animateCamera(cameraCurrentPos);
                }
            }
        });

        tvAddress = findViewById(R.id.tvAddress);
        tvAddress.setText(mCustomerAddress);
        tvAddress.setCompoundDrawablePadding(10);
        tvAddress.setCompoundDrawablesWithIntrinsicBounds(Util.changeIconColor(this, R.drawable.ic_place_black_24dp, android.R.color.holo_red_dark), null, null, null);
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null && cameraAddress != null) {
                    mMap.animateCamera(cameraAddress);
                }
            }
        });

        btnStartOtw = findViewById(R.id.btnStartOtw);
        btnStartOtw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (orderInfo == null) {
                    return;
                }

                final AlertDialog alertDialog = Util.showProgressDialog(MapsActivity.this);
                FBUtil.getTimestamp(new ListenerGetLong() {
                    @Override
                    public void onSuccess(long now) {
                        alertDialog.dismiss();

                        int minMinutesOtw = mobileSetup.getMin_minutes_otw();
                        long minMillisOtw = minMinutesOtw * DateUtil.TIME_ONE_MINUTE_MILLIS;

                        long oneHourBeforeOtw = orderInfo.getBookingTimestamp() - minMillisOtw;

                        if (oneHourBeforeOtw > now) {

                            Toast.makeText(MapsActivity.this, "Belum saatnya berangkat. Minimal " + minMinutesOtw + " Menit dari jam Layanan"
                                    , Toast.LENGTH_LONG).show();
                        } else {
                            startOtw();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        btnStartWorking = findViewById(R.id.btnStartWorking);
        btnStartWorking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showDialogConfirmation(MapsActivity.this, getString(R.string.title_start_working), getString(R.string.confirm_start_working), new ListenerPositiveConfirmation() {
                    @Override
                    public void onPositive() {

                        //turn off tracking
                        Realm r = Realm.getDefaultInstance();
                        try{
                            MobileSetup setup = r.where(MobileSetup.class).findFirst();

                            r.beginTransaction();
                            setup.setTrackingGps(false);

                            r.copyToRealmOrUpdate(setup);
                            r.commitTransaction();
                        }finally {
                            r.close();
                        }

                        final AlertDialog _dialog =  Util.showProgressDialog(MapsActivity.this);

                        Assignment_setStatus(mMitraId, mTechnicianId, mAssignmentId, mCustomerId, mOrderId, EOrderDetailStatus.WORKING, new ListenerModifyData() {
                                @Override
                            public void onSuccess() {
                                _dialog.dismiss();
                                long time = new Date().getTime();

                                Map<String, Object> keyValAssignment = new HashMap<>();
                                keyValAssignment.put("startDate", time);
                                keyValAssignment.put("updatedTimestamp", time);

                                final DatabaseReference _assignmentRef = FBUtil.Assignment_getPendingRef(mTechnicianId, mAssignmentId);
                                _assignmentRef.child("assign").updateChildren(keyValAssignment, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                        Intent data = new Intent();
                                        data.putExtra("statusDetailId", EOrderDetailStatus.WORKING.name());
                                        data.putExtra(PARAM_ASSIGNMENT_ID, mAssignmentId);
                                        data.putExtra(PARAM_CUSTOMER_ID, mCustomerId);
                                        data.putExtra(PARAM_ORDER_ID, mOrderId);
                                        data.putExtra(PARAM_TECHNICIAN_ID, mTechnicianId);
                                        data.putExtra(PARAM_MITRA_ID, mMitraId);
                                        data.putExtra(PARAM_SERVICE_TYPE, mServiceType);
                                        setResult(RESULT_OK, data);
                                        finish();
                                    }
                                });

                            }

                            @Override
                            public void onError(Exception e) {
                                _dialog.dismiss();
                                Log.e(TAG, e.getMessage());
                            }
                        });

                    }
                });
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void startOtw() {
        Util.showDialogConfirmation(MapsActivity.this, "Mulai Perjalanan ?", getString(R.string.confirm_start_otw), new ListenerPositiveConfirmation() {
            @Override
            public void onPositive() {

                final AlertDialog _dialog = Util.showProgressDialog(MapsActivity.this);

                //1. cek dulu apa user cancel order ?
                final DatabaseReference orderRef = FBUtil.Order_getPendingCustomerRef(mCustomerId, mOrderId);

                orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            _dialog.dismiss();
                            return;
                        }

                        OrderHeader obj = dataSnapshot.getValue(OrderHeader.class);

                        if (obj.getStatusId().equals(EOrderStatus.FINISHED.name())) {
                            _dialog.dismiss();
                            Util.showDialog(MapsActivity.this, "Status Berubah", "Maaf, Pelanggan mungkin telah membatalkan layanan.");
                            return;
                        }

                        //2. update status
//                                also update orderbucket,FIELD APANYA YG DIUPDATE KAMPRETTTTTT !! BIKIN SUSAH GW DI MASA DEPAN SAJA !!!!!!!!!!!!!!!!!!!!!!
                        Assignment_setStatus(mMitraId, mTechnicianId, mAssignmentId, mCustomerId, mOrderId, EOrderDetailStatus.OTW, new ListenerModifyData() {
                            @Override
                            public void onSuccess() {
                                btnStartOtw.setVisibility(View.GONE);
                                btnStartWorking.setVisibility(View.VISIBLE);
                                _dialog.dismiss();

                                //turn on tracking
                                Realm r = Realm.getDefaultInstance();
                                try{
                                    MobileSetup setup = r.where(MobileSetup.class).findFirst();

                                    r.beginTransaction();
                                    setup.setTrackingGps(true);
                                    setup.setTrackingOrderId(mOrderId);

                                    r.copyToRealmOrUpdate(setup);
                                    r.commitTransaction();

                                }finally {
                                    r.close();
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                _dialog.dismiss();
                                Util.showDialog(MapsActivity.this, "Error", "Silakan coba lagi");
                                Log.e(TAG, e.getMessage());

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        _dialog.dismiss();
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                    }
                });

            }
        });

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

        if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);

            if (Util.TESTING_MODE)
                mMap.getUiSettings().setZoomControlsEnabled(true);
        }

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            listener = new LocationUpdateListener();

            // get current location before gps updates
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false));
            if (location != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(MAP_ZOOM_DEFAULT)                   // Sets the zoom
//                        .bearing(90)                // Sets the orientation of the camera to east
//                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder

                cameraCurrentPos = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.animateCamera(cameraCurrentPos);
            }

        }

//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, listener);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, listener);

        mMap.setPadding(16, 8, 8, 160);

        // Add a marker in Sydney and move the camera
        double lat = Double.parseDouble(mLatitude);
        double lng = Double.parseDouble(mLongitude);
        LatLng targetLocation = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(targetLocation).title(mCustomerAddress));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(targetLocation, MAP_ZOOM_DEFAULT));
        cameraAddress = cameraUpdate;

        mMap.moveCamera(cameraAddress);


    }


    class LocationUpdateListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
//            String latitude = String.valueOf(location.getLatitude());
//            String longitude= String.valueOf(location.getLongitude());
            boolean update = false;

            if (lastLoc == null) {
                update = true;
            } else if (lastLoc != null && lastLoc.getProvider().equals(location.getProvider())) {
                update = true;
            } else if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                update = true;
            }

            if (!update)
                return;

            lastLoc = location;

//            log("onLocationChanged.lat=" + location.getLatitude() + ", long="+location.getLongitude());

            if (mMap == null)
                return;
            // update your marker here
            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(sydney));
            cameraCurrentPos = CameraUpdateFactory.newLatLng(sydney);
            mMap.moveCamera(cameraCurrentPos);

//            updatePositionToServer(sydney);

        }

        @Override
        public void onProviderDisabled(String provider) {
            // dipanggil kalo user ubah location switch
//            log("onProviderDisabled");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // dipanggil kalo user ubah location switch
//            log("onProviderEnabled");

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // paling sering dipanggil meski koordinat tdk berubah
//            log("onStatusChanged, provider=" + provider + ",status=" + status);

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        final AlertDialog dialog = Util.showProgressDialog(this);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialog.dismiss();

                if (!dataSnapshot.exists())
                    return;

                orderInfo = dataSnapshot.getValue(OrderHeader.class);
//                OrderHeader obj = dataSnapshot.getValue(OrderHeader.class);
                Log.e(TAG, "obj:" + orderInfo.toString());

                tvACCount.setText("Jml AC: " + String.valueOf(orderInfo.getJumlahAC()));
                tvOrderId.setText("Order Id: " + orderInfo.getUid());
                tvCustomerName.setText(orderInfo.getCustomerName());
                tvDateOfService.setText("Tgl Service: " + DateUtil.displayTimeInJakarta(orderInfo.getBookingTimestamp(), "dd-MMM-yyyy HH:mm"));
//                tvDateOfService.setText("Tgl Service: " + Util.convertDateToString(new Date(obj.getTimestamp()), "dd-MMM-yyyy HH:mm"));
                tvMitra.setText("Mitra: " + orderInfo.getPartyName());
                tvProblem.setText("Keterangan: " + orderInfo.getProblem());

                btnStartOtw.setEnabled(true);
                btnStartWorking.setEnabled(true);

                switch (EOrderDetailStatus.convertValue(orderInfo.getStatusDetailId())) {
                    case ASSIGNED:
                        btnStartOtw.setVisibility(View.VISIBLE);
                        btnStartWorking.setVisibility(View.GONE);
                        break;
                    case OTW:
                        btnStartOtw.setVisibility(View.GONE);
                        btnStartWorking.setVisibility(View.VISIBLE);
                        break;
                    case CANCELLED_BY_CUSTOMER:
                    case CANCELLED_BY_TIMEOUT:
                    case CANCELLED_BY_SERVER:
                        tvDateCancel.setText("Jam Cancel: "+ DateUtil.formatDateToSimple(orderInfo.getUpdatedTimestamp()));
                        tvDateCancel.setVisibility(View.VISIBLE);
                        btnStartOtw.setText(orderInfo.getStatusDetailId());
                        btnStartOtw.setEnabled(false);
                        btnStartWorking.setEnabled(false);
                        break;
                    default:
                        // bahaya finish disini, krn bisa saja ada proses lain blm selesai
//                        finish();
                }

                mCustomerPhone = orderInfo.getPhone();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();
            }
        };

        alwaysListenOrderListener = valueEventListener;

        // standalone monitor order
        alwaysListenOrderRef = FBUtil.Order_getPendingCustomerRef(mCustomerId, mOrderId);
        alwaysListenOrderRef.addValueEventListener(alwaysListenOrderListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (alwaysListenOrderRef != null && alwaysListenOrderListener != null) {
            alwaysListenOrderRef.removeEventListener(alwaysListenOrderListener);
        }
    }
}
