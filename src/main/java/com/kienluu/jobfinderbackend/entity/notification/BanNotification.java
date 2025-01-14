package com.kienluu.jobfinderbackend.entity.notification;

import jakarta.persistence.Column;
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
@Table(name = "ban_notification")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BanNotification extends BaseNotification {
    @Column(columnDefinition = "TEXT")
    private String title;
    private String reason;
    private String userId;

}
