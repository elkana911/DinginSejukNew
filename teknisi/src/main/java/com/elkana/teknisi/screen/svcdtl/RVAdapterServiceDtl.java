package com.elkana.teknisi.screen.svcdtl;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.pojo.mitra.ServiceType;
import com.elkana.dslibrary.pojo.technician.ServiceItem;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Eric on 13-Nov-17.
 */
public class RVAdapterServiceDtl extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = RVAdapterServiceDtl.class.getSimpleName();

    private final int VIEW_TYPE_CELL = 1;
    private final int VIEW_TYPE_DLL = 2;
    private final int VIEW_TYPE_FOOTER = 3;

    private Context mContext;
    private String mMitraId, mAssignmentId;

    private AdapterServiceTypeSpinner mAdapterSpinner;

    private List<ServiceItem> mList = new ArrayList<>();
    private ListenerServiceDetail mListener;

    public RVAdapterServiceDtl(Context context, String mitraId, String assignmentId, ListenerServiceDetail listener) {
        mContext = context;
        mMitraId = mitraId;
        mAssignmentId = assignmentId;
        mListener = listener;

        mAdapterSpinner = new AdapterServiceTypeSpinner(mContext, android.R.layout.simple_spinner_item, mMitraId);

        if (mListener != null) {
            mListener.onPrepareList(mList);

            if (mList.size() > 0) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
                /*
                ActivityServiceDetail.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mInfo.setText(str);
                    }
                });
                notifyDataSetChanged();
                */
            }
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CELL) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_ac_item_default, parent, false);
            return new MyViewDefaultHolder(itemView);
        } else
        if (viewType == VIEW_TYPE_DLL) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_ac_item_dll, parent, false);
            return new MyViewDllHolder(itemView);
        } else {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_ac_item_add, parent, false);
            return new MyAddOrderHolder(itemView);
        }

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // posisi terakhir adalah tombol utk add data
        if (position == mList.size())
            return;

        final ServiceItem row = mList.get(position);

        if (isServiceTypeIsDLL(row))
            ((MyViewDllHolder) holder).setData(row, position);
        else
            ((MyViewDefaultHolder) holder).setData(row, position);

    }

    @Override
    public int getItemViewType(int position) {
        if (position == mList.size()) {
            return VIEW_TYPE_FOOTER;
        } else {
            final ServiceItem row = mList.get(position);

            if (isServiceTypeIsDLL(row))
                return VIEW_TYPE_DLL;
            else
                return VIEW_TYPE_CELL;
//                return (position == mList.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    public List<ServiceItem> getList() {
        return mList;
    }

    private boolean isServiceTypeIsDLL(ServiceItem obj) {
        return obj.getServiceTypeId() < 0;
    }

    private void removeRow(ServiceItem row, int position) {
        if (mListener != null) {
            mListener.onDeleteItem(row, position);
            mList.remove(position);
        }

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());

        notifyDataSetChanged();

    }

    private int isDataExists(long uid) {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getUid() == uid)
                return i;
        }

        return -1;
    }

    class MyViewDllHolder extends RecyclerView.ViewHolder {
        private EditText etServiceItem;
        private FloatingActionButton fabDelete;
        private TextView tvPrice;

        public MyViewDllHolder(View itemView) {
            super(itemView);

            etServiceItem = itemView.findViewById(R.id.etServiceItem);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            fabDelete = itemView.findViewById(R.id.fabDelete);
        }

        public void setData(final ServiceItem obj, final int position) {
            etServiceItem.setText(obj.getServiceLabel());

            Double sum = new Double(obj.getRate());
            tvPrice.setText(Util.convertLongToRupiah(sum.longValue()));

            fabDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    class MyViewDefaultHolder extends RecyclerView.ViewHolder {
        private Spinner spServiceItem;
        private FloatingActionButton fabDelete;
        private EditText etPromoCode, etCounter;
        private TextView tvPrice, tvUid;
        private Button btnDataAC;
        private AppCompatImageView btnDecItem, btnIncItem;

        public MyViewDefaultHolder(View itemView) {
            super(itemView);

            spServiceItem = itemView.findViewById(R.id.spServiceItem);
            spServiceItem.setAdapter(mAdapterSpinner);
            spServiceItem.setSelection(0);

            fabDelete = itemView.findViewById(R.id.fabDelete);

            etPromoCode = itemView.findViewById(R.id.etPromoCode);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvUid = itemView.findViewById(R.id.tvUid);

            btnDataAC = itemView.findViewById(R.id.btnDataAC);

            etCounter = itemView.findViewById(R.id.etCounter);

            btnDecItem = itemView.findViewById(R.id.ivDecItem);
            btnIncItem = itemView.findViewById(R.id.ivIncItem);

        }

        private void updateServiceItemByCounter(ServiceItem obj, int count) {
            etCounter.setText(String.valueOf(count));

            obj.setCount(count);

            ServiceType sst = (ServiceType) spServiceItem.getAdapter().getItem(spServiceItem.getSelectedItemPosition());

            Double sum =  sst.getRate() * count;
            tvPrice.setText(Util.convertLongToRupiah(sum.longValue()));
        }


        public void setData(final ServiceItem obj, final int position) {

            tvUid.setVisibility(Util.DEVELOPER_MODE ? View.VISIBLE : View.GONE);
            tvUid.setText("" + obj.getUid());

            spServiceItem.setSelection(0);

            for (int i = 0; i < mAdapterSpinner.getCount(); i++) {
                ServiceType _obj = mAdapterSpinner.getItem(i);
                if (_obj.getServiceTypeId() == obj.getServiceTypeId()) {
                    spServiceItem.setSelection(i);
                    break;
                }
            }

            updateServiceItemByCounter(obj, obj.getCount());

            etPromoCode.setText(obj.getPromoCode());
            etPromoCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            btnDataAC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onAddDataAC(obj);
                }
            });

            fabDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Util.showDialogConfirmation(mContext, null, "Hapus Jasa " + obj.getServiceLabel() + " ?", new ListenerPositiveConfirmation() {
                        @Override
                        public void onPositive() {
                            removeRow(obj, position);
                        }
                    });
                }
            });

            btnDecItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String counter = Util.counter(etCounter.getText().toString(), false, 1, -1);

                    updateServiceItemByCounter(obj, Integer.parseInt(counter));

                    int row = isDataExists(obj.getUid());
                    mList.set(row, obj);
                    notifyDataSetChanged();

                }
            });

            btnIncItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String counter = Util.counter(etCounter.getText().toString(), true, 1, 20);

                    updateServiceItemByCounter(obj, Integer.parseInt(counter));

                    int row = isDataExists(obj.getUid());
                    mList.set(row, obj);
                    notifyDataSetChanged();

                }
            });

            spServiceItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                    ServiceType obj = (ServiceType) parent.getItemAtPosition(pos);

                    ServiceItem serviceItem = mList.get(position);
                    try {
                        serviceItem.setServiceTypeId(obj.getServiceTypeId());
                        serviceItem.setServiceLabel(obj.getServiceTypeNameBahasa());
                        serviceItem.setRate(obj.getRate());

                        String quantity = etCounter.getText().toString();

                        Double sum = Double.parseDouble(quantity) * obj.getRate();
                        tvPrice.setText(Util.convertLongToRupiah(sum.longValue()));

                        int row = isDataExists(serviceItem.getUid());
                        mList.set(row, serviceItem);
                        notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

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


                    if (mListener != null)
                        mListener.onAddServiceDetail();

                    ServiceType serviceType = mAdapterSpinner.getItem(0);

                    ServiceItem item = new ServiceItem();
                    item.setUid(new Date().getTime());
                    item.setCount(1);
                    item.setAssignmentId(mAssignmentId);
                    item.setServiceTypeId(serviceType.getServiceTypeId());
                    item.setServiceLabel(serviceType.getServiceTypeNameBahasa());
                    item.setRate(serviceType.getRate());
                    item.setPromoCode(serviceType.getPromoCode());
                    item.setUidNegative(item.getUid() * -1);

                    mList.add(item);

                    notifyDataSetChanged();
                }
            });


        }
    }

}

