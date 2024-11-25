package com.kienluu.jobfinderbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JobApplicationState {
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED"),
    PENDING("PENDING"),;
    private final String state;

    public static JobApplicationState fromState(final String state) {
        return JobApplicationState.valueOf(state.toUpperCase());
    }
    @Override
    public String toString(){
        return this.getState();
    }
}
