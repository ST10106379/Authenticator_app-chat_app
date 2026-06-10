package com.mycompany.authenticatorapp;

import java.util.Scanner;

/**
 * QuickChat - post-login menu and message-sending flow.
 * Called from Authenticator_App after a successful login.
 */
public class Quickchat {

    /** Entry point - call this from Authenticator_App once the user has logged in. */
    public static void run(Scanner scanner) {
        // Welcome message (assignment requirement #2)
        System.out.println("Welcome to QuickChat.");

        boolean running = true;
        while (running) {
            int choice = showMenu(scanner);
            switch (choice) {
                case 1:
                    sendMessagesFlow(scanner);
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1, 2, or 3.");
            }
        }

        // Total accumulated messages on exit (assignment requirement #6)
        System.out.println("\nTotal messages sent this session: " + Message.returnTotalMessages());
        System.out.println("Goodbye!");
    }

    private static int showMenu(Scanner scanner) {
        System.out.println("\n--- QuickChat Menu ---");
        System.out.println("1) Send Messages");
        System.out.println("2) Show recently sent messages");
        System.out.println("3) Quit");
        System.out.print("Enter choice: ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Asks how many messages, loops, validates, builds, asks action, displays details. */
    private static void sendMessagesFlow(Scanner scanner) {
        // Requirement #5 - user defines how many messages to enter
        System.out.print("How many messages do you want to send? ");
        int numMessages;
        try {
            numMessages = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number - returning to menu.");
            return;
        }
        if (numMessages <= 0) {
            System.out.println("Please enter a positive number.");
            return;
        }

        for (int i = 0; i < numMessages; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");

            // Recipient (with international code)
            String recipient;
            while (true) {
                System.out.print("Recipient cell number (e.g. +27718693002): ");
                recipient = scanner.nextLine().trim();
                String result = Message.checkRecipientCell(recipient);
                System.out.println(result);
                if (result.equals("Cell phone number successfully captured.")) break;
            }

            // Message body (max 250 chars)
            String body;
            while (true) {
                System.out.print("Enter your message (max 250 characters): ");
                body = scanner.nextLine();
                String validate = Message.validateMessageLength(body);
                System.out.println(validate);
                if (validate.equals("Message ready to send.")) break;
            }

            // Build the message (auto-generates ID, number and hash)
            Message msg = new Message(recipient, body);

            // Ask the user what to do with it
            System.out.println("\nWhat would you like to do with this message?");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Choice: ");
            int action;
            try {
                action = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                action = -1;
            }

            String response = msg.sentMessage(action);
            System.out.println(response);

            // Requirement #7 - display full details after sending
            if (action == 1) {
                System.out.println("\n--- Message Details ---");
                System.out.println("Message ID:   " + msg.getMessageID());
                System.out.println("Message Hash: " + msg.getMessageHash());
                System.out.println("Recipient:    " + msg.getRecipient());
                System.out.println("Message:      " + msg.getMessageBody());
            }
        }

        System.out.println("\nMessages sent so far: " + Message.returnTotalMessages());
    }
}