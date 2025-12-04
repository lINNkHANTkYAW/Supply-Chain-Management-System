package com.example.SupplyChainManagement.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatListResponse {
    private Long senderId;
    private String senderName;
    private String senderProfileImg;
    private String lastMessageText;
    private LocalDateTime lastMessageTimestamp;
    private Long unreadCount;

    public ChatListResponse(Long senderId, String senderName, String senderProfileImg, String lastMessageText, LocalDateTime lastMessageTimestamp, Long unreadCount) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderProfileImg = senderProfileImg;
        this.lastMessageText = lastMessageText;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.unreadCount = unreadCount;
    }

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderProfileImg() {
		return senderProfileImg;
	}

	public void setSenderProfileImg(String senderProfileImg) {
		this.senderProfileImg = senderProfileImg;
	}

	public String getLastMessageText() {
		return lastMessageText;
	}

	public void setLastMessageText(String lastMessageText) {
		this.lastMessageText = lastMessageText;
	}

	public LocalDateTime getLastMessageTimestamp() {
		return lastMessageTimestamp;
	}

	public void setLastMessageTimestamp(LocalDateTime lastMessageTimestamp) {
		this.lastMessageTimestamp = lastMessageTimestamp;
	}

	public Long getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(Long unreadCount) {
		this.unreadCount = unreadCount;
	}
}