package com.kienluu.jobfinderbackend.controller;

import com.kienluu.jobfinderbackend.dto.request.LoginRequest;
import com.kienluu.jobfinderbackend.dto.request.UserCreationRequest;
import com.kienluu.jobfinderbackend.entity.UserEntity;
import com.kienluu.jobfinderbackend.model.UserRole;
import com.kienluu.jobfinderbackend.service.implement.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController
@RequestMapping()
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public ResponseEntity<Object> createUser(@RequestBody  UserCreationRequest request){
       return ResponseEntity.ok("Created");
    }

    @PostMapping("/user/signup")
    public ResponseEntity<String> registerUser(@RequestBody UserEntity user,@RequestParam UserRole role) {
        try {
            String result = userService.registerUser(user, role);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during registration: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public String showUserHomePage(@RequestBody LoginRequest request) {
        return request.getEmail();
    }
}

