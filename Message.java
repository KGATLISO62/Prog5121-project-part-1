/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplicationproject.pkg1;

import java.util.Scanner; 
import java.util.Random;  
/**
 *
 * @author kgatl
 */
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Message {

    private static int totalMessagesSent = 0;

    private String messageID;
    private int messageNumber;
    private String recipient;
    private String message;
    private String messageHash;

    private static final Scanner ista = new Scanner(System.in);
    private static final Random random = new Random();

    public Message(String messageID, int messageNumber,
                   String recipient, String message) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.message = message;
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    public String checkRecipientCell() {
        if (recipient != null && recipient.matches("^\\+27[0-9]{9}$")) {
            return "Cell phone number successfully captured.";
        }

        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    public String checkMessageLength() {
        if (message != null && message.length() < 250) {
            return "Message ready to send.";
        }

        int extraCharacters = message == null
                ? 250 : message.length() - 249;

        return "Message exceeds the allowed length by "
                + extraCharacters + " characters.";
    }

    public String createMessageHash() {
        String[] words = message.trim().split("\\s+");

        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 0
                ? words[words.length - 1] : "";

        messageHash = (messageID.substring(0, 2) + ":"
                + messageNumber + ":" + firstWord + lastWord)
                .toUpperCase();

        return messageHash;
    }

    public String SentMessage(int choice) {
        if (choice == 1) {
            totalMessagesSent++;
            return "Message successfully sent.";
        } else if (choice == 2) {
            return storeMessage();
        } else if (choice == 3) {
            return "Message successfully discarded.";
        }

        return "Invalid option.";
    }

    public String printMessages() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + message;
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    public static String generateMessageID() {
        long number = 1000000000L + random.nextInt(900000000);
        return String.valueOf(number);
    }

    public String storeMessage() {
        ArrayList<String> messages = new ArrayList<>();

        try {
            String fileName = "messages.json";

            if (Files.exists(Paths.get(fileName))) {
                String content = new String(
                        Files.readAllBytes(Paths.get(fileName))).trim();

                if (content.startsWith("[") && content.endsWith("]")) {
                    String existing = content.substring(
                            1, content.length() - 1).trim();

                    if (!existing.isEmpty()) {
                        messages.add(existing);
                    }
                }
            }

            String safeMessage = message
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");

            String newMessage = "{"
                    + "\"messageID\":\"" + messageID + "\","
                    + "\"messageHash\":\"" + messageHash + "\","
                    + "\"recipient\":\"" + recipient + "\","
                    + "\"message\":\"" + safeMessage + "\""
                    + "}";

            FileWriter writer = new FileWriter(fileName);
            writer.write("[\n");

            for (String oldMessage : messages) {
                writer.write(oldMessage + ",\n");
            }

            writer.write(newMessage + "\n]");
            writer.close();

            return "Message successfully stored.";

        } catch (IOException e) {
            return "Error storing message.";
        }
    }

    public static void main(String[] args) {
        displayInfo();
    }

    public static void displayInfo() {
        int option;

        do {
            System.out.println("\nWelcome to QuickChat");
            System.out.println("1. Send Messages");
            System.out.println("2. Coming Soon");
            System.out.println("3. Quit");
            System.out.print("Choose an option: ");

            while (!ista.hasNextInt()) {
                System.out.println("Please enter a number.");
                ista.next();
            }

            option = ista.nextInt();
            ista.nextLine();

            switch (option) {
                case 1:
                    sendMessage();
                    break;
                case 2:
                    System.out.println("Coming soon.");
                    break;
                case 3:
                    System.out.println("Thank you for using QuickChat.");
                    break;
                default:
                    System.out.println("Invalid option chosen.");
            }

        } while (option != 3);
    }

    public static void sendMessage() {
        System.out.print("Enter the number of messages: ");

        while (!ista.hasNextInt()) {
            System.out.println("Please enter a number.");
            ista.next();
        }

        int count = ista.nextInt();
        ista.nextLine();

        for (int i = 0; i < count; i++) {
            String id = generateMessageID();
            System.out.println("Message ID: " + id);

            System.out.print("Enter recipient number (+27821234567): ");
            String recipient = ista.nextLine();

            Message temp = new Message(id, i + 1, recipient, "");

            if (!temp.checkRecipientCell().startsWith(
                    "Cell phone number successfully")) {
                System.out.println(temp.checkRecipientCell());
                continue;
            }

            System.out.print("Write a message (under 250 characters): ");
            String text = ista.nextLine();

            Message msg = new Message(id, i + 1, recipient, text);

            if (!msg.checkMessageLength().equals("Message ready to send.")) {
                System.out.println(msg.checkMessageLength());
                continue;
            }

            System.out.println("Message hash: " + msg.createMessageHash());
            System.out.println("Choose: 1. Send  2. Store  3. Discard");

            while (!ista.hasNextInt()) {
                System.out.println("Please enter 1, 2, or 3.");
                ista.next();
            }     

            int choice = ista.nextInt();
            ista.nextLine();

            System.out.println(msg.SentMessage(choice));

            if (choice == 1) {
                System.out.println(msg.printMessages());
            }
        }

        System.out.println("Total messages sent: " + returnTotalMessages());
    }
}