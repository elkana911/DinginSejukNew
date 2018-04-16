package com.elkana.customer.screen.order;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elkana.customer.R;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.pojo.OrderHeader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentOrderListInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentOrderList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentOrderList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "userId";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = FragmentOrderList.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mUserId;
    private String mParam2;

    private List<OrderHeader> mList = new ArrayList<>();
    private Realm realm;
    private RVAdapterOrders mAdapter;

    RecyclerView rvOrders;

    private OnFragmentOrderListInteractionListener mListener;
    private DatabaseReference orderRef;

    public FragmentOrderList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMitraList.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentOrderList newInstance(String userId, String param2) {
        FragmentOrderList fragment = new FragmentOrderList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, userId);
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

    }

    @Override
    public void onStart() {
        super.onStart();
        this.realm = Realm.getDefaultInstance();

        if (this.mUserId == null)
            return;

        reInitiate(getActivity(), this.mUserId);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.realm != null) {
            this.realm.close();
            this.realm = null;
        }

        if (mAdapter != null)
            mAdapter.cleanUpListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_order_list, container, false);

        rvOrders = v.findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentOrderListInteractionListener) {
            mListener = (OnFragmentOrderListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnFragmentOrderListInteractionListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void reInitiate(Context ctx, String customerId) {

        this.mUserId = customerId;

//        orderRef = FirebaseDatabase.getInstance().getReference("orders/ac/orderHeader/" + this.mUserId);
        orderRef = FirebaseDatabase.getInstance().getReference(FBUtil.REF_ORDERS_CUSTOMER_AC_PENDING).child(this.mUserId);

        if (mAdapter != null)
            mAdapter.cleanUpListener();

        mAdapter = new RVAdapterOrders(ctx, orderRef, this.mUserId, mListener);

        rvOrders.setAdapter(mAdapter);

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
    public interface OnFragmentOrderListInteractionListener extends ListenerOrderList {
    }

}
