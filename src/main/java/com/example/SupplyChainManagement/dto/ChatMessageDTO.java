package com.example.SupplyChainManagement.dto;

import java.time.LocalDateTime;

import com.example.SupplyChainManagement.model.ChatMessage;

public class ChatMessageDTO {
    private Long messageId;
    private Long senderId;
    private Long receiverId;
    private String text;
    private String imageUrl;
    private LocalDateTime timestamp;
    private boolean read;

    // Constructors
    public ChatMessageDTO(ChatMessage message) {
        this.messageId = message.getMessageId();
        this.senderId = message.getSender().getUserId();
        this.receiverId = message.getReceiver().getUserId();
        this.text = message.getText();
        this.imageUrl = message.getImageUrl();
        this.timestamp = message.getTimestamp();
        this.read = message.isRead();
    }

    // Getters and Setters
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}