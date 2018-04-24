package com.elkana.ds.mitraapp;

import android.app.Application;

import com.elkana.ds.mitraapp.util.MitraUtil;
import com.elkana.dslibrary.MyLibraryModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Eric on 20-Oct-17.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //enable offline: tp bahaya ternyata, node yg harusnya ga ada malah msh dianggap ada. kejadian di multiple device
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

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

//        MitraUtil.initiateOfflineData();
    }
}
