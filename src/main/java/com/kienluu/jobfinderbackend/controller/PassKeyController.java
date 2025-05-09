package com.kienluu.jobfinderbackend.controller;

import com.kienluu.jobfinderbackend.model.passkey.ChallengeResponse;
import com.kienluu.jobfinderbackend.model.passkey.PasskeyRegistrationRequest;
import com.kienluu.jobfinderbackend.service.implement.WebAuthnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/passkey")
@Slf4j
@RequiredArgsConstructor
public class PassKeyController {

    private static final Logger LOGGER = Logger.getLogger(PassKeyController.class.getName());

    private final WebAuthnService webAuthnService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerPasskey(@RequestBody PasskeyRegistrationRequest request) {
        try {
            boolean verified = webAuthnService.verifyCredential(request);
            if (!verified) {
                LOGGER.warning("Passkey verification failed");
                return ResponseEntity.badRequest().build();
            }
            webAuthnService.saveCredential(request, "google_1737087253719");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            LOGGER.severe("Error processing passkey registration: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/challenge")
    public ChallengeResponse getChallenge() {
        // Tạo challenge ngẫu nhiên
        byte[] challengeBytes = new byte[32];
        new SecureRandom().nextBytes(challengeBytes);
        String challenge = Base64.getEncoder().encodeToString(challengeBytes);

        // Lưu challenge tạm thời (ví dụ: trong session, database, hoặc cache)
        // Ở đây chỉ trả về để minh họa
        return new ChallengeResponse(challenge);
    }
}
