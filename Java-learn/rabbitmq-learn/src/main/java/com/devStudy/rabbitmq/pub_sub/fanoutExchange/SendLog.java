package com.devStudy.rabbitmq.pub_sub.fanoutExchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.time.LocalDateTime;

public class SendLog {
    private static final String EXCHANGE_NAME = "logsFanout";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            String message = LocalDateTime.now() + " [info]: Hello World!";
            for(int i = 0; i < 10; i++) {
                channel.basicPublish(EXCHANGE_NAME, "", null,
                        (message + " - num-" + i).getBytes("UTF-8"));
            }
            System.out.println(" [x] Sent '" + message + "'");
        }
    }
}
