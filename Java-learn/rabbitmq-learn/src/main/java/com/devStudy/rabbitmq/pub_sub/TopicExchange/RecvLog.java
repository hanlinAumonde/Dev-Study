package com.devStudy.rabbitmq.pub_sub.TopicExchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.List;

public class RecvLog {
    private static final String EXCHANGE_NAME = "logsTopic";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        Thread t1 = new Thread(() -> {
            try {
                doWork(channel, "Worker1", List.of("#"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                doWork(channel, "Worker2", List.of("user.*"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread t3 = new Thread(() -> {
            try {
                doWork(channel, "Worker3", List.of("*.error"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread t4 = new Thread(() -> {
            try {
                doWork(channel, "Worker4", List.of("kern.*"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();
    }

    private static void doWork(Channel channel, String worker, List<String> severity) throws Exception {
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();
        //channel.queueBind(queueName, EXCHANGE_NAME, "");
        for(String s : severity){
            channel.queueBind(queueName, EXCHANGE_NAME, s);
        }

        System.out.println(" [*] " + worker + " Waiting for messages");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] " + worker + " Received '" + message + ", with routing key: " + severity);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}
