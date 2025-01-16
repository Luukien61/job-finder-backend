package com.kienluu.jobfinderbackend.entity.notification;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@Table(name = "accept_notification")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AcceptNotification extends BaseNotification {
    public String title;
    private String userId;
}
