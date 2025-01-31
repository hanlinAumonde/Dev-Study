package com.devStudy.java.threadPool.MyThreadPool.RejectHandle;

import com.devStudy.java.threadPool.MyThreadPool.MyThreadPool;

public class RandomDiscardRejectHandler implements RejectHandler {
    @Override
    public void reject(Runnable runnable, MyThreadPool myThreadPool) {
        myThreadPool.getBlockingQueue().poll();
        myThreadPool.execute(runnable);
    }
}
