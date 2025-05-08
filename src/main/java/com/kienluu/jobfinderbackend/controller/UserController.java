package com.kienluu.jobfinderbackend.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping()
public class UserController {

    private final IUserService userService;
    private final MailService mailService;

    @Value("${oauth.google.client-id}")
    private String googleClientId;

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


    @PostMapping("/google/mobile/signin")
    public ResponseEntity<?> verifyGoogleToken(@RequestBody String idTokenString) {
        try {
            // Cấu hình verifier để kiểm tra ID token
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            // Xác minh ID token
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String userId = payload.getSubject(); // Subject ID duy nhất của người dùng
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                // Logic xử lý người dùng (ví dụ: kiểm tra hoặc tạo user trong database)
                // Ở đây chỉ trả về thông tin xác thực thành công
                return ResponseEntity.ok(new AuthResponse("Authenticated", userId, email, name));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse("Invalid ID token", null, null, null));
            }
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse("Error verifying token: " + e.getMessage(), null, null, null));
        }
    }

    @PostMapping("/google/mobile/signup")
    public ResponseEntity<?> signupGoogle(@RequestBody String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String userId = payload.getSubject();
                // Kiểm tra user trong database
//                if (userId.isEmpty()) {
//                    return ResponseEntity.ok(new AuthResponse("Signed up and signed in", userId, payload.getEmail(), (String) payload.get("name")));
//                } else {
//                    return ResponseEntity.status(HttpStatus.CONFLICT)
//                            .body(new AuthResponse("User already exists", userId, payload.getEmail(), (String) payload.get("name")));
//                }
                return ResponseEntity.ok(new AuthResponse("Signed up and signed in", userId, payload.getEmail(), (String) payload.get("name")));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse("Invalid ID token", null, null, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse("Error: " + e.getMessage(), null, null, null));
        }
    }

}
record AuthResponse(String message, String userId, String email, String name) {}

