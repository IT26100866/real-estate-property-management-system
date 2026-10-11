package com.example.realestate.model;

public enum Role {
    USER("User", "/dashboard"),
    AGENT("Agent", "/agent/dashboard"),
    ADMIN("Admin", "/admin/dashboard");

    private final String displayName;
    private final String defaultDashboardUrl;

    Role(String displayName, String defaultDashboardUrl) {
        this.displayName = displayName;
        this.defaultDashboardUrl = defaultDashboardUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultDashboardUrl() {
        return defaultDashboardUrl;
    }
}