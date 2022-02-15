package com.safaltaclass.plus.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class SafaltaPlusMigrationManager {

    private final static String KEY_PREFERENCES_VERSION = "key_preferences_version";
    private final static int PREFERENCES_VERSION = 1;


    public static void migrate(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SafaltaPlusPreferences.PREFS_NAME, Context.MODE_PRIVATE);
        checkPreferences(preferences);
    }

    private static void checkPreferences(SharedPreferences thePreferences) {
        final double oldVersion = thePreferences.getInt(KEY_PREFERENCES_VERSION, 1);

        if (oldVersion < PREFERENCES_VERSION) {
            final SharedPreferences.Editor edit = thePreferences.edit();
            edit.clear();
            edit.putInt(KEY_PREFERENCES_VERSION, PREFERENCES_VERSION);
            edit.commit();
        }
    }
}
