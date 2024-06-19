package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
    public static void main(String[] args) {
        new Client().start("localhost", 12345);
    }

    public void start(String address, int port) {
        try {
            ConnectionThread thread = new ConnectionThread(address, port);
            thread.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in)
            );

            System.out.println("Enter your login:");

            String login = reader.readLine();
            thread.login(login);

            while (true) {
                String rawMessage = reader.readLine();
                if (rawMessage.startsWith("/w ")) {
                    int firstSpaceIndex = rawMessage.indexOf(' ', 3);
                    if (firstSpaceIndex == -1) {
                        System.out.println("Invalid private message format. Use: /w recipient message");
                        continue;
                    }
                    String recipient = rawMessage.substring(3, firstSpaceIndex);
                    String messageContent = rawMessage.substring(firstSpaceIndex + 1);
                    Message message = new Message(MessageType.Private, messageContent, recipient);
                    thread.send(message);
                } else {
                    Message message = new Message(MessageType.Broadcast, rawMessage);
                    thread.send(message);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}