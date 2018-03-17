package com.elkana.teknisi.screen.order;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.elkana.dslibrary.activity.FirebaseActivity;
import com.elkana.dslibrary.pojo.mitra.NotifyNewOrderItem;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.R;
import com.elkana.teknisi.screen.payment.ActivityPayment;
import com.google.firebase.auth.FirebaseUser;

public class ActivityNewOrder extends FirebaseActivity {

    public static final String TAG = ActivityNewOrder.class.getSimpleName();

    public static final String PARAM_ORDER_ID = "order.id";
//    public static final String PARAM_TECH_ID = "tech.id";
    public static final String PARAM_MITRA_ID = "mitra.id";

    String mOrderId, mMitraId;
    RecyclerView rvOrders;
//    private boolean selfDeny = false;

    private RVAdapterNotifyNewOrderList mAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        mOrderId = getIntent().getStringExtra(PARAM_ORDER_ID);
//        mTechId = mAuth.getCurrentUser().getUid();
//        mCustomerId = getIntent().getStringExtra(PARAM_CUSTOMER_ID);
//        mCustomerName = getIntent().getStringExtra(PARAM_CUSTOMER_NAME);
        mMitraId = getIntent().getStringExtra(PARAM_MITRA_ID);

        if (Util.TESTING_MODE && mOrderId == null) {
            mOrderId = "-L6o-DwfOBp7lBgFxh6R";
//            mTechId = "BFAi7avqKCbtHhYMydfoHiKifVv2";
//            mCustomerName = "Eric Elkana";
            mMitraId = "a6VtLlJ3nGZKlkZZGXLHTwGVbQT2";
        }

        Button btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        String mTechId = mAuth.getCurrentUser().getUid();

        String mTechName = mAuth.getCurrentUser().getDisplayName();
        if (TextUtils.isEmpty(mTechName)) {
            BasicInfo basicInfo = this.realm.where(BasicInfo.class).findFirst();
            mTechName = basicInfo.getName();
        }

        mAdapter = new RVAdapterNotifyNewOrderList(this, mMitraId, mTechId, mTechName, new ListenerNotifyNewOrderList() {
            @Override
            public void onDeny(NotifyNewOrderItem data) {

//                selfDeny = true;
//
//                if (mAdapter.getItemCount() < 2) {
//                    setResult(RESULT_CANCELED);
//                    finish();
//                }
            }

            @Override
            public void onAccept(NotifyNewOrderItem data) {
//                if (mAdapter.getItemCount() < 2) {
//                    setResult(RESULT_OK);
//                    finish();
//                }

            }

            @Override
            public void onOrderRemoved(NotifyNewOrderItem data) {
//                if (!selfDeny) {
//                    Toast.makeText(ActivityNewOrder.this, "Maaf, Booking dari " + data.getCustomerName() + " sudah tidak tersedia.", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onTimesUp() {

            }

            @Override
            public void onTimerStart() {
//                selfDeny = false;
            }
        });

        rvOrders = findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new GridLayoutManager(this, 1));
        rvOrders.setAdapter(mAdapter);
    }

    @Override
    protected void onLoggedOff() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onLoggedOn(FirebaseUser user) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanUpListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_new_order, menu);

        Drawable drawable = menu.findItem(R.id.action_close).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, android.R.color.white));
        menu.findItem(R.id.action_close).setIcon(drawable);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
