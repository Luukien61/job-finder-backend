package com.kienluu.jobfinderbackend.controller;

import com.kienluu.jobfinderbackend.dto.request.LoginRequest;
import com.kienluu.jobfinderbackend.dto.request.UserCreationRequest;
import com.kienluu.jobfinderbackend.dto.response.UserResponse;
import com.kienluu.jobfinderbackend.model.CodeExchange;
import com.kienluu.jobfinderbackend.model.MailTemplate;
import com.kienluu.jobfinderbackend.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@AllArgsConstructor
@RestController
@RequestMapping()
public class UserController {

    private IUserService userService;


    @PostMapping("/user/signup")
    public ResponseEntity<Object> registerUser(@RequestBody UserCreationRequest request) {
        try {
            UserResponse userResponse = userService.registerUser(request);
            return ResponseEntity.ok(userResponse);
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
}

