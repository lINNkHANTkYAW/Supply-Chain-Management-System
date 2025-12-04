package com.example.SupplyChainManagement.dto;

import java.time.LocalDateTime;

public class ChatMessageResponse {
	private Long messageId;
	private Long senderId;
	private String senderName;
	private String senderProfileImage;
	private String text;
	private Long unreadCount;
	private LocalDateTime timestamp;
	private String imageUrl;

	public ChatMessageResponse(Long senderId, String senderName, String senderProfileImage, String text,
			Long unreadCount) {
		this.senderId = senderId;
		this.senderName = senderName;
		this.senderProfileImage = senderProfileImage;
		this.text = text;
		this.unreadCount = unreadCount;
	}

	public ChatMessageResponse(Long messageId, Long senderId, String senderName, String senderProfileImage, String text,
			LocalDateTime timestamp) {
		this.messageId = messageId;
		this.senderId = senderId;
		this.senderName = senderName;
		this.senderProfileImage = senderProfileImage;
		this.text = text;
		this.timestamp = timestamp;

	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public ChatMessageResponse(Long senderId, String senderName, String senderProfileImage, String text,
			LocalDateTime timestamp) {
		this.senderId = senderId;
		this.senderName = senderName;
		this.senderProfileImage = senderProfileImage;
		this.text = text;
		this.timestamp = timestamp;

	}

	public ChatMessageResponse(Long messageId, Long senderId, String senderName, String senderProfileImage, String text,
			String imageUrl, LocalDateTime timestamp) {
		this.messageId = messageId;
		this.senderId = senderId;
		this.senderName = senderName;
		this.senderProfileImage = senderProfileImage;
		this.text = text;
		this.imageUrl = imageUrl; // ✅ Store image URL
		this.timestamp = timestamp;
	}

	public ChatMessageResponse(Long senderId, String senderName, String senderProfileImage, String text,
			String imageUrl, int unreadCount) {
		this.senderId = senderId;
		this.senderName = senderName;
		this.senderProfileImage = senderProfileImage;
		this.text = text;
		this.imageUrl = imageUrl;
		this.unreadCount = (long) unreadCount;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
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

	public String getSenderProfileImage() {
		return senderProfileImage;
	}

	public void setSenderProfileImage(String senderProfileImage) {
		this.senderProfileImage = senderProfileImage;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(Long unreadCount) {
		this.unreadCount = unreadCount;
	}

	// Getters and Setters
}
