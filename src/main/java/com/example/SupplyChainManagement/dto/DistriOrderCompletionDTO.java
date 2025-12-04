package com.example.SupplyChainManagement.dto;

public class DistriOrderCompletionDTO {
    private String status;
    private Integer count;

    public DistriOrderCompletionDTO(String status, Integer count) {
        this.status = status;
        this.count = count;
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
}