package com.kienluu.jobfinderbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
public enum CompanyState {
    ACTIVE("ACTIVE"),
    RESTRICT("RESTRICT"),
    BAN("BAN");
    private String state;

    @Override
    public String toString() {
        return this.state;
    }

    public static CompanyState fromString(String state) {
        return switch (state) {
            case "RESTRICT" -> RESTRICT;
            case "BAN" -> BAN;
            default -> ACTIVE;
        };
    }
}
