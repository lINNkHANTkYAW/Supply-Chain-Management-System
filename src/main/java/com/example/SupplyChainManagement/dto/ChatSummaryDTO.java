package com.example.SupplyChainManagement.dto;

import java.util.List;

import com.example.SupplyChainManagement.model.ChatMessage;

public class ChatSummaryDTO {
    private Long senderId;
    private String senderName;
    private List<ChatMessage> messages;

    // Getters and setters
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }
}