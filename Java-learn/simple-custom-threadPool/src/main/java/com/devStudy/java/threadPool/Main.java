package com.devStudy.java.threadPool;

import com.devStudy.java.threadPool.MyThreadPool.MyThreadPool;
import com.devStudy.java.threadPool.MyThreadPool.RejectHandle.RandomDiscardRejectHandler;
import com.devStudy.java.threadPool.MyThreadPool.RejectHandle.ThrowRejectHandler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        //MyThreadPool myThreadPool = new MyThreadPool(2, 4, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2), new ThrowRejectHandler());
        MyThreadPool myThreadPool = new MyThreadPool(2, 4, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2), new RandomDiscardRejectHandler());

        for(int i=0;i<6;i++){
            myThreadPool.execute(()->{
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName());
            });
        }
        System.out.println("main function not block");
    }
}