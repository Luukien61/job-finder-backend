package com.kienluu.jobfinderbackend.controller;

import com.kienluu.jobfinderbackend.dto.JobDto;
import com.kienluu.jobfinderbackend.dto.request.JobCreateRequest;
import com.kienluu.jobfinderbackend.dto.response.JobCardResponse;
import com.kienluu.jobfinderbackend.elasticsearch.document.JobDocument;
import com.kienluu.jobfinderbackend.elasticsearch.service.JobSearchService;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.service.IJobService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/job")
@AllArgsConstructor
public class JobController {
    private final IJobService jobService;
    private final JobSearchService jobSearchService;


    @PostMapping("/{companyId}")
    public ResponseEntity<Object> createJob(@PathVariable String companyId, @RequestBody JobCreateRequest job) {
        try {
            JobDto saveJob = jobService.saveJob(job);
            return new ResponseEntity<>(saveJob, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<Object> bulkJob(@RequestBody List<JobCreateRequest> jobs) {
        try {
            jobs.forEach(jobService::saveJob);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<Object> getJob(@PathVariable Long jobId) {
        try {
            JobDto job = jobService.getJobById(jobId);
            return new ResponseEntity<>(job, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{jobId}/valid")
    public ResponseEntity<Object> getJobValid(@PathVariable Long jobId,
                                              @RequestHeader(value = "X-custom-userId",required = false) String userId) {
        try{
            JobDto job = jobService.getJobByIdNotExpiryAndNotBan(jobId, userId);
            return new ResponseEntity<>(job, HttpStatus.OK);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/")
    public ResponseEntity<Object> deleteJob(@RequestBody JobEntity job) {
        try {
            jobService.deleteJob(job);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteJob(@PathVariable Long id) {
        try {
            jobService.deleteJobById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @GetMapping("/search")
    public ResponseEntity<Object> searchJobWithLocationAndSalary(
            @RequestParam String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) Integer maxSalary,
            @RequestParam(required = false) Integer experience,
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order,
            @RequestHeader(value = "X-custom-userId",required = false) String userId) {
        try {
            Page<JobDocument> documents = jobSearchService.searchJobs(keyword, location, minSalary, maxSalary, experience, page, size, sort,order,userId);
            return new ResponseEntity<>(documents, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<Object> updateJob(@RequestBody JobDto job) {
        try{
            jobService.updateJob(job);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<Object> searchJobByCompany(
            @PathVariable String companyId,
            @RequestParam("page") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        try {
            Page<JobCardResponse> cards = jobService.getJobCardsByCompany(companyId, page, size);
            return new ResponseEntity<>(cards, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/news")
    public ResponseEntity<Object> fetchNewJobs(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "12") Integer size

    ){
        try{
            Page<JobDto> newJobs = jobService.getNewJobs(page, size);
            return new ResponseEntity<>(newJobs, HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/customHeaders")
    public ResponseEntity<Object> fetchCustomHeaders(@RequestHeader(value = "X-custom-userId",required = false) String userId) {
        log.info("UserId: {}", userId);
        return new ResponseEntity<>("received" ,HttpStatus.OK);
    }

    @GetMapping("/elastic/sync")
    public ResponseEntity<Object> syncElastic() {
        try{
            jobSearchService.syncAllJobsToElasticsearch();
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
