package com.elkana.dslibrary.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.elkana.dslibrary.R;
import com.elkana.dslibrary.util.Storage;
import com.elkana.dslibrary.util.Util;

import java.util.Locale;

import dmax.dialog.SpotsDialog;
import io.realm.Realm;

/**
 * Created by Eric on 19-Oct-17.
 */

public class BasicActivity extends AppCompatActivity {
    static {
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    protected Typeface fontFace;
    protected Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fontFace = Typeface.createFromAsset(getAssets(),
                "fonts/DinDisplayProLight.otf");

        this.realm = Realm.getDefaultInstance();

        changeLocale(Storage.getLanguageId(this));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.realm != null)
            this.realm.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        /*else  if (id == R.id.action_settings) {
//            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    //http://www.sureshjoshi.com/mobile/changing-android-locale-programmatically/
    protected boolean changeLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return true;
    }

}
