package com.lerenard.catchphrase;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.lerenard.catchphrase.helper.DatabaseHandler;

import java.util.Random;

/**
 * Created by mc on 11-Jan-17.
 */

public class MainApplication extends Application {
    public static final String SHARED_PREFERENCES_FILENAME = "preferences";
    private static final String TAG = "MainApplication_";
    private static DatabaseHandler database;
            // not a memory leak because it will only hold application context.
    private static Context context;
    public static final Random random = new Random(System.currentTimeMillis());

    public static DatabaseHandler getDatabase() {
        return database;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        WordBank.initialize(context);
        database = new DatabaseHandler(context);
        Log.d(TAG, database.toString());
    }
}
