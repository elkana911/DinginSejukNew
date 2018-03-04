package com.elkana.ds.mitraapp.screen.assign;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elkana.ds.mitraapp.R;
import com.elkana.ds.mitraapp.pojo.TechnicianReg;
import com.elkana.dslibrary.component.RealmSearchView;

import io.realm.Realm;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentTechnicianListInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentTechnicianList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTechnicianList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MITRA_ID = "mitraId";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParamMitraId;
    private String mParam2;
    private Realm mRealm;

    RealmSearchView search_view;
    private OnFragmentTechnicianListInteractionListener mListener;
    private RSVAdapterTechnicianReg mAdapter;

    public FragmentTechnicianList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param paramMitraId Parameter 1.
     * @param param2       Parameter 2.
     * @return A new instance of fragment FragmentOrderList.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTechnicianList newInstance(String paramMitraId, String param2) {
        FragmentTechnicianList fragment = new FragmentTechnicianList();
        Bundle args = new Bundle();
        args.putString(ARG_MITRA_ID, paramMitraId);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamMitraId = getArguments().getString(ARG_MITRA_ID);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mRealm != null)
            mRealm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tech_list, container, false);

        mAdapter = new RSVAdapterTechnicianReg(getContext(), mRealm, "name", new ListenerTechnicianList() {
            @Override
            public void onItemSelected(TechnicianReg obj) {

            }
        });

        search_view = v.findViewById(R.id.search_view);
//        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        search_view.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentTechnicianListInteractionListener) {
            mListener = (OnFragmentTechnicianListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentTechnicianListInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentTechnicianListInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
