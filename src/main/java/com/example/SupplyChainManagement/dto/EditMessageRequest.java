package com.example.SupplyChainManagement.dto;

public class EditMessageRequest {
    private Long messageId;
    private String text;
	public Long getMessageId() {
		return messageId;
	}
	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

    // Getters and Setters
}
