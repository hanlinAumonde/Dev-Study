package com.devStudy.java.threadPool.MyThreadPool;

import com.devStudy.java.threadPool.MyThreadPool.RejectHandle.RejectHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MyThreadPool {

    private final int corePoolSize;
    private final int maxPoolSize;
    private final int timeout;
    private final TimeUnit timeUnit;
    private final BlockingQueue<Runnable> blockingQueue;
    private final RejectHandler rejectHandler;

    public MyThreadPool(int corePoolSize, int maxPoolSize, int timeout, TimeUnit timeUnit, BlockingQueue<Runnable> blockingQueue, RejectHandler rejectHandler){
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.blockingQueue = blockingQueue;
        this.rejectHandler = rejectHandler;
    }

    public BlockingQueue<Runnable> getBlockingQueue() {
        return blockingQueue;
    }

    //BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(1024);

    List<Thread> coreList = Collections.synchronizedList(new ArrayList<>());
    List<Thread> supportList = Collections.synchronizedList(new ArrayList<>());

    public synchronized void execute(Runnable runnable){
        if(coreList.size() < corePoolSize){
            Thread coreThread = new CoreThread();
            coreList.add(coreThread);
            coreThread.start();
        }
        if(blockingQueue.offer(runnable)){
            return;
        }
        if(coreList.size() + supportList.size() < maxPoolSize){
            Thread supportThread = new SupportThread();
            supportList.add(supportThread);
            supportThread.start();
        }
        if(!blockingQueue.offer(runnable)){
            rejectHandler.reject(runnable, this);
        }
    }

    class CoreThread extends Thread {
        @Override
        public void run() {
            while(true) {
                try {
                    Runnable command = blockingQueue.take();
                    command.run();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    class SupportThread extends Thread {
        @Override
        public void run() {
            while(true) {
                try {
                    Runnable command = blockingQueue.poll(timeout, timeUnit);
                    if(command == null){
                        //这里是否需要删除supportlist中对应的辅助线程？
                        break;
                    }
                    command.run();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            System.out.println("This support thread" + Thread.currentThread().getName() + " has ended!");
        }
    }
}
