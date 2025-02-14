package com.kienluu.jobfinderbackend.controller;

import com.kienluu.jobfinderbackend.dto.UserDTO;
import com.kienluu.jobfinderbackend.dto.request.LoginRequest;
import com.kienluu.jobfinderbackend.dto.request.UserAccountUpdateRequest;
import com.kienluu.jobfinderbackend.dto.request.UserCreationRequest;
import com.kienluu.jobfinderbackend.dto.JobDto;
import com.kienluu.jobfinderbackend.dto.response.UserResponse;
import com.kienluu.jobfinderbackend.model.CodeExchange;
import com.kienluu.jobfinderbackend.model.MailTemplate;
import com.kienluu.jobfinderbackend.model.StringElement;
import com.kienluu.jobfinderbackend.service.IUserService;
import com.kienluu.jobfinderbackend.service.implement.MailService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping()
public class UserController {

    private IUserService userService;
    private MailService mailService;


    @PostMapping("/user/signup")
    public ResponseEntity<Object> registerUser(@RequestBody UserCreationRequest request) {
        try {
            UserResponse userResponse = userService.registerUser(request);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/user/complete")
    public ResponseEntity<Object> userCompletion(@RequestBody UserDTO userDTO) {
        try {
            UserResponse userResponse = userService.userCompleted(userDTO);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/user/profile")
    public ResponseEntity<Object> updateProfile(@RequestBody UserDTO userDTO) {
        try{
            UserDTO updateProfile = userService.updateProfile(userDTO);
            return ResponseEntity.ok(updateProfile);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/user/{id}/cv")
    public ResponseEntity<Object> uploadCv(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        try {
            userService.uploadCv(id, file);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/signup/code")
    public ResponseEntity<Object> authenticatedSuccess(@RequestBody MailTemplate mailTemplate) {
        try {
            String code = userService.sendSignupCode(mailTemplate);
            return ResponseEntity.ok(code);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/code")
    public ResponseEntity<Object> sendVerificationCode(@RequestBody MailTemplate mailTemplate) {
        try{
            String code = mailService.send(mailTemplate);
            return ResponseEntity.ok(code);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
        try {
            UserResponse userResponse = userService.loginUser(loginRequest);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/google/signup")
    public ResponseEntity<Object> registerGoogle(@RequestBody CodeExchange codeExchange) {
        try {
            UserResponse userResponse = userService.sigUpWithGoogle(codeExchange);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/google/login")
    public ResponseEntity<Object> loginWithGoogle(@RequestBody CodeExchange codeExchange) {
        try {
            UserResponse userResponse = userService.loginWithGoogle(codeExchange);
            return ResponseEntity.ok(userResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Object> getUserProfile(@PathVariable String id) {
        try {
            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/info/{id}")
    public ResponseEntity<Object> getUserChat(@PathVariable String id) {
        try {
            UserResponse userResponse = userService.getUserInfoById(id);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/user/{id}/cv")
    public ResponseEntity<Object> deleteCv(@PathVariable String id, @RequestBody StringElement element) {
        try {
            userService.deleteCvById(id, element.getValue());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{id}/saved")
    public ResponseEntity<Object> getSavedJobs(@PathVariable String id) {
        try {
            List<JobDto> allSavedJobs = userService.findAllSavedJobs(id);
            return ResponseEntity.ok(allSavedJobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{id}/applied")
    public ResponseEntity<Object> getAppliedJobs(@PathVariable String id) {
        try {
            List<JobDto> allSavedJobs = userService.findAllAppliedJobs(id);
            return ResponseEntity.ok(allSavedJobs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/user/account/verification")
    public ResponseEntity<Object> verifyAccount(@RequestBody UserAccountUpdateRequest request) {
        try{
            String code = userService.sendVerificationEmail(request);
            return ResponseEntity.ok(code);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/user/account/update")
    public ResponseEntity<Object> updateUser(@RequestBody UserAccountUpdateRequest request) {
        try{
            UserResponse response = userService.updateUserAccount(request);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/user/{userId}/save")
    public ResponseEntity<Object> saveJob(@PathVariable String userId, @RequestParam("jobId") Long jobId){
        try{
            boolean saved = userService.saveJob(userId, jobId);
            return ResponseEntity.ok().body(saved);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/user/{userId}/unsave")
    public ResponseEntity<Object> unSaveJob(@PathVariable String userId, @RequestParam("jobId") Long jobId){
        try{
            boolean saved = userService.unsaveJob(userId, jobId);
            return ResponseEntity.ok().body(saved);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/save")
    public ResponseEntity<Object> getJob(@PathVariable String userId,@RequestParam("jobId") Long jobId){
        try{
            boolean isSaved = userService.isJobSaved(userId, jobId);
            return ResponseEntity.ok(isSaved);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/user/{userId}/basic")
    public ResponseEntity<Object> getBasicInfo(@PathVariable String userId){
        try{
            UserDTO userBasicInfo = userService.getUserBasicInfo(userId);
            return ResponseEntity.ok(userBasicInfo);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

