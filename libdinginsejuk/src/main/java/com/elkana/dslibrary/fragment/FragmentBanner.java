package com.elkana.dslibrary.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.elkana.dslibrary.R;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentBannerInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentBanner#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentBanner extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_URL = "url";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mUrl;
    private String mParam2;

    private OnFragmentBannerInteractionListener mListener;
    private OkHttpClient client;

    public FragmentBanner() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentBanner.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentBanner newInstance(String param1, String param2) {
        FragmentBanner fragment = new FragmentBanner();
        Bundle args = new Bundle();
        args.putString(ARG_URL, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString(ARG_URL);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_banner, container, false);

        ImageView ivBanner = v.findViewById(R.id.ivBanner);

        Picasso pic = new Picasso.Builder(getContext())
                .downloader(new OkHttp3Downloader(client))
                .build();
        pic.setIndicatorsEnabled(true);

        Uri uri =  Uri.parse( mUrl );
//        Uri uri =  Uri.parse( "http://www.facebook.com" );

        pic.load(uri).into(ivBanner, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                Toast.makeText(getContext(), "Error loading banner ", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentBannerInteractionListener) {
            mListener = (OnFragmentBannerInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnFragmentBannerInteractionListener.class.getSimpleName());
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
    public interface OnFragmentBannerInteractionListener {
        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
    }
}
