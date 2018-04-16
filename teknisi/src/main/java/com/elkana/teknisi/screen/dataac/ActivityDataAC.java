package com.elkana.teknisi.screen.dataac;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.elkana.dslibrary.firebase.FBUtil;
import com.elkana.dslibrary.listener.ListenerModifyData;
import com.elkana.dslibrary.util.Util;
import com.elkana.teknisi.AFirebaseTeknisiActivity;
import com.elkana.teknisi.R;
import com.elkana.teknisi.pojo.IsiDataAC;
import com.elkana.teknisi.util.TeknisiUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ActivityDataAC extends AFirebaseTeknisiActivity {
    private static final String TAG = ActivityDataAC.class.getSimpleName();

    public static final String PARAM_ASSIGNMENT_ID = "assignment.id";
    public static final String PARAM_TECHNICIAN_ID = "tech.id";

    private String mTechnicianId, mAssignmentId;

    private String lastScanContent;
    private String lastScanFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_ac);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setSubtitle(userFullName);
//            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_activity_data_ac));
        }

        mAssignmentId = getIntent().getStringExtra(PARAM_ASSIGNMENT_ID);
        mTechnicianId = getIntent().getStringExtra(PARAM_TECHNICIAN_ID);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptToSubmitData();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

    }

    private void attemptToSubmitData() {

        // validate

        FragmentDataAC fragment = (FragmentDataAC) getSupportFragmentManager().findFragmentById(R.id.fragmentDataAC);

        IsiDataAC data = fragment.buildData();

        if (data.getNotes().length() > 160) {
            Toast.makeText(this, "Notes maksimal 160 karakter", Toast.LENGTH_SHORT).show();
            return;
        }

        if (data.getTipeAC().length() > 50) {
            Toast.makeText(this, "Tipe AC maksimal 50 karakter", Toast.LENGTH_SHORT).show();
            return;
        }

        data.setScanContent(lastScanContent);
        data.setScanFormat(lastScanFormat);

        Toast.makeText(this, "Submit data" + data, Toast.LENGTH_LONG).show();

        final AlertDialog alertDialog = Util.showProgressDialog(this, "Submit data AC...");

        TeknisiUtil.Assignment_addDataAC(mTechnicianId, mAssignmentId, data, new ListenerModifyData() {
            @Override
            public void onSuccess() {
                alertDialog.dismiss();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(Exception e) {
                alertDialog.dismiss();
                Log.e(TAG, e.getMessage(), e);
                Toast.makeText(ActivityDataAC.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_data_ac, menu);

        Drawable drawable = menu.findItem(R.id.action_qrcode).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, android.R.color.white));
        menu.findItem(R.id.action_qrcode).setIcon(drawable);

        drawable = DrawableCompat.wrap(menu.findItem(R.id.action_barcode).getIcon());
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, android.R.color.white));
        menu.findItem(R.id.action_barcode).setIcon(drawable);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_qrcode) {

            lastScanContent = null;
            lastScanFormat = null;

            IntentIntegrator i = new IntentIntegrator(this);
            i.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            i.setPrompt("Scan QRCode");
            i.setBeepEnabled(false);
// i.setCameraId(0);                // ga ngefek ?
// i.setBarcodeImageEnabled(true);    // ga ngefek jd ga tau buat apaan
            i.initiateScan();

            return true;
        } else if (id == R.id.action_barcode) {
            return true;
        } else if (id == R.id.action_reset) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
//we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            // do anywant
//            Toast.makeText(this, "content=" + scanContent + "\nscanFormat=" + scanFormat, Toast.LENGTH_LONG).show();
            lastScanContent = scanContent;
            lastScanFormat = scanFormat;
        }else{
            //No scan data received!
            Toast.makeText(this, "No code received", Toast.LENGTH_SHORT).show();
        }

    }
}
