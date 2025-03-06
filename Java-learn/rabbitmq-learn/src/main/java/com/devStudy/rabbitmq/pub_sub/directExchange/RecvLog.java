package com.devStudy.rabbitmq.pub_sub.directExchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.List;

public class RecvLog {
    private static final String EXCHANGE_NAME = "logsDirect";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        Thread t1 = new Thread(() -> {
            try {
                doWork(channel, "Worker1", List.of("info"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                doWork(channel, "Worker2", List.of("error", "warning"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }

    private static void doWork(Channel channel, String worker, List<String> severity) throws Exception {
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        String queueName = channel.queueDeclare().getQueue();
        //channel.queueBind(queueName, EXCHANGE_NAME, "");
        for(String s : severity){
            channel.queueBind(queueName, EXCHANGE_NAME, s);
        }

        System.out.println(" [*] " + worker + " Waiting for messages");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] " + worker + " Received '" + (severity.size()==1? severity : message + "'"));
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}
