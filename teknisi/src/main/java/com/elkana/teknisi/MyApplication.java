package com.elkana.teknisi;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.elkana.dslibrary.MyLibraryModule;
import com.elkana.teknisi.util.TeknisiUtil;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Eric on 20-Oct-17.
 */

public class MyApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        /*

//        https://academy.realm.io/posts/secure-storage-in-android-san-francisco-android-meetup-2017-najafzadeh/
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("myrealm.realm")
//                .encryptionKey(key)   https://github.com/realm/realm-java/blob/master/examples/encryptionExample/src/main/java/io/realm/examples/encryptionexample/EncryptionExampleActivity.java
                .build();
        Realm.setDefaultConfiguration(config);
        */
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("mitra.realm")
                .modules(Realm.getDefaultModule(), new MyLibraryModule())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

//        TeknisiUtil.initiateOfflineData();
    }
}
