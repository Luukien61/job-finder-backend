package com.kienluu.jobfinderbackend.controller;

import com.kienluu.jobfinderbackend.dto.JobApplicationDto;
import com.kienluu.jobfinderbackend.service.IJobApplicationService;
import com.kienluu.jobfinderbackend.service.implement.JobApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/application")
@AllArgsConstructor
public class JobApplicationController {
    private final IJobApplicationService jobApplicationService;

    @PostMapping("/apply")
    public ResponseEntity<Object> applyJob(@RequestBody JobApplicationDto application) {
        try{
            jobApplicationService.applyJob(application);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/applied")
    public ResponseEntity<Object> getJob(
            @RequestParam("jobId") Long jobId,
            @RequestParam("userId") String userId
    ) {
        boolean applied = jobApplicationService.isApplied(jobId, userId);
        if (applied) {
            return ResponseEntity.ok().body(applied);
        }
        return ResponseEntity.ok(applied);
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<Object> getApplicationByJobId(@PathVariable Long jobId) {
        try{
            List<JobApplicationDto> applicationsByJobId = jobApplicationService.getApplicationsByJobId(jobId);
            return ResponseEntity.ok().body(applicationsByJobId);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{appId}/accept")
    public ResponseEntity<Object> acceptApplication(@PathVariable Long appId) {
        try{
            jobApplicationService.acceptApplication(appId);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{appId}/reject")
    public ResponseEntity<Object> rejectApplication(@PathVariable Long appId) {
        try{
            jobApplicationService.declineApplication(appId);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
