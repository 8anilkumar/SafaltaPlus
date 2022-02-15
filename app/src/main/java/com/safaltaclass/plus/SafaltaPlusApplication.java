package com.safaltaclass.plus;

import android.app.Application;
import android.content.Context;

import java.io.File;

public class SafaltaPlusApplication extends Application {

    private static Context appContext;
    private static File downloadFile;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();


    }

    public static Context getAppContext() {
        return appContext;
    }

}
