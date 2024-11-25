package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long> {
    Optional<JobEntity> findByJobId(Long id);

    @Query("select count(p) from JobEntity p")
    Integer countAllJob();

    @Query("select count(p) from  JobEntity p where p.expireDate >= CURRENT_DATE ")
    Integer countJobNotExpired();

    @Query("select count(p) from  JobEntity p where p.field = :field and p.expireDate >= CURRENT_DATE ")
    Integer countAllByFieldAndNotExpried(@Param("field") String field);

    @Query("select count(j) from JobEntity j where j.company.companyId = :companyId" )
    Integer countJobByCompanyId(@Param("companyId") String companyId);

    @Query("select count(p) from CompanyEntity p join JobEntity j on p.companyId = j.company.companyId and p.companyId = :companyId and j.expireDate >= CURRENT_DATE " )
    Integer countJobNotExpireByCompanyId(@Param("companyId") String companyId);
}
