package com.kienluu.jobfinderbackend.model;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class MailTemplate {
    private String to;
    private String useCase;
}
