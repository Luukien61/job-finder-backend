package com.kienluu.jobfinderbackend.elasticsearch.event;

import com.kienluu.jobfinderbackend.entity.JobEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobChangedEvent {
    private JobEntity job;
    private EvenType action;
}
