package com.elkana.dslibrary.template;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.elkana.dslibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 19-Mar-18.
 */

public class RVAdapterTemplate extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_CELL = 1;
    private final int VIEW_TYPE_FOOTER = 2;

    private Context mContext;
    private List<?> mList = new ArrayList<>();


    public RVAdapterTemplate(Context context) {
        mContext = context;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CELL) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_rv_template, parent, false);
            return new MyViewHolder(itemView);
        } else {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_rv_template_add, parent, false);
            return new MyAddServiceHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == mList.size()) {
        } else {
            final Object obj = mList.get(position);

            ((MyViewHolder) holder).setData(obj);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() +1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mList.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    public List<?> getList() {
        return mList;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        public EditText etInput;

        public MyViewHolder(View itemView) {
            super(itemView);

            etInput = itemView.findViewById(R.id.etInput);
        }

        public void setData(final Object obj) {

            etInput.setText("Fill something");

        }
    }

    class MyAddServiceHolder extends RecyclerView.ViewHolder {
        public FloatingActionButton fab;

        public MyAddServiceHolder(View itemView) {
            super(itemView);

            fab = itemView.findViewById(R.id.fabAddRow);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Object item = new Object();

//                    mList.add(item);

                    notifyDataSetChanged();
                }
            });
        }
    }

}
