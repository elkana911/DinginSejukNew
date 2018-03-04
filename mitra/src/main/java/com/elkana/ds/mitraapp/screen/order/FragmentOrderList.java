package com.elkana.ds.mitraapp.screen.order;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.elkana.ds.mitraapp.R;
import com.elkana.ds.mitraapp.screen.assign.ActivityScrollingAssignment;
import com.elkana.ds.mitraapp.screen.map.ActivityTechOtwMap;
import com.elkana.dslibrary.pojo.OrderBucket;
import com.elkana.dslibrary.util.EOrderDetailStatus;

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
    private static final String ARG_MITRA_ID = "mitraId";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParamMitraId;
    private String mParam2;

    RecyclerView rvOrders;

    private OnFragmentOrderListInteractionListener mListener;
    private RVAdapterOrderList mAdapter;

    public FragmentOrderList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param paramMitraId Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentOrderList.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentOrderList newInstance(String paramMitraId, String param2) {
        FragmentOrderList fragment = new FragmentOrderList();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_order_list, container, false);

        rvOrders = v.findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new RVAdapterOrderList(getContext(), mParamMitraId, new ListenerOrderList() {
            @Override
            public void onItemSelected(OrderBucket order) {

                Intent intent;
                switch (EOrderDetailStatus.convertValue(order.getStatusDetailId())){
                    case CREATED:
                        intent = new Intent(getActivity(), ActivityScrollingAssignment.class);
                        intent.putExtra(ActivityScrollingAssignment.PARAM_ORDER_ID, order.getUid());
                        intent.putExtra(ActivityScrollingAssignment.PARAM_CUSTOMER_ID, order.getCustomerId());
                        intent.putExtra(ActivityScrollingAssignment.PARAM_CUSTOMER_NAME, order.getCustomerName());
                        startActivity(intent);
                        break;
                    case ASSIGNED:
                        Toast.makeText(getActivity(), "Waiting for confirmation from Technician", Toast.LENGTH_SHORT).show();
                        break;
                    case CANCELLED_BY_TIMEOUT:
                        Toast.makeText(getActivity(), "This order is expired", Toast.LENGTH_SHORT).show();
                        break;
                    case OTW:
                        intent = new Intent(getActivity(), ActivityTechOtwMap.class);
                        intent.putExtra(ActivityTechOtwMap.PARAM_ORDER_ID, order.getUid());
                        startActivity(intent);
                        break;
                    default:
                        Toast.makeText(getActivity(), "Unhandled " + order.getStatusDetailId(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        rvOrders.setAdapter(mAdapter);

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
    public interface OnFragmentOrderListInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
