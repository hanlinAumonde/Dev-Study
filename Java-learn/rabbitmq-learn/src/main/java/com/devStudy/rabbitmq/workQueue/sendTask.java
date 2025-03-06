package com.devStudy.rabbitmq.workQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.List;

public class sendTask {
    private static final String QUEUE_NAME = "taskQueue";
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()){
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            List<String> msgs = List.of("-First message.", "-Second message..", "-Third message...", "Fourth message....", "Fifth message.....");
            msgs.forEach(msg -> {
                try {
                    channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(" [x] Sent '" + msg + "'");
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
