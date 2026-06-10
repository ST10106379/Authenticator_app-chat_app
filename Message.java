package com.mycompany.authenticatorapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * QuickChat - Message class.
 * Handles creation, validation, hashing, sending, storing and printing of messages.
 */
public class Message {

    // ----- Static state (shared across all messages in a session) -----
    private static int totalMessagesSent = 0;
    private static int messageCounter = 0;
    private static final List<Message> sentMessages = new ArrayList<>();

    public static void disableFileStorageForTests() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // ----- Instance state -----
    private final String messageID;     // Random 10-digit ID
    private final int messageNum;       // Sequential number (0, 1, 2, ...)
    private final String recipient;     // Cell number with country code
    private final String messageBody;   // The actual text
    private final String messageHash;   // Auto-generated hash
    private String flag;                // "Sent" / "Stored" / "Disregarded"

    /** Production constructor - generates a fresh random ID and uses the next counter value. */
    public Message(String recipient, String messageBody) {
        this(recipient, messageBody, generateMessageID(), messageCounter);
        messageCounter++;
    }

    /** Test/internal constructor - lets you control the messageID and number for predictable testing. */
    public Message(String recipient, String messageBody, String messageID, int messageNum) {
        this.recipient = recipient;
        this.messageBody = messageBody;
        this.messageID = messageID;
        this.messageNum = messageNum;
        this.messageHash = createMessageHash();
    }

    // ============ Method for ID GENERATION & VALIDATION ============

    private static String generateMessageID() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) sb.append(rand.nextInt(10));
        return sb.toString();
    }

    /** Returns true if the message ID is no more than 10 characters. */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    // ============ RECIPIENT VALIDATION ============

    /**
     * Validates that the recipient cell number starts with "+" (international code)
     * and contains only digits afterwards, with a reasonable max length.
     */
    public String checkRecipientCell() {
        return checkRecipientCell(this.recipient);
    }

    /** Static version - useful for validating input before constructing a Message. */
    public static String checkRecipientCell(String recipient) {
        if (recipient == null || recipient.isEmpty()) {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
        // Must start with "+" then digits only, total length up to 13 chars
        if (!recipient.matches("\\+\\d{1,12}")) {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
        return "Cell phone number successfully captured.";
    }

    // ============ MESSAGE LENGTH VALIDATION ============

    /** Validates message body length (max 250 characters). */
    public static String validateMessageLength(String message) {
        if (message == null) return "Message cannot be null.";
        if (message.length() <= 250) {
            return "Message ready to send.";
        }
        int excess = message.length() - 250;
        return "Message exceeds 250 characters by " + excess + ", please reduce the size.";
    }

    // ============ MESSAGE HASH ============

    /**
     * Creates a message hash in the format: XX:N:FIRSTWORDLASTWORD (all caps)
     * where XX = first two chars of message ID, N = message number,
     * and FIRSTWORD/LASTWORD are the first and last words (punctuation stripped).
     * Example: "00:0:HITONIGHT"
     */
    public String createMessageHash() {
        if (messageBody == null || messageBody.trim().isEmpty()) return "";
        String firstTwo = messageID.substring(0, 2);
        String[] words = messageBody.trim().split("\\s+");
        String first = words[0].replaceAll("[^a-zA-Z]", "");
        String last  = words[words.length - 1].replaceAll("[^a-zA-Z]", "");
        return (firstTwo + ":" + messageNum + ":" + first + last).toUpperCase();
    }

    // ============ SEND / STORE / DISREGARD ============

    /**
     * Handles the user's choice for what to do with the message.
     *  1 = Send Message       -> "Message successfully sent."
     *  2 = Disregard Message  -> "Press 0 to delete the message."
     *  3 = Store Message      -> "Message successfully stored." (also writes to JSON file)
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                this.flag = "Sent";
                sentMessages.add(this);
                totalMessagesSent++;
                return "Message successfully sent.";
            case 2:
                this.flag = "Disregarded";
                return "Press 0 to delete the message.";
            case 3:
                this.flag = "Stored";
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid selection.";
        }
    }

    // ============ PRINT / TOTAL ============

    /** Returns all messages that have been successfully sent during this run. */
    public static String printMessages() {
        if (sentMessages.isEmpty()) return "No messages have been sent yet.";
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append("Message ID: ").append(m.messageID).append('\n')
              .append("Message Hash: ").append(m.messageHash).append('\n')
              .append("Recipient: ").append(m.recipient).append('\n')
              .append("Message: ").append(m.messageBody).append("\n\n");
        }
        return sb.toString();
    }

    /** Returns the total number of messages successfully sent. */
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    // ============ JSON STORAGE ============

    /**
     * Stores this message as JSON in stored_messages.json.
     * Uses a small hand-written JSON serialiser so no external library is required.
     */
    public void storeMessage() {
        try {
            File file = new File("stored_messages.json");
            String entry = toJson();

            if (!file.exists() || file.length() == 0) {
                try (FileWriter fw = new FileWriter(file, false)) {
                    fw.write("[\n" + entry + "\n]");
                }
            } else {
                // Read existing, splice in the new entry before the closing ]
                StringBuilder existing = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) existing.append(line).append('\n');
                }
                String content = existing.toString().trim();
                if (content.endsWith("]")) {
                    content = content.substring(0, content.length() - 1).trim();
                    if (!content.endsWith("[")) content += ",";
                    content += "\n" + entry + "\n]";
                    try (FileWriter fw = new FileWriter(file, false)) {
                        fw.write(content);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to store message: " + e.getMessage());
        }
    }

    private String toJson() {
        return "  {\n" +
               "    \"messageID\": \"" + messageID + "\",\n" +
               "    \"messageNum\": " + messageNum + ",\n" +
               "    \"recipient\": \"" + escape(recipient) + "\",\n" +
               "    \"message\": \"" + escape(messageBody) + "\",\n" +
               "    \"messageHash\": \"" + messageHash + "\",\n" +
               "    \"flag\": \"" + (flag == null ? "" : flag) + "\"\n" +
               "  }";
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // ============ TEST HELPER ============

    /** Resets all static state - call this between unit tests. */
    public static void reset() {
        sentMessages.clear();
        totalMessagesSent = 0;
        messageCounter = 0;
    }

    // ============ GETTERS ============

    public String getMessageID()   { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient()   { return recipient; }
    public String getMessageBody() { return messageBody; }
    public int getMessageNum()     { return messageNum; }
    public String getFlag()        { return flag; }
}