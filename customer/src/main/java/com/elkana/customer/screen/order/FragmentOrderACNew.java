package com.elkana.customer.screen.order;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.elkana.customer.R;
import com.elkana.customer.exception.OrderAlreadyExists;
import com.elkana.customer.pojo.MobileSetup;
import com.elkana.customer.screen.register.SimpleAdapterUserAddress;
import com.elkana.customer.util.CustomerUtil;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.pojo.OrderBucket;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.elkana.dslibrary.pojo.mitra.Mitra;
import com.elkana.dslibrary.pojo.mitra.TmpMitra;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.pojo.user.UserAddress;
import com.elkana.dslibrary.util.Const;
import com.elkana.dslibrary.util.DateUtil;
import com.elkana.dslibrary.util.EOrderDetailStatus;
import com.elkana.dslibrary.util.EOrderStatus;
import com.elkana.dslibrary.util.Util;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Fragment penyederhanaan dari Quick & Schedule. Tidak perlu lagi ditanya mau pilih layanan yg mana.
 * Jadi langsung aja liat peta mitra terdekat dan submit order
 * <p>
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentOrderACInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentOrderACNew#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentOrderACNew extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "userId";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "FragmentOrderAC";

    private final String DATE_DISPLAY_PATTERN = "dd-MMM-yyyy";

    protected static final float MAP_ZOOM_DEFAULT = 13;

    private String mUserId;
    private String mParam2;

    protected List<TmpMitra> mitraInRange = new ArrayList<>();
    protected FirebaseDatabase database;
    protected GoogleMap mMap;
    protected MobileSetup mobileSetup = null;

    protected String kapanYYYYMMDD;
    protected int radiusVendorKM;

    protected SimpleAdapterUserAddress adapterUserAddress;

    private OnFragmentOrderACInteractionListener mListener;

    MaterialSpinner spAddress;

    EditText etDate, etTime, etProblem, etCounter;

    public EditText etSelectMitra;

    TextView tvExtraCharge;

    public FragmentOrderACNew() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentOrderAC.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentOrderACNew newInstance(String param1, String param2) {
        FragmentOrderACNew fragment = new FragmentOrderACNew();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        database = FirebaseDatabase.getInstance();

        mobileSetup = CustomerUtil.getMobileSetup();

        radiusVendorKM = mobileSetup.getVendor_radius_km();

        adapterUserAddress = new SimpleAdapterUserAddress(getContext(), android.R.layout.simple_spinner_item, mUserId);
        adapterUserAddress.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    protected void setupMap() {
        if (mMap == null)
            return;

        if (Util.DEVELOPER_MODE) {
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }

        mMap.clear();
//        mitraInRange.clear();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                //kupakai tag utk menyimpan mitra.UID
                if (marker.getTag() == null)
                    return false;

                Toast.makeText(getContext(), getString(R.string.warning_you_pick_mitra, marker.getTitle()), Toast.LENGTH_SHORT).show();
//                etSelectMitra.setText(CustomerUtil.lookUpMitraById((String)marker.getTag()).getName());

                return false;
            }
        });
    }


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_order_ac_new, container, false);

        etDate = v.findViewById(R.id.etDate);
        etTime = v.findViewById(R.id.etTime);
        tvExtraCharge = v.findViewById(R.id.tvExtraCharge);
        etProblem = v.findViewById(R.id.etServiceProblem);
        etCounter = v.findViewById(R.id.etCounter);
        etSelectMitra = v.findViewById(R.id.etSelectMitra);
        etSelectMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectMitra();
            }
        });
        spAddress = v.findViewById(R.id.spAddress);

        FloatingActionButton fabEditAddress = v.findViewById(R.id.fabEditAddress);
        fabEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onEditAddress();
            }
        });

        ImageView ivDropDown = v.findViewById(R.id.ivDropDown);
        ivDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectMitra();
            }
        });

        ImageView ivDecItem = v.findViewById(R.id.ivDecItem);
        ivDecItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCounter.setText(Util.counter(etCounter.getText().toString(), false, 1, mobileSetup.getUnit_ac_max()));

            }
        });
        ImageView ivIncItem = v.findViewById(R.id.ivIncItem);
        ivIncItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etCounter.setText(Util.counter(etCounter.getText().toString(), true, 1, mobileSetup.getUnit_ac_max()));
            }
        });

        ImageButton btnProblemTemplate = v.findViewById(R.id.btnProblemTemplate);
        btnProblemTemplate.setImageResource(R.drawable.ic_reorder_black_24dp);
        btnProblemTemplate.setColorFilter(Color.parseColor("#FFFFFF"));

        btnProblemTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Unhandled ProblemTemplate", Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton btnSubmitOrder = v.findViewById(R.id.btnSubmitOrder);
        btnSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySubmitOrder();
            }
        });

        final LinearLayout bottomSheetCardView = v.findViewById(R.id.bottom_sheet);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            bottomSheetCardView.getCardBackgroundColor().setAlpha(230);
        } else {
//            bottomSheetCardView.setCardBackgroundColor(Color.parseColor("#ffffffff"));
        }

        BottomSheetBehavior mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheetCardView);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
/*
        final FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    fab.setImageDrawable(AppCompatDrawableManager.get().getDrawable(getContext(), R.drawable.ic_expand_less_black_24dp));
                } else if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                    fab.setImageDrawable(AppCompatDrawableManager.get().getDrawable(getContext(), R.drawable.ic_expand_more_black_24dp));
                } else if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    fab.setImageDrawable(AppCompatDrawableManager.get().getDrawable(getContext(), R.drawable.ic_expand_less_black_24dp));
                }
            }
        });*/

        if (mMap == null) {
            SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;

                    setupMap();
//                    setupMapBasedOnCurrentPosition();
                }
            });
        }

        etDate.setCompoundDrawablesWithIntrinsicBounds(null, null, Util.changeIconColor(getContext(), R.drawable.ic_date_range_black_24dp, R.color.cardTextColor), null);
        etTime.setCompoundDrawablesWithIntrinsicBounds(null, null, Util.changeIconColor(getContext(), R.drawable.ic_access_time_black_24dp, R.color.cardTextColor), null);

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                final int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                etDate.setText(Util.prettyDate(getContext(), dayOfMonth, monthOfYear, year, false));

                                Calendar c = Calendar.getInstance();
                                c.set(year, monthOfYear, dayOfMonth);
                                kapanYYYYMMDD = Util.convertDateToString(c.getTime(), "yyyyMMdd");

                                tvExtraCharge.setVisibility(DateUtil.isToday(c.getTime()) ? View.VISIBLE : View.INVISIBLE);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// Get Current Time
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                etTime.setText(String.format("%02d:%02d", hourOfDay, minute));
//                                etTime.setText(hourOfDay + ":" + minute);

                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

//        spServiceCount.setAdapter(adapterACCount);

        spAddress.setAdapter(adapterUserAddress);
        spAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Object obj = adapterView.getItemAtPosition(i);

                if (obj instanceof String) {
                    String addr = (String) adapterView.getItemAtPosition(i);

                } else if (obj instanceof UserAddress) {

                    UserAddress ua = (UserAddress) adapterView.getItemAtPosition(i);

                    // skip for now
                    if (true)
                        return;

                    if (mMap != null & ua.getAddress() != null) {

                        MobileSetup mobileSetup = CustomerUtil.getMobileSetup();

                        double latitude = Double.parseDouble(ua.getLatitude());
                        double longitude = Double.parseDouble(ua.getLongitude());
                        LatLng latLng = new LatLng(latitude, longitude);

                        mMap.clear();
                        MarkerOptions a = new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .title(ua.getLabel())
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                        Marker m = mMap.addMarker(a);
                        m.showInfoWindow();

                        if (Util.TESTING_MODE)
                            mMap.addCircle(new CircleOptions().center(latLng)
                                    .radius(radiusVendorKM * 1000).strokeWidth(0f)
                                    .fillColor(ContextCompat.getColor(getContext(), R.color.colorMapRadius)));

                        m.setPosition(new LatLng(latitude, longitude));

                        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng, MAP_ZOOM_DEFAULT));
                        mMap.moveCamera(cameraUpdate);

                        // add markers based on mitra
                        Realm r = Realm.getDefaultInstance();
                        try {
                            RealmResults<Mitra> all = r.where(Mitra.class).equalTo("visible", true).findAll();

                            mitraInRange.clear();
                            for (Mitra mitra : all) {
                                if (mitra.getLatitude() == null)
                                    continue;

                                double lat = Double.parseDouble(mitra.getLatitude());
                                double lng = Double.parseDouble(mitra.getLongitude());

                                // cek distance first
                                float[] distance = new float[2];
                                Location.distanceBetween(latLng.latitude, latLng.longitude, lat, lng, distance);

                                if (distance[0] <= radiusVendorKM * 1000) {
                                    // Inside The Circle
                                    MarkerOptions title = new MarkerOptions().position(new LatLng(lat, lng)).title(mitra.getName());
                                    Marker marker = mMap.addMarker(title);
                                    marker.setTag(mitra.getUid());

                                    if (mobileSetup.isMap_show_vendor_title())
                                        marker.showInfoWindow();

                                    // create new mitra list based on radius
                                    TmpMitra tmpMitra = CustomerUtil.cloneMitra(mitra);
                                    mitraInRange.add(tmpMitra);
                                } else {
                                    // Outside The Circle
                                }

                            }


                        } finally {
                            r.close();
                        }
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (Util.DEVELOPER_MODE && Util.TESTING_MODE) {
            if (mUserId == null) {
                mUserId = Util.TESTING_USER;
            }

            Log.i(TAG, "mUserId is null ! Developer bypass using " + mUserId);

            // auto fill
            etProblem.setText("Kurang dingin");
            spAddress.setSelection(0);
//            spServiceCount.setSelection(2);
//            etDate.setText("Hari Ini");   karena ada conversion
            etTime.setText(Util.convertDateToString(new Date(), "HH:mm"));
        }

        // auto select
        selectAddressDefault();
        selectMitraDefault();

        Date nextWorkingDay = CustomerUtil.getWorkingDay(new Date(), 2);
        etDate.setText(Util.prettyDate(getContext(),  nextWorkingDay, true));
        kapanYYYYMMDD = Util.convertDateToString(nextWorkingDay, "yyyyMMdd");

//        etTime.setText(CustomerUtil.getNextWorkingHour(2));

        tvExtraCharge.setText(getString(R.string.warning_extracharge1));
        tvExtraCharge.setVisibility(DateUtil.isToday(nextWorkingDay) ? View.VISIBLE : View.INVISIBLE);

        return v;
    }

    private void selectAddressDefault() {
        int setDefaultAddress = -1;

        if (adapterUserAddress.getCount() == 1) {
            spAddress.setSelection(1);
            return;
        }

        for (int i = 0; i < adapterUserAddress.getCount(); i++) {

            UserAddress item = adapterUserAddress.getItem(i);

            if (item.isDefaultAddress()) {
                setDefaultAddress = i;
                break;
            }
        }

        if (setDefaultAddress > -1) {
            spAddress.setSelection(setDefaultAddress+1);
        }

    }

    private Mitra selectMitraDefault(){
        Realm realm = Realm.getDefaultInstance();
        try {
            Mitra mitraObj = realm.where(Mitra.class)
                    .findFirst();

            if (mitraObj != null) {
                etSelectMitra.setText(mitraObj.getName());

                return realm.copyFromRealm(mitraObj);
            }

        } finally {
            realm.close();
        }

        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentOrderACInteractionListener) {
            mListener = (OnFragmentOrderACInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnFragmentOrderACInteractionListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    protected void onSelectMitra() {
        spAddress.setError(null);

        // address must picked first
        Object addressObj = spAddress.getSelectedItem();
        String alamat = "";
        if (addressObj != null) {
            if (addressObj instanceof UserAddress) {
                alamat = ((UserAddress) addressObj).getLabel();
            } else {
                alamat = addressObj.toString();
            }

        }

        if (TextUtils.isEmpty(alamat)) {
            spAddress.setError(getString(R.string.error_field_required));
            spAddress.requestFocus();
            return;
        }

        if (mListener != null) {
            mListener.onClickSelectMitra(mitraInRange);
        }

    }

    public void reInitiate(String userId, String param2) {
        this.mUserId = userId;

        if (adapterUserAddress != null)
            adapterUserAddress.cleanUpListener();

        adapterUserAddress = new SimpleAdapterUserAddress(getContext(), android.R.layout.simple_spinner_item, mUserId);
        spAddress.setAdapter(adapterUserAddress);

        selectAddressDefault();
    }

    private void submitOrder(final OrderHeader orderHeader){
        final AlertDialog dialog = Util.showProgressDialog(getContext(), "Booking process...");

        DatabaseReference orderPendingCustomerRef = database.getReference(CustomerUtil.REF_ORDERS_CUSTOMER_AC_PENDING)
                .child(mUserId).push();

        final String orderKey = orderPendingCustomerRef.getKey();
        // set uid
        orderHeader.setUid(orderKey);

        final OrderBucket orderBucket = new OrderBucket();
        orderBucket.setUid(orderKey);
        orderBucket.setAcCount(orderHeader.getJumlahAC());
        orderBucket.setCustomerId(orderHeader.getCustomerId());
        orderBucket.setCustomerName(orderHeader.getCustomerName());
        orderBucket.setAddressByGoogle(orderHeader.getAddressByGoogle());
        orderBucket.setStatusDetailId(orderHeader.getStatusDetailId()); // duh males lg nambah node yg ini
        orderBucket.setPartyId(orderHeader.getPartyId());
        orderBucket.setTechnicianId(orderHeader.getTechnicianId());
//        orderBucket.setTechnicianName(orderHeader.getTechnicianId());
        orderBucket.setOrderTimestamp(orderHeader.getTimestamp());
        orderBucket.setUpdatedTimestamp(new Date().getTime());
        orderBucket.setUpdatedBy(String.valueOf(Const.USER_AS_COSTUMER));


        try {

            String root = FBUtil.REF_ORDERS_CUSTOMER_AC_PENDING + "/" + orderHeader.getCustomerId() + "/" + orderKey;
            Map<String, Object> keyValOrderHeader = FBUtil.convertObjectToKeyVal(root, orderHeader);
            keyValOrderHeader.put(root + "/createdTimestamp", ServerValue.TIMESTAMP);

            Map<String, Object> keyValOrderBucket = FBUtil.convertObjectToKeyVal(FBUtil.REF_ORDERS_MITRA_AC_PENDING + "/" + orderHeader.getPartyId() + "/" + orderKey, orderBucket);

            keyValOrderHeader.putAll(keyValOrderBucket);

            FirebaseDatabase.getInstance().getReference().updateChildren(keyValOrderHeader).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dialog.dismiss();

                    if (task.isSuccessful()) {

                        Realm realm = Realm.getDefaultInstance();
                        try {
                            // flushed
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(orderHeader);
                                    realm.copyToRealmOrUpdate(orderBucket);
                                }
                            });

                            if (mListener != null) {
                                mListener.onOrderCreated(orderHeader);
                            }

                        } finally {
                            realm.close();
                        }
                    } else {
                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void trySubmitOrder() {

        boolean cancel = false;
        View focus = null;

        etDate.setError(null);
        etTime.setError(null);
        etProblem.setError(null);
        spAddress.setError(null);
        etSelectMitra.setError(null);

        final String kapan = etDate.getText().toString().trim();
        final String jam = etTime.getText().toString().trim();
        final String problem = etProblem.getText().toString().trim();

        Object addressObj = spAddress.getSelectedItem();
        String alamat = "";
        String alamatByGoogle = "";
        String latitude = "";
        String longitude = "";
        if (addressObj != null) {
            if (addressObj instanceof UserAddress) {
                alamat = ((UserAddress) addressObj).getLabel();
                alamatByGoogle = ((UserAddress) addressObj).getAddress();
                latitude = ((UserAddress) addressObj).getLatitude();
                longitude = ((UserAddress) addressObj).getLongitude();
            } else {
                alamat = addressObj.toString();
                alamatByGoogle = alamat;
            }

        }
        final String mitra = etSelectMitra.getText().toString().trim();

        if (TextUtils.isEmpty(kapan)) {
            etDate.setError(getString(R.string.error_field_required));
            focus = etDate;
            cancel = true;
        }

        if (TextUtils.isEmpty(jam)) {
            etTime.setError(getString(R.string.error_field_required));
            focus = etTime;
            cancel = true;
        }

        if (TextUtils.isEmpty(problem)) {
            etProblem.setError(getString(R.string.warning_field_required));
        }

        if (TextUtils.isEmpty(alamat)) {
            spAddress.setError(getString(R.string.error_field_required));
            focus = spAddress;
            cancel = true;
        }

        if (TextUtils.isEmpty(mitra)) {
            Toast.makeText(getContext(), "Mitra not defined", Toast.LENGTH_SHORT).show();
            etSelectMitra.setError(getString(R.string.error_field_required));
            focus = etSelectMitra;
            cancel = true;
        }

        if (cancel) {
            focus.requestFocus();
            return;
        }

        final String finalAlamat = alamat;
        final String finalAlamatByGoogle = alamatByGoogle;
        final String finalLatitude = latitude;
        final String finalLongitude = longitude;
        Util.showDialogConfirmation(getActivity(), "Booking Confirmation", "Booking Layanan sekarang ?", new ListenerPositiveConfirmation() {
            @Override
            public void onPositive() {
                final OrderHeader orderHeader = new OrderHeader();
                orderHeader.setCustomerId(mUserId);//jd tdk mungkin user yg sama bisa order 2x
                orderHeader.setDateOfService(kapanYYYYMMDD);
                orderHeader.setTimeOfService(jam);
                orderHeader.setTimestamp(Util.convertStringToDate(kapanYYYYMMDD + jam, "yyyyMMddHH:mm").getTime());
                orderHeader.setServiceType(DateUtil.isToday(orderHeader.getTimestamp()) ? 1 : 2);
                orderHeader.setInvoiceNo(String.valueOf(orderHeader.getTimestamp()));
                orderHeader.setStatusId(EOrderStatus.PENDING.name());
                orderHeader.setStatusDetailId(EOrderDetailStatus.CREATED.name());
                orderHeader.setAddressId(finalAlamat);
                orderHeader.setAddressByGoogle(finalAlamatByGoogle);
                orderHeader.setLatitude(finalLatitude);
                orderHeader.setLongitude(finalLongitude);

                orderHeader.setJumlahAC(Integer.parseInt(etCounter.getText().toString()));
                orderHeader.setProblem(problem);
                orderHeader.setRescheduleCounter(0);
                orderHeader.setUpdatedTimestamp(new Date().getTime());
                orderHeader.setUpdatedBy(String.valueOf(Const.USER_AS_COSTUMER));

                Realm realm = Realm.getDefaultInstance();
                try {
                    Mitra mitraObj = CustomerUtil.lookUpMitra(realm, mitra);

                    orderHeader.setPartyId(mitraObj.getUid());
                    orderHeader.setPartyName(mitraObj.getName());

                    // final check constraint: cant have same address, same mitra and same day
                    OrderHeader first = realm.where(OrderHeader.class)
                            .equalTo("addressId", finalAlamat)
                            .equalTo("partyId", mitraObj.getUid())
                            .equalTo("dateOfService", kapanYYYYMMDD)
                            .notEqualTo("statusId", EOrderStatus.FINISHED.name())
                            .findFirst();

                    if (first != null) {
                        if (mListener != null) {
                            mListener.onError(new OrderAlreadyExists(finalAlamat, mitra, kapan));
                            return;
                        }
                    }

                    // get the rest
                    BasicInfo basicInfo = realm.where(BasicInfo.class)
                            .equalTo("uid", mUserId)
                            .findFirst();

                    orderHeader.setCustomerName(basicInfo.getName());
                    orderHeader.setPhone(basicInfo.getPhone1());


                } finally {
                    realm.close();
                }

                submitOrder(orderHeader);

            }
        });

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentOrderACInteractionListener {

        void onClickSelectMitra(List<TmpMitra> mitraInRange);

        void onEditAddress();

        void onError(Exception e);

        void onOrderCreated(OrderHeader newOrder);
    }
}
