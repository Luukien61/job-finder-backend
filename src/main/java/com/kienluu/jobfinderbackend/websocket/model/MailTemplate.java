package com.kienluu.jobfinderbackend.websocket.model;

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
