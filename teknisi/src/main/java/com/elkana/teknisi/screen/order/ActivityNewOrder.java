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

import com.elkana.dslibrary.activity.FirebaseActivity;
import com.elkana.dslibrary.pojo.mitra.NotifyNewOrderItem;
import com.elkana.dslibrary.pojo.user.BasicInfo;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.R;
import com.google.firebase.auth.FirebaseUser;

public class ActivityNewOrder extends FirebaseActivity {

    public static final String TAG = ActivityNewOrder.class.getSimpleName();

    public static final String PARAM_ORDER_ID = "order.id";
//    public static final String PARAM_TECH_ID = "tech.id";
    public static final String PARAM_MITRA_ID = "mitra.id";

    String mOrderId, mMitraId;
//    boolean isOrderTaken = false;

//    Button btnGiveUpOrDo, btnTakeOrder, btnDenyOrder;
//    TextView tvCounter, tvPleaseAcceptNewOrder;
//    View llQuickButtons;
    RecyclerView rvOrders;

    private RVAdapterNotifyNewOrderList mAdapter;

//    CountDownTimer timer;
//    private ValueEventListener mQuickestEventListener;
//    private DatabaseReference fightRef;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

/*
        long expirationMillis = 2 * 60 * 1000;

        timer = new CountDownTimer(expirationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String ss = Util.convertDateToString(new Date(millisUntilFinished), "mm:ss");
                tvCounter.setText(ss);
            }

            @Override
            public void onFinish() {
                tvCounter.setText("Expired!!");

                btnGiveUpOrDo.setVisibility(View.VISIBLE);
                llQuickButtons.setVisibility(View.GONE);
                // TODO: update firebase db

            }
        }.start();
*/
    }

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
/*
        llQuickButtons = findViewById(R.id.llQuickButtons);
        tvPleaseAcceptNewOrder = findViewById(R.id.tvPleaseAcceptNewOrder);
        tvCounter = findViewById(R.id.tvCounter);
        btnGiveUpOrDo = findViewById(R.id.btnGiveUpOrDo);
        btnGiveUpOrDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();

                if (isOrderTaken) {
                    setResult(RESULT_OK);
                } else {
                    setResult(RESULT_CANCELED);
                }

                finish();
            }
        });

        btnDenyOrder = findViewById(R.id.btnDenyOrder);
        btnDenyOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showDialogConfirmation(ActivityNewOrder.this, "Tolak Order", "Yakin Anda tolak dan coba Lain kali ?", new ListenerPositiveConfirmation() {
                    @Override
                    public void onPositive() {
                        timer.cancel();
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
        }
        });

        btnTakeOrder = findViewById(R.id.btnTakeOrder);
        btnTakeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = Util.showProgressDialog(ActivityNewOrder.this);

                // check already taken ? assume device is offline intentfully

                final DatabaseReference _fightRef = FBUtil.Assignment_fight(mOrderId)
                        .child("techId");

                _fightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            _fightRef.setValue(mTechId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    timer.cancel();
                                    alertDialog.dismiss();

                                    // hapus notify_new_order
                                    FBUtil.TechnicianReg_DeleteNotifyNewOrder(mMitraId, mTechId,  mOrderId,null);
                                }
                            });
                            return;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
                    }
                });
            }

        });
*/
        String mTechId = mAuth.getCurrentUser().getUid();

        String mTechName = mAuth.getCurrentUser().getDisplayName();
        if (TextUtils.isEmpty(mTechName)) {
            BasicInfo basicInfo = this.realm.where(BasicInfo.class).findFirst();
            mTechName = basicInfo.getName();
        }

        mAdapter = new RVAdapterNotifyNewOrderList(this, mMitraId, mTechId, mTechName, new ListenerNotifyNewOrderList() {
            @Override
            public void onDeny(NotifyNewOrderItem data) {
                if (mAdapter.getItemCount() < 2) {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }

            @Override
            public void onAccept(NotifyNewOrderItem data) {
                if (mAdapter.getItemCount() < 2) {
                    setResult(RESULT_OK);
                    finish();
                }

            }
        });

        rvOrders = findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new GridLayoutManager(this, 1));
        rvOrders.setAdapter(mAdapter);
/*
        mQuickestEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    return;

                // already taken
                isOrderTaken = true;
                String value = dataSnapshot.getValue().toString();

                if (value.equals(mTechId)) {
                    tvCounter.setText("YESS !!!");
                    tvPleaseAcceptNewOrder.setText("Anda tercepat ambil order.\nSelamat bekerja !");

                } else {
                    tvCounter.setText("Terlambat !");
                    tvPleaseAcceptNewOrder.setText("Order sudah diambil Teknisi lain");
                }

                llQuickButtons.setVisibility(View.GONE);
                btnGiveUpOrDo.setText("Tutup Layar");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
            }
        };

        fightRef = FBUtil.Assignment_fight(mOrderId).child("techId");

        fightRef.addValueEventListener(mQuickestEventListener);
        */
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
//        fightRef.removeEventListener(mQuickestEventListener);
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
