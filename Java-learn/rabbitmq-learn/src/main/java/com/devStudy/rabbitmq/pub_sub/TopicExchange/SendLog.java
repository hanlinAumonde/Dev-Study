package com.devStudy.rabbitmq.pub_sub.TopicExchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class SendLog {
    private static final String EXCHANGE_NAME = "logsTopic";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            List<String> loglevels = List.of("kern.info", "user.warning", "user.error", "kern.info", "user.info", "kern.error", "kern.warning", "user.info", "kern.info", "user.warning");
            loglevels.forEach(severity -> {
                String message = "This is a [" + severity + "] msg";
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
