package com.elkana.ds.mitraapp.screen.info.price;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.elkana.ds.mitraapp.AFirebaseMitraActivity;
import com.elkana.ds.mitraapp.R;
import com.elkana.ds.mitraapp.pojo.MobileSetup;
import com.elkana.ds.mitraapp.util.MitraUtil;
import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerPositiveConfirmation;
import com.elkana.dslibrary.pojo.mitra.PriceInfo;
import com.elkana.dslibrary.util.NetUtil;
import com.elkana.dslibrary.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ActivityPriceInfo extends AFirebaseMitraActivity {

    private static final String TAG = ActivityPriceInfo.class.getSimpleName();

    private int penaltyCounter = -3;
    private static final int PENALTY_WAIT_MILLISECONDS = 60000;

    MobileSetup mobileSetup;
    EditText etMemo;
    TextView tvFooterInfo;
    Button btnSubmit;
    String buffer;

    private void startTimer() {
        new Handler().post(new Runnable() {
            public void run() {
                //                // count down timer start, ditambah jumlah penalty
                new CountDownTimer(PENALTY_WAIT_MILLISECONDS + (penaltyCounter * 1000), 1000) {
                    public void onTick(long millisUntilFinished) {
                        long t = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                        btnSubmit.setText(getString(R.string.action_penalty, String.valueOf(t)));
                    }

                    public void onFinish() {
                        btnSubmit.setText(getString(R.string.action_submit));

                        btnSubmit.setEnabled(true);
                    }
                }.start();
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        final AlertDialog alertDialog = Util.showProgressDialog(this);
        FBUtil.Mitra_GetInfoPriceRef(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        alertDialog.dismiss();

                        if (!dataSnapshot.exists()) {
                            return;
                        }

                        PriceInfo priceInfo = dataSnapshot.getValue(PriceInfo.class);
                        buffer = priceInfo.getInfo();
                        etMemo.setText(buffer);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        alertDialog.dismiss();
                        Toast.makeText(ActivityPriceInfo.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_info);


        mobileSetup = MitraUtil.getMobileSetup();
        int maxInfoPriceChars = mobileSetup.getMaxInfoPriceChars();
        if (maxInfoPriceChars < 1)
            maxInfoPriceChars = 600;

        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(userFullName);
//            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle(TAG);

            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(mobileSetup.getTheme_color_default())));

        }
        etMemo = findViewById(R.id.etMemo);
        etMemo.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(maxInfoPriceChars) });

        tvFooterInfo = findViewById(R.id.tvFooterInfo);
        tvFooterInfo.setText(getString(R.string.message_footer_info_price_customer) + "\nMax Karakter: " + maxInfoPriceChars);

        btnSubmit = findViewById(R.id.btnSubmit);
        final int finalMaxInfoPriceChars = maxInfoPriceChars;
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetUtil.shownMessageWhenOffline(ActivityPriceInfo.this)) {
                    return;
                }

                final String memo = Util.trimString(etMemo.getText().toString().trim(), finalMaxInfoPriceChars);

                // kalo kosong atau sama dengan buffer ga perlu submit
                if (memo.trim().length() < 1 || buffer.equals(memo)) {
                    return;
                }

                Util.showDialogConfirmation(ActivityPriceInfo.this, getString(R.string.title_confirm), getString(R.string.message_confirm_submit), new ListenerPositiveConfirmation() {
                    @Override
                    public void onPositive() {

                        penaltyCounter += 1;

                        if (penaltyCounter >= 0) {
                            btnSubmit.setEnabled(false);
                            startTimer();
                        }

                        PriceInfo pi = new PriceInfo();
                        pi.setUpdatedTimestamp(new Date().getTime());
                        pi.setInfo(etMemo.getText().toString());
                        pi.setMitraId(mAuth.getCurrentUser().getUid());

                        FBUtil.Mitra_GetInfoPriceRef(mAuth.getCurrentUser().getUid())
                                .setValue(pi)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<Void> task) {
                                                               if (!task.isSuccessful()) {
                                                                   Util.showErrorDialog(ActivityPriceInfo.this, null, task.getException().getMessage());
                                                                   return;
                                                               }

                                                               buffer = memo;

                                                               Util.showDialog(ActivityPriceInfo.this, null, getString(R.string.message_success));

                                                           }
                                                       }
                                );

                    }
                });
            }
        });
    }
}
