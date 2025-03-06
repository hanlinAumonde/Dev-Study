package com.devStudy.rabbitmq.workQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class recvTask {
    private static final String QUEUE_NAME = "taskQueue";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory1 = new ConnectionFactory();
        factory1.setHost("localhost");

        Connection connection = factory1.newConnection();
        Channel channel1 = connection.createChannel();
        Channel channel2 = connection.createChannel();
        Thread worker1 = new Thread(new Worker(channel1, "Worker1"));
        worker1.start();
        Thread worker2 = new Thread(new Worker(channel2, "Worker2"));
        worker2.start();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(" [!] Forcibly interrupting Worker2");
                try {
                    channel2.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
                worker2.interrupt();// forcibly interrupt Worker2, will throw an error
            }
        }, 1200);

        worker1.join();
    }

    static class Worker implements Runnable {
        private final Channel channel;
        private final String name;
        private final AtomicInteger count = new AtomicInteger(0);
        private final AtomicLong lastDeliveryTag = new AtomicLong(0);
        private final AtomicLong lastAckTime = new AtomicLong(System.currentTimeMillis());
        private static final int MAX_TIMEOUT = 5000;
        private Timer timer = new Timer(true);

        public Worker(Channel connection, String name) throws IOException {
            this.channel = connection;
            this.channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            this.channel.basicQos(2);
            this.name = name;

            this.timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run(){
                    checkAndAck();
                }
            }, MAX_TIMEOUT, MAX_TIMEOUT);
        }

        private synchronized void checkAndAck(){
            long now = System.currentTimeMillis();
            if(lastDeliveryTag.get() > 0 && count.get() > 0 && (now - lastAckTime.get()) >= MAX_TIMEOUT){
                try {
                    channel.basicAck(lastDeliveryTag.get(), true);
                    System.out.println("Timeout ack: confirmed " + count.get() + " messages up to delivery tag : " + lastDeliveryTag);
                    lastAckTime.set(now);
                    count.set(0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void run() {
            try {
                doWork();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void doWork() throws Exception {
            System.out.println(" [*]" + this.name + " Waiting for messages.");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x]" + this.name + " Received '" + message + "'");
                try {
                    for (char ch : message.toCharArray()) {
                        if (ch == '.') {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException _ignored) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }catch (Exception e) {
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                    e.printStackTrace();
                }finally {
                    lastDeliveryTag.set(delivery.getEnvelope().getDeliveryTag());
                    if(count.incrementAndGet() % 2 == 0){
                        channel.basicAck(lastDeliveryTag.get(), true);
                        System.out.println("Batch ack: confirmed " + count.get() + " messages up to delivery tag : " + lastDeliveryTag);
                        count.set(0);
                        lastAckTime.set(System.currentTimeMillis());
                    }
                    System.out.println(" [x]" + this.name + " Done");
                }
            };
            channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
        }
    }
}
