package com.github.diwakar1988.easytrain.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by diwakar.mishra on 06/10/16.
 */
public class BackgroundExecutor {
    private Executor executor;
    private static BackgroundExecutor instance;

    public static synchronized void init(int sizeThreadPool){
        instance = new BackgroundExecutor(sizeThreadPool);
    }
    private BackgroundExecutor(int sizeThreadPool) {
        executor = Executors.newFixedThreadPool(sizeThreadPool);
    }

    public static BackgroundExecutor getInstance() {
        return instance;
    }

    public void run(Runnable runnable) {
        executor.execute(runnable);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return getInstance();
    }
    public void stop(){
//        executor.shutdownNow();
    }

}
