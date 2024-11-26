package com.kienluu.jobfinderbackend.controller;

import com.kienluu.jobfinderbackend.dto.JobDto;
import com.kienluu.jobfinderbackend.service.implement.AdminService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
@Getter
@Setter
public class AdminController {
    private final AdminService adminService;
    //tinh tong so luong ung vien da tham gia
    @GetMapping("/user/total")
    public ResponseEntity<Long> getTotalUsers() {
        try {
            long count = adminService.countAllUser();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //tong so job da duoc tao
    @GetMapping("/job/total")
    public ResponseEntity<Long> getTotalJobs() {
        try{
            long count = adminService.countAllJob();
            return ResponseEntity.ok(count);
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //tong so job chua het han
    @GetMapping("/job/notexpired")
    public ResponseEntity<Long> getTotalJobsExpired() {
        try{
            long count = adminService.countJobNotExpired();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //so job theo tung linh vuc dang tuyen
    @GetMapping("/job/notexpired/")
    public ResponseEntity<Long> getTotalJobsNotExpiredByField(@RequestParam("field") String field) {
        try{
            long count = adminService.countJobNotExpiredByField(field);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    //so job theo thang, nam
    @GetMapping("/job/quantity")
    public ResponseEntity<Long> getTotalJobsByMonthAndYear(@RequestParam("month") int month,
                                                           @RequestParam("year") int year) {
        try{
            long count = adminService.countJobsByMonthAndYear(month, year);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/job")
    public ResponseEntity<Long> getTotalJobsByYear(@RequestParam("year") int year) {
        try{
            long count = adminService.countJobsByYear(year);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //so job da dang cua tung cong ty
    @GetMapping("/{companyid}/totaljobs")
    public ResponseEntity<Long> getTotalJobsByCompany(@PathVariable("companyid") String companyId) {
        try{
            long count = adminService.countJobByCompany(companyId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //tong so job dang can tuyen theo tung cong ty
    @GetMapping("/{companyid}/quantityjobnotexpired")
    public ResponseEntity<Long> getTotalJobsNotExpiredByCompany(@PathVariable("companyid") String companyId) {
        try{
            long count = adminService.countJobNotExpiredByCompany(companyId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //tong so cong ty, nha tuyen dung da tham gia web
    @GetMapping("/company/total")
    public ResponseEntity<Long> getTotalCompany() {
        try{
            long count = adminService.countAllCompany();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //so cong ty dang can tuyen nguoi
    @GetMapping("/company/finding")
    public ResponseEntity<Long> getTotalCompanyFindingEmployee(){
        try{
            long count = adminService.countCompanyFindingEmployee();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/company")
    public ResponseEntity<Long> getTotalCompanyByYear(@RequestParam("year") int year) {
        try{
            long count = adminService.countCompanyByYear(year);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/reportedjob")
    public ResponseEntity<Object> getReportedJobs(){
        try {
            List<JobDto> rpjobs = adminService.reportedJobs();
            return ResponseEntity.ok(rpjobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{jobid}/rpreason")
    public ResponseEntity<Object> getReportedReasonsByJobId(@PathVariable("jobid") Long jobId) {
        try{
            List<String> reasons = adminService.reportedDescription(jobId);
            return ResponseEntity.ok(reasons);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/ban/{companyid}")
    public ResponseEntity<Object> banCompany(@PathVariable("companyid") String companyid) {
        try{
            adminService.deActivateCompany(companyid);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
