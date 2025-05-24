package com.example.checkscam.constant;

public enum TeamStatus {
    ACTIVE("ACTIVE"),
    ELIMINATED("ELIMINATED"),
    WITHDRAWN("WITHDRAWN");

    private final String value;

    TeamStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
