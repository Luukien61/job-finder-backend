package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.CompanySubscription;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanySubscriptionRepository extends JpaRepository<CompanySubscription, String> {

    @Modifying
    @Transactional
    @Query("update CompanySubscription com set com.status='cancelled' where com.id= :id")
    void cancelCompanySubscription(String id);
}
