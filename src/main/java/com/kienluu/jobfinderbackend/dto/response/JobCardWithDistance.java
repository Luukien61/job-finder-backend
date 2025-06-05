package com.kienluu.jobfinderbackend.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class JobCardWithDistance {
    private JobCardResponse job;
    private Double distance;
}

