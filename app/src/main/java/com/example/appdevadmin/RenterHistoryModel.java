package com.example.appdevadmin;

public class RenterHistoryModel {
    private String tenantName;
    private String leaseRange; // To store "04/07/2026 - 05/06/2026"
    private String status;     // To store "Current" or "Past"

    public RenterHistoryModel(String tenantName, String leaseRange, String status) {
        this.tenantName = tenantName;
        this.leaseRange = leaseRange;
        this.status = status;
    }

    public String getTenantName() { return tenantName; }
    public String getLeaseRange() { return leaseRange; }
    public String getStatus() { return status; }
}