package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.notification.BanNotification;
import com.kienluu.jobfinderbackend.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BanNotificationRepository extends JpaRepository<BanNotification, Long> {
    List<BanNotification> findAllByUserId(String userId);

    @Query("select count(n) from BanNotification n where n.userId= :user_id and n.status= :status")
    Long countAllNotificationsByUserIdAndStatus(@Param("user_id") String userId, @Param("status") NotificationStatus status);

}
