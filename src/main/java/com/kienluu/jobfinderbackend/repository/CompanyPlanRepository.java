package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.CompanyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyPlanRepository extends JpaRepository<CompanyPlan, String> {
    @Query("select plan from CompanyPlan plan")
    List<CompanyPlan> findAll();
}
