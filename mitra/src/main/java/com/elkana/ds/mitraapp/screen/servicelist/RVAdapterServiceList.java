package com.elkana.ds.mitraapp.screen.servicelist;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.elkana.ds.mitraapp.R;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerGetAllData;
import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.pojo.mitra.ServiceType;
import com.elkana.dslibrary.pojo.mitra.SubServiceType;
import com.elkana.dslibrary.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Eric on 19-Mar-18.
 */

public class RVAdapterServiceList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_CELL = 1;
    private final int VIEW_TYPE_FOOTER = 2;

    private Context mContext;
    private String mMitraId;

    private final Typeface fontFace;

    private AdapterSubServiceTypeSpinner mAdapterSubServiceTypeSpinner;

    private List<SubServiceType> mSubServiceTypes;

    private List<ServiceType> mOriginalList = new ArrayList<>();
    private List<ServiceType> mAddedList = new ArrayList<>();
    private List<ServiceType> mRemoveList = new ArrayList<>();

    public RVAdapterServiceList(Context context, String mitraId, List<SubServiceType> subServiceTypes, final ListenerGetAllData listener) {
        mContext = context;
        mMitraId = mitraId;
        mSubServiceTypes = subServiceTypes;

        fontFace = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/DinDisplayProLight.otf");

        mAdapterSubServiceTypeSpinner = new AdapterSubServiceTypeSpinner(mContext, android.R.layout.simple_spinner_item, mSubServiceTypes);

        final AlertDialog alertDialog = Util.showProgressDialog(mContext, "Loading Service List...");

        if (listener != null)
            listener.onPrepare();
        // see saveData
        FBUtil.Mitra_GetServicesRef(mMitraId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mOriginalList.clear();

                        alertDialog.dismiss();

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            ServiceType _obj = postSnapshot.getValue(ServiceType.class);
                            mOriginalList.add(_obj);

                            // change from spinner to edit text ?
                        }

                        notifyDataSetChanged();

                        if (listener != null)
                            listener.onSuccess(mOriginalList);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (listener != null)
                            listener.onError(databaseError.toException());

                        alertDialog.dismiss();
                    }
                });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CELL) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_service_list, parent, false);
            return new MyViewHolder(itemView);
        } else {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_service_list_add, parent, false);
            return new MyAddServiceHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // posisi terakhir adalah tombol utk add data
        if (position == mOriginalList.size())
            return;

        final ServiceType row = mOriginalList.get(position);

        ((MyViewHolder) holder).setData(row, position);

        ((MyViewHolder) holder).fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO dicek dulu kalo ada transaksi ga boleh diremove. tp nti dicek dulu di path mana soalnya lupa
//                FBUtil.Orders_

                Util.showDialogConfirmation(mContext, null, "Hapus layanan " + row.getServiceTypeNameBahasa() + "?", new ListenerPositiveConfirmation() {
                    @Override
                    public void onPositive() {
                        removeRow(row, position);
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return mOriginalList.size() + 1;
    }

    public List<ServiceType> getList() {
        return mOriginalList;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mOriginalList.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    public void cleanUpListener() {

    }

    public List<ServiceType> cleanBeforeSubmit() {

        List<ServiceType> bufferList = new ArrayList<>();

        if (mOriginalList.size() < 1)
            return bufferList;

        bufferList.addAll(mOriginalList);

        // remove
        for (ServiceType removedST : mRemoveList) {
            FBUtil.Mitra_GetServicesRef(mMitraId)
                    .child(String.valueOf(removedST.getServiceTypeId()))
                    .removeValue();

            for (int i = 0; i < bufferList.size(); i++) {
                if (bufferList.get(i).getServiceTypeId() == removedST.getServiceTypeId()) {
                    bufferList.remove(i);
                    break;
                }
            }
        }

        // adding
        for (ServiceType addedST : mAddedList) {
            boolean _exist = false;
            for (ServiceType _st : mOriginalList) {
                if (_st.getServiceTypeId() == addedST.getServiceTypeId()) {
                    _exist = true;
                    break;
                }
            }

            if (!_exist)
                mOriginalList.add(addedST);
        }

        String duplicate = null;
        for (int i = 0; i < mOriginalList.size() - 1; i++) {
            ServiceType _st = mOriginalList.get(i);

            for (int j = i + 1; j < mOriginalList.size(); j++) {
                if (_st.getServiceTypeId() == mOriginalList.get(j).getServiceTypeId()) {
                    duplicate = _st.getServiceTypeNameBahasa();
                    break;
                }

            }
        }

        if (duplicate != null) {
            Toast.makeText(mContext, "Duplikasi layanan " + duplicate + ". Please Check.", Toast.LENGTH_SHORT).show();
            return null;
        }

        bufferList.addAll(mAddedList);

        return bufferList;
    }

    private void removeRow(ServiceType row, int position) {
        // cek dulu kalo ga ada di draftadded br bisa dicatat sbg penghapusan data
        int _idx = isAddedDraftExists(row.getServiceTypeId());
        if (_idx < 0) {
            mRemoveList.add(row);
        }

        _idx = isAddedDraftExists(row.getServiceTypeId());

        if (_idx > -1) {
            mAddedList.remove(_idx);
        }

        mOriginalList.remove(position);

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());

        notifyDataSetChanged();
    }

    public boolean saveData() {
        String path = FBUtil.REF_MITRA_AC + "/" + mMitraId + "/services";

        List<ServiceType> cleanList = cleanBeforeSubmit();

        if (cleanList == null)
            return false;

        try {
            for (ServiceType st : cleanList) {
                //mitra/ac/X4nJdNrOvYQOwwv5mp45UXweZLh2/services/110
                Map<String, Object> keyVal = FBUtil.convertObjectToKeyVal(path + "/" + st.getServiceTypeId(), st);

                FirebaseDatabase.getInstance().getReference().updateChildren(keyVal);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private Spinner spSubServiceItem;
        private EditText etRateCounter;
        private FloatingActionButton fabDelete;
        private AppCompatImageView btnDecItem, btnIncItem;
        private CheckBox cbIsPromo;
        private Button btnPromoRule;

        public MyViewHolder(View itemView) {
            super(itemView);

            fabDelete = itemView.findViewById(R.id.fabDelete);
            etRateCounter = itemView.findViewById(R.id.etCounter);

            spSubServiceItem = itemView.findViewById(R.id.spSubServiceItem);
            spSubServiceItem.setAdapter(mAdapterSubServiceTypeSpinner);
            spSubServiceItem.setSelection(0);

            btnDecItem = itemView.findViewById(R.id.ivDecItem);
            btnDecItem.setColorFilter(Color.parseColor("#A9A9A9"));
//            btnDecItem.setImageResource();
            btnIncItem = itemView.findViewById(R.id.ivIncItem);
            btnIncItem.setColorFilter(Color.parseColor("#A9A9A9"));

            btnPromoRule = itemView.findViewById(R.id.btnPromoRule);
            cbIsPromo = itemView.findViewById(R.id.cbIsPromo);
            cbIsPromo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    btnPromoRule.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
                }
            });
        }

        public void setData(final ServiceType obj, final int position) {

            etRateCounter.setText(String.valueOf(new Double(obj.getRate()).intValue()));

            spSubServiceItem.setSelection(0);

            for (int i = 0; i < mAdapterSubServiceTypeSpinner.getCount(); i++) {
                SubServiceType _obj = mAdapterSubServiceTypeSpinner.getItem(i);
                if (_obj.getTypeId() == obj.getServiceTypeId()) {
                    spSubServiceItem.setSelection(i);
                    break;
                }

            }
            spSubServiceItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SubServiceType _row = mAdapterSubServiceTypeSpinner.getItem(position);


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            btnDecItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String rate = Util.counter(etRateCounter.getText().toString(), false, 500, -1, 500);
                    etRateCounter.setText(rate);

                    obj.setRate(new BigDecimal(rate).doubleValue());
                    int row = isOriginalExists(obj.getServiceTypeId());
                    mOriginalList.set(row, obj);
                    notifyDataSetChanged();
                }
            });

            btnIncItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String rate = Util.counter(etRateCounter.getText().toString(), true, 1, 5000000, 500);
                    etRateCounter.setText(rate);

                    obj.setRate(new BigDecimal(rate).doubleValue());
                    int row = isOriginalExists(obj.getServiceTypeId());
                    mOriginalList.set(row, obj);
                    notifyDataSetChanged();
                }
            });


            /* bahaya nih kalo diterapkan di list yg berubah2
            etRateCounter.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    ServiceType serviceType = obj;
                    try {
                        serviceType.setRate(new BigDecimal(s.toString()).doubleValue());

                        // isi mOriginalList mungkin sudah berubah, jadi serviceType perlu dicocokkan dulu

                        int _row = isOriginalExists(serviceType.getServiceTypeId());
                        if (_row > -1) {
                            mOriginalList.set(_row, serviceType);

                            notifyDataSetChanged();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
*/

        }
    }

    class MyAddServiceHolder extends RecyclerView.ViewHolder {
        public FloatingActionButton fab;

        public MyAddServiceHolder(View itemView) {
            super(itemView);

            fab = itemView.findViewById(R.id.fabAddService);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    if (mListener != null) {

                    //looking non existence previous item
                    boolean addedRow = false;
                    for (SubServiceType sst : mAdapterSubServiceTypeSpinner.getItems()) {

                        if (isOriginalExists(sst.getTypeId()) < 0) {
                            ServiceType item = new ServiceType();
                            item.setEnable(true);
                            item.setServiceTypeId(sst.getTypeId());
                            item.setServiceTypeName(sst.getTypeName());
                            item.setServiceTypeNameBahasa(sst.getTypeNameBahasa());
                            item.setPromoCode(null);
                            item.setRate(25000);
                            item.setVisible(true);
                            item.setUpdatedTimestamp(new Date().getTime());

                            mOriginalList.add(item);

                            if (isAddedDraftExists(item.getServiceTypeId()) < 0) {
                                mAddedList.add(item);
                            }

                            notifyDataSetChanged();

                            addedRow = true;
                            break;
                        }
                    }

                    if (!addedRow) {
                        Toast.makeText(mContext, "Cannot add more service", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    private int isOriginalExists(int serviceTypeId) {
        for (int i = 0; i < mOriginalList.size(); i++) {
            if (mOriginalList.get(i).getServiceTypeId() == serviceTypeId)
                return i;
        }

        return -1;
    }

    private int isRemoveDraftExists(int serviceTypeId) {
        for (int i = 0; i < mRemoveList.size(); i++) {
            if (mRemoveList.get(i).getServiceTypeId() == serviceTypeId)
                return i;
        }

        return -1;
    }

    private int isAddedDraftExists(int serviceTypeId) {
        for (int i = 0; i < mAddedList.size(); i++) {
            if (mAddedList.get(i).getServiceTypeId() == serviceTypeId)
                return i;
        }

        return -1;
    }

}
