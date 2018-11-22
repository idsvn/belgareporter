package be.belga.reporter.mobile.reporter.persistantStorage;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class PersistantStorageManager {

    private static int schemaVersion = 5;

    public static void configureDB(Context context) {
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("reporter.realm")
                .schemaVersion(schemaVersion)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

}
