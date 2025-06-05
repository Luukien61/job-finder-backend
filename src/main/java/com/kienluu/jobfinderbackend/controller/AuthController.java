package com.kienluu.jobfinderbackend.controller;

import com.kienluu.jobfinderbackend.dto.request.PublicKeyRequest;
import com.kienluu.jobfinderbackend.dto.request.VerifyChallengeRequest;
import com.kienluu.jobfinderbackend.dto.response.UserResponse;
import com.kienluu.jobfinderbackend.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping()
public class AuthController {
    private final IUserService userService;


    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        try {
            var tokenResponse = userService.refreshToken(request.get("refresh_token"));
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(e.getMessage());
        }
    }

    @PostMapping("/register-public-key")
    public ResponseEntity<?> registerPublicKey(@RequestBody PublicKeyRequest request) {
        try {
            var response = userService.registerPublicKey(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/verify-client-challenge")
    public ResponseEntity<?> verifyClientChallenge(@RequestBody VerifyChallengeRequest request) {
        try{
            UserResponse userResponse = userService.verifyClientChallenge(request);
            return ResponseEntity.ok(userResponse);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/clear-biometric-key")
    public ResponseEntity<?> clearPublicKey(@RequestBody VerifyChallengeRequest request) {
        try{
            Boolean isClear = userService.clearBiometricPublicKey(request);
            return ResponseEntity.ok(isClear);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
