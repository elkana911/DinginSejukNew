package com.elkana.teknisi.screen.dataac;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elkana.teknisi.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDataAC extends Fragment {


    public FragmentDataAC() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_activity_data_ac, container, false);

        return v;
    }

}
