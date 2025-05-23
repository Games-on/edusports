package com.example.checkscam.constant.sports;

public enum TournamentStatus {
    REGISTRATION("Đang đăng ký"),
    ONGOING("Đang diễn ra"),
    COMPLETED("Đã hoàn thành"),
    CANCELLED("Đã hủy");

    private final String description;

    TournamentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
