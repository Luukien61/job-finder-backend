package com.kienluu.jobfinderbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
public enum ReportStatus {
    PENDING("PENDING"),
    DONE("DONE");

    private String reportStatus;

    @Override
    public String toString() {
        return this.getReportStatus();
    }

    public static ReportStatus fromString(String status) {
        for (ReportStatus reportStatus : ReportStatus.values()) {
            if (reportStatus.getReportStatus().equalsIgnoreCase(status)) {
                return reportStatus;
            }
        }
        throw new IllegalArgumentException("No Status with text " + status + " found");
    }
}
