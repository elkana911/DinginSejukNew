package com.elkana.teknisi.screen.svcdtl;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.elkana.dslibrary.pojo.mitra.SubServiceType;
import com.elkana.dslibrary.pojo.technician.ServiceItem;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.R;
import com.elkana.teknisi.pojo.ServiceToParty;
import com.elkana.teknisi.util.TeknisiUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Eric on 13-Nov-17.
 */
public class RVAdapterServiceDetail extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = RVAdapterServiceDetail.class.getSimpleName();

    private final int VIEW_TYPE_CELL = 1;
    private final int VIEW_TYPE_FOOTER = 2;
    private final Typeface fontFace;

    private List<ServiceToParty> mListSubService = new ArrayList<>();
//    private List<SubServiceType> mListSubService = new ArrayList<>();
    private AdapterServiceACSpinner mAdapterServiceACSpinner;

    private Context mContext;
    private String mAssignmentId;
    private List<ServiceItem> mList = new ArrayList<>();
    private ListenerServiceDetail mListener;

    public RVAdapterServiceDetail(Context context, String assignmentId, String partyId, ListenerServiceDetail listener) {
        mContext = context;
        mListener = listener;
        mAssignmentId = assignmentId;

        fontFace = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/DinDisplayProLight.otf");

        mAdapterServiceACSpinner = new AdapterServiceACSpinner(mContext, android.R.layout.simple_spinner_item, mListSubService);

//        if (!NetUtil.isConnected(mContext)) {
//            if (getDataLocal())
//                return;
//        }

        final AlertDialog dialog = Util.showProgressDialog(mContext, "Check Available Services");

        FirebaseDatabase.getInstance().getReference(TeknisiUtil.REF_VENDOR_AC_SERVICES)
                .child(partyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dialog.dismiss();
                        if (!dataSnapshot.exists())
                            return;

                        Realm _r = Realm.getDefaultInstance();
                        try {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                ServiceToParty _obj = postSnapshot.getValue(ServiceToParty.class);
//                            SubServiceType _obj = postSnapshot.getValue(SubServiceType.class);
                                Log.e(TAG, _obj.toString());

//                            if (_obj.isVisible())
//                                mListSubService.add(_obj);
                                // isi property yg kosong dgn value dari subservicetype. agak membingungkan memang tp krn alasan efisiensi json di firebase
                                SubServiceType subServiceType = _r.where(SubServiceType.class)
                                        .equalTo("typeId", _obj.getServiceTypeId())
                                        .findFirst();

                                _obj.setServiceLabel(subServiceType.getTypeName());
                                _obj.setServiceLabelBahasa(subServiceType.getTypeNameBahasa());

                                mListSubService.add(_obj);
                            }
                        }finally {
                            _r.close();
                        }

                        mAdapterServiceACSpinner.notifyDataSetChanged();
/*
                        // cache
                        Realm _r = Realm.getDefaultInstance();
                        try{
                            _r.beginTransaction();
                            _r.copyToRealmOrUpdate(mListSubService);
                            _r.commitTransaction();
                        }finally {
                            _r.close();
                        }*/
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        dialog.dismiss();
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                    }
                });

    }
/*
    private boolean getDataLocal() {
        mListSubService.clear();

        Realm r = Realm.getDefaultInstance();

        try{
            RealmResults<SubServiceType> all = r.where(SubServiceType.class)
                    .equalTo("visible", true)
                    .findAll();

            if (all.size() > 0) {
                mListSubService.addAll(r.copyFromRealm(all));
                mAdapterServiceACSpinner.notifyDataSetChanged();
                return true;
            }
        }finally {
            r.close();
        }

        return false;
    }
*/

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CELL) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_ac_item, parent, false);
            return new MyViewHolder(itemView);
        } else {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_ac_item_add, parent, false);
            return new MyAddOrderHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (position == mList.size()) {
        } else {

            final ServiceItem row = mList.get(position);

            final TextView tvPrice = ((MyViewHolder) holder).tvPrice;
//            final TextView tvPricePerItem = ((MyViewHolder) holder).tvPricePerItem;

            final Spinner spServiceItem = ((MyViewHolder) holder).spServiceItem;
//            final Spinner spCount = ((MyViewHolder) holder).spCount;
            final EditText etCounter = ((MyViewHolder) holder).etCounter;

            spServiceItem.setSelection(0);

            for (int i = 0; i < mAdapterServiceACSpinner.getCount(); i++) {
                ServiceToParty _obj = mAdapterServiceACSpinner.getItem(i);
                if (_obj.getServiceTypeId() == row.getServiceTypeId()) {
                    spServiceItem.setSelection(i);
                    break;
                }
            }

            //            ((MyViewHolder) holder).spCount.setSelection(row.getCount() -1);
            ((MyViewHolder) holder).etPromoCode.setText(row.getPromoCode());
            ((MyViewHolder) holder).etPromoCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            ((MyViewHolder) holder).fabDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onDeleteItem(row, position);
                        mList.remove(position);
                    }

                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());

                    notifyDataSetChanged();
                }
            });

            spServiceItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    ServiceToParty obj = (ServiceToParty) parent.getItemAtPosition(pos);

                    ServiceItem serviceItem = mList.get(position);
                    try {
                        serviceItem.setServiceTypeId(obj.getServiceTypeId());
                        serviceItem.setServiceLabel(obj.getServiceLabel());
                        serviceItem.setRate(obj.getBasicFare());

                        String quantity = etCounter.getText().toString();
//                        String quantity = (String) spCount.getAdapter().getItem(spCount.getSelectedItemPosition());

                        Double sum =  Double.parseDouble(quantity) * obj.getBasicFare();
                        tvPrice.setText(Util.convertLongToRupiah(sum.longValue()));

//                        tvPricePerItem.setText("@" + Util.convertLongToRupiah((new Double(obj.getBasicFare()).longValue())));

                        mList.set(position, serviceItem);
//                        notifyItemChanged(position);//  dont call this, will repaint twice and slow performace
                        notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            etCounter.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    ServiceItem serviceItem = mList.get(position);
                    try {
                        serviceItem.setCount(Integer.parseInt(s.toString()));

                        ServiceToParty serviceToParty = (ServiceToParty)spServiceItem.getAdapter().getItem(spServiceItem.getSelectedItemPosition());

                        Double sum =  Double.parseDouble(s.toString()) * serviceToParty.getBasicFare();
                        tvPrice.setText(Util.convertLongToRupiah(sum.longValue()));

                        mList.set(position, serviceItem);
                        notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
/*
            ((MyViewHolder) holder).spCount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    String obj = (String) parent.getItemAtPosition(pos);

                    ServiceItem serviceItem = mList.get(position);
                    try {
                        serviceItem.setCount(Integer.parseInt(obj));

                        ServiceToParty serviceToParty = (ServiceToParty)spServiceItem.getAdapter().getItem(spServiceItem.getSelectedItemPosition());

                        Double sum =  Double.parseDouble(obj) * serviceToParty.getBasicFare();
                        tvPrice.setText(String.valueOf(sum.longValue()));

                        mList.set(position, serviceItem);
                        notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
*/
/*
            ((MyViewHolder) holder).tvAddress.setText(ctx.getString(R.string.row_label_ac_service, obj.getJumlahAC()));
            ((MyViewHolder) holder).tvAddress.setText(obj.getAddressId());
            ((MyViewHolder) holder).tvDateOfService.setText(ctx.getString(R.string.prompt_schedule) + ": " + Util.prettyTimestamp(ctx, obj.getTimestamp()));

            Realm r = Realm.getDefaultInstance();
            try{

                Mitra mitra = TeknisiUtil.lookUpMitra(r, Long.parseLong(obj.getPartyId()));

                if (mitra != null) {
                    ((MyViewHolder) holder).tvMitra.setText(ctx.getString(R.string.row_order_mitra, mitra.getName()));
                }

                EOrderDetailStatus orderStatus = EOrderDetailStatus.convertValue(obj.getStatusDetailId());

                ((MyViewHolder) holder).tvStatus.setText(TeknisiUtil.getMessageStatusDetail(ctx, orderStatus));

            }finally {
                r.close();
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onItemSelected(obj);
                }
            });
*/
        }

    }

    @Override
    public int getItemViewType(int position) {
        return (position == mList.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL; // super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    public List<ServiceItem> getList() {
        return mList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public Spinner spServiceItem;
        public FloatingActionButton fabDelete;
        public EditText etPromoCode, etCounter;
        public TextView tvPrice;
        public Button btnDataAC;

        public MyViewHolder(View itemView) {
            super(itemView);

            spServiceItem = itemView.findViewById(R.id.spServiceItem);
            spServiceItem.setAdapter(mAdapterServiceACSpinner);
            spServiceItem.setSelection(0);

            fabDelete = itemView.findViewById(R.id.fabDelete);

            etPromoCode = itemView.findViewById(R.id.etPromoCode);
            tvPrice = itemView.findViewById(R.id.tvPrice);

            btnDataAC = itemView.findViewById(R.id.btnDataAC);

            etCounter = itemView.findViewById(R.id.etCounter);

            AppCompatImageView btnDecItem = itemView.findViewById(R.id.ivDecItem);
            btnDecItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etCounter.setText(Util.counter(etCounter.getText().toString(), false, 1, -1));
                }
            });
            AppCompatImageView btnIncItem = itemView.findViewById(R.id.ivIncItem);
            btnIncItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etCounter.setText(Util.counter(etCounter.getText().toString(), true, 1, 20));
                }
            });



            btnDataAC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onAddDataAC();
                }
            });

            /*
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            tvAddress.setTypeface(fontFace);
/
//            btnCancelOrder = (ImageButton) itemView.findViewById(R.id.btnCancelOrder);
//            btnCancelOrder.setImageResource(R.drawable.ic_indeterminate_check_box_black_24dp);
//            btnCancelOrder.setColorFilter(Color.parseColor("#000000"));
*/
        }
    }

    class MyAddOrderHolder extends RecyclerView.ViewHolder {
        public FloatingActionButton fab;

        public MyAddOrderHolder(View itemView) {
            super(itemView);

            fab = itemView.findViewById(R.id.fabAddService);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
//                        mListener.onAddServiceDetail();
                        ServiceItem item = new ServiceItem();
                        item.setUid(new Date().getTime());
                        item.setCount(1);
                        item.setAssignmentId(mAssignmentId);
                        item.setUidNegative(Math.abs(item.getUid()));

                        mList.add(item);

                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

}

