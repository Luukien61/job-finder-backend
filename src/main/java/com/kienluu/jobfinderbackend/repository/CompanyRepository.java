package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, String> {
    CompanyEntity findByCompanyId(String id);

    @Query("SELECT c.monthlyPost FROM CompanyEntity c WHERE c.companyId = :companyId")
    Integer findMonthlyPostByCompanyId(@Param("companyId") String companyId);

    // Tăng số lượng bài đăng trong tháng sau mỗi lần đăng
    @Modifying
    @Query("UPDATE CompanyEntity c SET c.monthlyPost = c.monthlyPost + 1 WHERE c.companyId = :companyId")
    void incrementMonthlyPost(@Param("companyId") String companyId);
}
