package com.example.checkscam.constant;

public enum TournamentStatus {
    REGISTRATION("REGISTRATION"),
    READY_TO_START("READY_TO_START"),
    ONGOING("ONGOING"), 
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED");

    private final String value;

    TournamentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
