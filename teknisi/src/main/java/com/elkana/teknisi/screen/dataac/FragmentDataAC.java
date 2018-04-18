package com.elkana.teknisi.screen.dataac;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.elkana.teknisi.R;
import com.elkana.teknisi.pojo.IsiDataAC;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDataAC extends Fragment {

    public long uid;

    public EditText etNotes, etTipe;

    public Spinner spThnPemasangan, spDaya, spMerk;

    public FragmentDataAC() {
        // Required empty public constructor
    }

    public void prepareFragmentUsingId(long uid, IsiDataAC data){

        this.uid = uid;

        if (data == null) {
            return;
        }

        etNotes.setText(data.getNotes());
        etTipe.setText(data.getTipeAC());

        //spMerk.setSelection(pos);

    }

    public IsiDataAC buildData(){

        if (uid < 1)
            throw new RuntimeException("uid should not be less than 1");

        IsiDataAC data = new IsiDataAC();

        data.setUid(uid);

        // build
        final String merkAC = spMerk.getSelectedItem() == null ? null : (String) spMerk.getSelectedItem();
        data.setMerkAC(merkAC);

        final String dayaAC = spDaya.getSelectedItem() == null ? null : (String) spDaya.getSelectedItem();
        data.setDayaAC(dayaAC);

        final String thnPemasangan = spThnPemasangan.getSelectedItem() == null ? null : (String) spThnPemasangan.getSelectedItem();
        data.setTahunPemasangan(thnPemasangan);

        data.setNotes(etNotes.getText().toString().trim());
        data.setTipeAC(etTipe.getText().toString().trim());

        return data;
    }

    public boolean validateForm() {
        boolean cancel = false;



        if (cancel)
            return false;

        return true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_activity_data_ac, container, false);

        etNotes = v.findViewById(R.id.etNotes);
        etTipe = v.findViewById(R.id.etTipe);

        spThnPemasangan = v.findViewById(R.id.spThnPemasangan);
        spDaya = v.findViewById(R.id.spDaya);
        spMerk = v.findViewById(R.id.spMerek);

        return v;
    }

}
