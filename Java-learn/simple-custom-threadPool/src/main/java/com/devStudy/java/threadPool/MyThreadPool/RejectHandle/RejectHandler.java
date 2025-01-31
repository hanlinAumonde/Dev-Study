package com.devStudy.java.threadPool.MyThreadPool.RejectHandle;

import com.devStudy.java.threadPool.MyThreadPool.MyThreadPool;

public interface RejectHandler {
    void reject(Runnable runnable, MyThreadPool myThreadPool);
}
