package com.devStudy.rabbitmq.simpleQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class recv {
    private static final String QUEUE_NAME1 = "hello";
    private  static final String QUEUE_NAME2 = "helloDurable";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME1, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press Ctrl+C");

        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received from " + QUEUE_NAME1 + " '" + message + "'");
        };
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received from " + QUEUE_NAME2 + " '" + message + "'");
        };

        channel.basicConsume(QUEUE_NAME1, true, deliverCallback1, consumerTag -> { });
        channel.basicConsume(QUEUE_NAME2, true, deliverCallback2, consumerTag -> { });
    }
}
