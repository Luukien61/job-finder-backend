package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.model.JobByField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, String> {
    Optional<CompanyEntity> findCompanyById(String id);
    boolean existsByEmail(String email);

    Optional<CompanyEntity> findByEmail(String email);
    Optional<CompanyEntity> findByName(String name);
    Optional<CompanyEntity> findCompanyEntityByEmailAndPassword(String email, String password);



    @Query("select count(p) from CompanyEntity p")
    Integer countAllCompany();

    @Query("select  count(distinct p.id) from CompanyEntity p join JobEntity j on p.id = j.company.id and j.expireDate >= CURRENT_DATE ")
    Integer countCompanyByJobNotExpired();

    @Query("SELECT COUNT(j) FROM CompanyEntity j " +
            "WHERE EXTRACT(MONTH FROM j.createdAt) = :month " +
            "AND EXTRACT(YEAR FROM j.createdAt) = :year")
    long countCompanyByMonthAndYear(@Param("month") int month,
                                    @Param("year") int year);

    @Query("SELECT COUNT(j) FROM CompanyEntity j " +
            "WHERE EXTRACT(YEAR FROM j.createdAt) = :year")
    long countCompanyByYear(@Param("year") int year);


}
