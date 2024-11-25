package com.kienluu.jobfinderbackend.controller;

import com.kienluu.jobfinderbackend.service.implement.AdminService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
@Getter
@Setter
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/user/total")
    public ResponseEntity<Long> getTotalUsers() {
        try {
            long count = adminService.countAllUser();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/job/total")
    public ResponseEntity<Long> getTotalJobs() {
        try{
            long count = adminService.countAllJob();
            return ResponseEntity.ok(count);
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/job/notexpired")
    public ResponseEntity<Long> getTotalJobsExpired() {
        try{
            long count = adminService.countJobNotExpired();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/job/{field}")
    public ResponseEntity<Long> getTotalJobsNotExpiredByField(@PathVariable("field") String field) {
        try{
            long count = adminService.countJobNotExpiredByField(field);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/{companyid}/totaljobs")
    public ResponseEntity<Long> getTotalJobsByCompany(@PathVariable("companyid") String companyId) {
        try{
            long count = adminService.countJobByCompany(companyId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/company/quantityjobnotexpired")
    public ResponseEntity<Long> getTotalJobsNotExpiredByCompany(@Param("companyId") String companyId) {
        try{
            long count = adminService.countJobNotExpiredByCompany(companyId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/company/total")
    public ResponseEntity<Long> getTotalCompany() {
        try{
            long count = adminService.countAllCompany();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/company/")
    public ResponseEntity<Long> getTotalCompanyFindingEmployee(){
        try{
            long count = adminService.countCompanyFindingEmployee();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/report/list")
    public ResponseEntity<List> getReportedJobs(){
        try {
            List<String> jobs = adminService.
        }
    }

}
