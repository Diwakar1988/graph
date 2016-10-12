package com.github.diwakar1988.easytrain;

import android.app.Application;

import com.github.diwakar1988.easytrain.util.BackgroundExecutor;

/**
 * Created by diwakar.mishra on 05/10/16.
 */

public class EasyMetroApp extends Application {
    private static final int SIZE_THREAD_POOL = 5;
    private static EasyMetroApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        BackgroundExecutor.init(SIZE_THREAD_POOL);
    }


    public static EasyMetroApp getInstance() {
        return instance;
    }
}
