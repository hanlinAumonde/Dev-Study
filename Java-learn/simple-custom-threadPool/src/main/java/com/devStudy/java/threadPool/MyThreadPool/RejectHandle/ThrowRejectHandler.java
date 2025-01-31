package com.devStudy.java.threadPool.MyThreadPool.RejectHandle;

import com.devStudy.java.threadPool.MyThreadPool.MyThreadPool;

public class ThrowRejectHandler implements RejectHandler {
    @Override
    public void reject(Runnable runnable, MyThreadPool myThreadPool) {
        throw new RuntimeException("The blocking queue is fulfilled");
    }
}
