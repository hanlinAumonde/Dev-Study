package com.devStudy.rabbitmq.pub_sub.directExchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class SendLog {
    private static final String EXCHANGE_NAME = "logsDirect";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            List<String> loglevels = List.of("info", "warning", "error", "info", "info", "error", "warning", "info", "info", "warning");
            loglevels.forEach(severity -> {
                String message = LocalDateTime.now() + " [" + severity + "]: Hello World!";
                try {
                    channel.basicPublish(EXCHANGE_NAME, severity, null,
                            message.getBytes("UTF-8"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }finally{
                    System.out.println(" [x] Sent '" + message + "'");
                }
            });
        }
    }
}
