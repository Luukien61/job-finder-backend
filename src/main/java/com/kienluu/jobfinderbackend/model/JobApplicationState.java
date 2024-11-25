package com.kienluu.jobfinderbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JobApplicationState {
    PENDING("PENDING",0),
    ACCEPTED("ACCEPTED",1),
    REJECTED("REJECTED",2),;
    private final String state;
    private final int priority;

    public static JobApplicationState fromState(final String state) {
        return JobApplicationState.valueOf(state.toUpperCase());
    }
    @Override
    public String toString(){
        return this.getState();
    }
}
