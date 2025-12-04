package com.example.SupplyChainManagement.dto;

public class PaymentMethodDTO {
    private Long payMethodId;
    private String payMethodName;

    // No-argument constructor (required for JSON serialization/deserialization)
    public PaymentMethodDTO() {}

    // All-argument constructor
    public PaymentMethodDTO(Long payMethodId, String payMethodName) {
        this.payMethodId = payMethodId;
        this.payMethodName = payMethodName;
    }

    // Getters and Setters
    public Long getPayMethodId() {
        return payMethodId;
    }

    public void setPayMethodId(Long payMethodId) {
        this.payMethodId = payMethodId;
    }

    public String getPayMethodName() {
        return payMethodName;
    }

    public void setPayMethodName(String payMethodName) {
        this.payMethodName = payMethodName;
    }
}