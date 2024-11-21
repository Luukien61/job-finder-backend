package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, String> {
    Optional<CompanyEntity> findByCompanyId(String id);

    @Query("select count(p) from CompanyEntity p")
    Integer countAllCompany();



    @Query("select count(p) from CompanyEntity p join JobEntity j on p.companyId = j.companyId and p.companyId = :companyId and j.expireDate >= CURRENT_DATE " )
    Integer countJobNotExpireByCompanyId(@Param("companyId") String companyId);

    @Query("select  count(distinct p.companyId) from CompanyEntity p join JobEntity j on p.companyId = j.companyId and j.expireDate >= CURRENT_DATE ")
    Integer countCompanyByJobNotExpired();


}
