package com.elkana.dslibrary.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

public class SimpleArrayAdapter extends ArrayAdapter<String> {
    Context mContext;


    public SimpleArrayAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);
    }
}
