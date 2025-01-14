package com.kienluu.jobfinderbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
public enum NotificationStatus {
    SENT("SENT"),
    DELIVERED("DELIVERED"),
    READ("READ"),
    FAILED("FAILED");
    private String status;
    @Override
    public String toString() {
        return this.status;
    }

    public static NotificationStatus fromString(String state) {
        return switch (state) {
            case "SENT" -> SENT;
            case "DELIVERED" -> DELIVERED;
            default -> FAILED;
        };
    }
}
