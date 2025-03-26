package models;

import java.time.LocalDateTime;

/**
 * Message class representing a message in a consultation
 */
public class Message {
    private int id;
    private int senderId;
    private int receiverId;
    private String content;
    private LocalDateTime timestamp;

    /**
     * Constructor for Message class
     * 
     * @param id Message ID
     * @param senderId ID of the sender
     * @param receiverId ID of the receiver
     * @param content Content of the message
     * @param timestamp Time the message was sent
     */
    public Message(int id, int senderId, int receiverId, String content, LocalDateTime timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
    }
    
    /**
     * Constructor with current timestamp
     */
    public Message(int id, int senderId, int receiverId, String content) {
        this(id, senderId, receiverId, content, LocalDateTime.now());
    }

    // Getters
    public int getId() { return id; }
    public int getSenderId() { return senderId; }
    public int getReceiverId() { return receiverId; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    /**
     * Display message information
     */
    public void displayInfo() {
        System.out.println("[" + timestamp + "] From User #" + senderId + " to User #" + receiverId + ": " + content);
    }
}