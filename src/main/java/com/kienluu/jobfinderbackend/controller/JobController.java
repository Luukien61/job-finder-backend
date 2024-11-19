package com.kienluu.jobfinderbackend.controller;

import com.kienluu.jobfinderbackend.dto.request.JobCreateRequest;
import com.kienluu.jobfinderbackend.dto.response.JobResponse;
import com.kienluu.jobfinderbackend.elasticsearch.document.JobDocument;
import com.kienluu.jobfinderbackend.elasticsearch.service.JobSearchService;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.service.implement.JobService;
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
    private final JobService jobService;
    private final JobSearchService jobSearchService;


    @PostMapping("")
    public ResponseEntity<Object> createJob(@RequestBody JobCreateRequest job) {
        try {
            JobResponse saveJob = jobService.saveJob(job);
            return new ResponseEntity<>(saveJob, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<Object> bulkJob(@RequestBody List<JobCreateRequest> jobs) {
        try{
            jobs.forEach(jobService::saveJob);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
//
//    @PutMapping("/")
//    public ResponseEntity<Object> updateJob(@RequestBody JobEntity job) {
//        try {
//            JobEntity updateJob = jobService.updateJob(job);
//            return new ResponseEntity<>(updateJob, HttpStatus.OK);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

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
    public ResponseEntity<Object> searchJob(
            @RequestParam String query,
            @RequestParam int page,
            @RequestParam int size)
    {
        try {
            Page<JobDocument> documents = jobSearchService.searchJobs(query, page, size);
            return new ResponseEntity<>(documents, HttpStatus.OK);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
