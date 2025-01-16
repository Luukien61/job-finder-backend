package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.notification.AcceptNotification;
import com.kienluu.jobfinderbackend.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcceptNotificationRepository extends JpaRepository<AcceptNotification, Long> {
    List<AcceptNotification> findAllByUserId(String userId);

    @Query("select count(n) from AcceptNotification n where n.userId= :user_id and n.status= :status or n.status = 'SENT'")
    Long countAllNotificationsByUserIdAndStatus(@Param("user_id") String userId, @Param("status") NotificationStatus status);
}
