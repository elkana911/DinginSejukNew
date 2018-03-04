package com.elkana.customer.screen.order;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elkana.customer.R;
import com.elkana.dslibrary.component.RealmSearchView;
import com.elkana.dslibrary.pojo.mitra.Mitra;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentMitraListInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMitraList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMitraList extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    protected static final String ARG_PARAM1 = "param1";
    protected static final String ARG_PARAM2 = "param2";
    private static final String TAG = FragmentMitraList.class.getSimpleName();

    // TODO: Rename and change types of parameters
    protected String mParam1;
    protected String mParam2;

    protected Realm realm;
    protected RVAdapterMitra mAdapter;

//    @BindView(R.id.search_view)
    RealmSearchView search_view;

    protected OnFragmentMitraListInteractionListener mListener;

    public FragmentMitraList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMitraList.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMitraList newInstance(String param1, String param2) {
        FragmentMitraList fragment = new FragmentMitraList();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        this.realm = Realm.getDefaultInstance();
        loadList();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.realm != null) {
            this.realm.close();
            this.realm = null;
        }

    }

    protected void loadList() {
        long count = this.realm.where(Mitra.class).count();

        getDialog().setTitle("Pick Contract (" + count + ")");

        if (mAdapter == null) {
            mAdapter = new RVAdapterMitra(
                    getContext(),
                    this.realm,
                    "name",
                    new ListenerMitraList() {
                        @Override
                        public void onItemSelected(Mitra mitra) {
//                            https://stackoverflow.com/questions/10905312/receive-result-from-dialogfragment
                            if (mListener != null) {
                                mListener.onMitraSelected(mitra);
                            }
                            getDialog().dismiss();
                        }
                    }
            );
        }
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//        recycler_view.setLayoutManager(mLayoutManager);
        search_view.setAdapter(mAdapter);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getString(R.string.title_pick_mitra));
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mitra_list, container, false);

//        ButterKnife.bind(this, v);

        search_view = v.findViewById(R.id.search_view);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentMitraListInteractionListener) {
            mListener = (OnFragmentMitraListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentMitraListInRangeInteractionListener");
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
    public interface OnFragmentMitraListInteractionListener {
        // TODO: Update argument type and name
        void onMitraSelected(Mitra mitra);
    }

}
