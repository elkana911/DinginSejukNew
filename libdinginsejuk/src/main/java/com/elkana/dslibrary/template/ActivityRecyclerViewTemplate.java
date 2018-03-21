package com.elkana.dslibrary.template;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.elkana.dslibrary.R;
import com.elkana.dslibrary.activity.FirebaseActivity;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Eric on 19-Mar-18.
 */

public class ActivityRecyclerViewTemplate extends FirebaseActivity{

    RecyclerView rv;
    RVAdapterTemplate mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rv_template);


        rv = findViewById(R.id.rvList);
        rv.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new RVAdapterTemplate(this);

        rv.setAdapter(mAdapter);

    }

    @Override
    protected void onLoggedOff() {

    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {

    }
}
