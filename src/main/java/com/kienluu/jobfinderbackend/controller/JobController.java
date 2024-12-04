package com.kienluu.jobfinderbackend.controller;

import com.kienluu.jobfinderbackend.dto.JobDto;
import com.kienluu.jobfinderbackend.dto.request.JobCreateRequest;
import com.kienluu.jobfinderbackend.dto.response.JobEmployerCard;
import com.kienluu.jobfinderbackend.elasticsearch.document.JobDocument;
import com.kienluu.jobfinderbackend.elasticsearch.service.JobSearchService;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.service.IJobService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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



//    @GetMapping("/search")
//    public ResponseEntity<Object> searchJob(
//            @RequestParam String query,
//            @RequestParam int page,
//            @RequestParam int size)
//    {
//        try {
//            Page<JobDocument> documents = jobSearchService.searchJobWithLocation(query, page, size);
//            return new ResponseEntity<>(documents, HttpStatus.OK);
//        }catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchJobWithLocationAndSalary(
            @RequestParam String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) Integer maxSalary,
            @RequestParam(required = false) Integer experience,
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "expiry-date") String sort,
            @RequestParam(required = false,defaultValue = "desc") String order) {
        try {
            Page<JobDocument> documents = jobSearchService.searchJobs(keyword, location, minSalary, maxSalary, experience, page, size, sort,order);
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
            Page<JobEmployerCard> cards = jobService.getJobCardsByCompanyId(companyId, page, size);
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


}
