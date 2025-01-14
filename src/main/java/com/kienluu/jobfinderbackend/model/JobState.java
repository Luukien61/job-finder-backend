package com.kienluu.jobfinderbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JobState {
    BANNED("BANNED"),
    PENDING("PENDING"),
    DONE("DONE");
    private final String state;

    public String toString() {
        return this.getState();
    }

    public static JobState fromString(String state) {
        return switch (state) {
            case "PENDING" -> PENDING;
            case "DONE" -> DONE;
            default -> throw new IllegalStateException("Unexpected value: " + state);
        };
    }
}
