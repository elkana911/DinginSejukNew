package com.elkana.dslibrary.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Eric on 19-Oct-17.
 */

public class Storage {
    public static String getLanguageId(Context ctx) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String langId = sharedPrefs.getString("language", "id"); //id / en

        return langId;

    }

    public static Lang getLanguage(Context ctx) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String langId = sharedPrefs.getString("language", "id"); //id / en

        for (Lang l : Lang.values()) {
            if (l.getCode().equals(langId)) {
                return l;
            }
        }
        return null;

    }

}
