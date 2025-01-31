package com.devStudy.java.threadPool.MyThreadPool.RejectHandle;

import com.devStudy.java.threadPool.MyThreadPool.MyThreadPool;

public class CallerRunsRejectHandler implements RejectHandler{
    @Override
    public void reject(Runnable runnable, MyThreadPool myThreadPool) {
        runnable.run();
    }
}
