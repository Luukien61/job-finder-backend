package com.kienluu.jobfinderbackend.controller;

import com.kienluu.jobfinderbackend.dto.ReportedJobDto;
import com.kienluu.jobfinderbackend.dto.request.CompanyBanRequest;
import com.kienluu.jobfinderbackend.dto.request.LoginRequest;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.model.*;
import com.kienluu.jobfinderbackend.service.IAdminService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    private final IAdminService adminService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try{
            adminService.login(loginRequest);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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

    @GetMapping("/user/statistics")
    public ResponseEntity<Object> getStatistics(@RequestParam("month") int month,
                                                @RequestParam("year") int year) {
        try {
            UserStatistic userStatistic = adminService.getUserStatistic(month, year);
            return ResponseEntity.ok(userStatistic);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //so user theo thang, nam
    @GetMapping("/user/quantity")
    public ResponseEntity<Long> getTotalUsersByMonthAndYear(@RequestParam("month") int month,
                                                            @RequestParam("year") int year) {
        try {
            long count = adminService.countUserByMonthAndYear(month, year);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //so user dang ki moi theo nam
    @GetMapping("/user")
    public ResponseEntity<Long> getTotalUsersByYear(@RequestParam("year") int year) {
        try {
            long count = adminService.countJobsByYear(year);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //----------------------------------job----------------------------------------
    //tong so job da duoc tao
    @GetMapping("/job/total")
    public ResponseEntity<Long> getTotalJobs() {
        try {
            long count = adminService.countAllJob();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //tong so job chua het han
    @GetMapping("/job/ongoing/total")
    public ResponseEntity<Long> getTotalJobsExpired() {
        try {
            long count = adminService.countJobNotExpired();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //so job theo tung linh vuc dang tuyen
    @GetMapping("/job/ongoing")
    public ResponseEntity<Long> getTotalJobsNotExpiredByField(@RequestParam("field") String field) {
        try {
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
        try {
            long count = adminService.countJobsByMonthAndYear(month, year);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/job")
    public ResponseEntity<Long> getTotalJobsByYear(@RequestParam("year") int year) {
        try {
            long count = adminService.countJobsByYear(year);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //so job da dang cua tung cong ty
    @GetMapping("/{companyId}/job/total")
    public ResponseEntity<Long> getTotalJobsByCompany(@PathVariable("companyId") String companyId) {
        try {
            long count = adminService.countJobByCompany(companyId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //tong so job dang can tuyen theo tung cong ty
    @GetMapping("/{companyId}/job/ongoing/total")
    public ResponseEntity<Long> getTotalJobsNotExpiredByCompany(@PathVariable("companyId") String companyId) {
        try {
            long count = adminService.countJobNotExpiredByCompany(companyId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //dem jobs theo tung ngay trong thang
    @GetMapping("/job/quantity/day")
    public ResponseEntity<List<Long>> getTotalJobsByDay(@RequestParam int month,
                                                        @RequestParam int year) {
        try {
            List<Long> jobsByDay = adminService.countJobsByDayInMonth(month, year);
            return ResponseEntity.ok(jobsByDay);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    //-------------------------------------company------------------------------------
    //tong so cong ty, nha tuyen dung da tham gia web
    @GetMapping("/company/total")
    public ResponseEntity<Long> getTotalCompany() {
        try {
            long count = adminService.countAllCompany();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //so cong ty dang can tuyen nguoi
    @GetMapping("/company/finding")
    public ResponseEntity<Long> getTotalCompanyFindingEmployee() {
        try {
            long count = adminService.countCompanyFindingEmployee();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/company")
    public ResponseEntity<Long> getTotalCompanyByYear(@RequestParam("year") int year) {
        try {
            long count = adminService.countCompanyByYear(year);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //so cong ty dang ki moi theo thang
    @GetMapping("/company/quantity")
    public ResponseEntity<Long> getCompanyByMonthAndYear(@RequestParam("month") int month,
                                                         @RequestParam("year") int year) {
        try {
            long count = adminService.countCompanyByMonthAndYear(month, year);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //----------------------------report------------------------------------
    @GetMapping("/job/reported")
    public ResponseEntity<List<ReportedJobDto>> getReportedJobs() {
        try {
            List<ReportedJobDto> jobs = adminService.reportedJobs();
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{jobId}/reason")
    public ResponseEntity<Object> getReportedReasonsByJobId(@PathVariable("jobId") Long jobId) {
        try {
            List<ReportItemDetail> reasons = adminService.reportedItems(jobId);
            return ResponseEntity.ok(reasons);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/ban/{companyId}")
    public ResponseEntity<Object> banCompany(@PathVariable("companyId") String companyId,
                                             @RequestBody CompanyBanRequest request) {
        try {
            adminService.deActivateCompany(companyId, request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{companyId}/job/all")
    public ResponseEntity<Object> getAllJobs(@PathVariable String companyId) {
        try {
            List<JobEntity> jobs = adminService.findJobsByCompanyId(companyId);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/jobs/fields")
    public ResponseEntity<Object> getAllJobsByField(@RequestParam("month") int month,
                                                    @RequestParam("year") int year) {
        try {
            List<JobByField> jobs = adminService.getJobsByField(month, year);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/jobs/company")
    public ResponseEntity<Object> getAllJobsByCompany(@RequestParam("month") int month,
                                                      @RequestParam("year") int year) {
        try {
            List<JobByCompanyByMonth> jobsByCompany = adminService.getJobsByCompany(month, year);
            return ResponseEntity.ok(jobsByCompany);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping("/report/rejection/{jobId}")
    public ResponseEntity<Object> rejectReportsByJobId(@PathVariable("jobId") Long jobId) {
        try {
            adminService.rejectReports(ReportStatus.DONE, jobId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    //-----------------------job apply----------------------------------------
    @GetMapping("/application")
    public ResponseEntity<Integer> getQuantityApplicationsByMonth(@RequestParam int month,
                                                                  @RequestParam int year) {
        try {
            int count = adminService.countAppsByMonth(month, year);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
