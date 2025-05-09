package com.kienluu.jobfinderbackend.service.implement;


import com.kienluu.jobfinderbackend.entity.WebAuthnCredential;
import com.kienluu.jobfinderbackend.model.passkey.PasskeyRegistrationRequest;
import com.kienluu.jobfinderbackend.repository.UserRepository;
import com.kienluu.jobfinderbackend.repository.WebAuthnCredentialRepository;
import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.data.PublicKeyCredentialParameters;
import com.webauthn4j.data.PublicKeyCredentialType;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.RegistrationRequest;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebAuthnService {

    private final WebAuthnCredentialRepository credentialRepository;
    private final UserRepository userRepository;

    @Value("${origin}")
    private String ORIGIN;

    @Value("${rp-id}")
    private String RP_ID;

    private final WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();


    public boolean verifyCredential(PasskeyRegistrationRequest request) {
        String clientDataJSON = request.getResponse().getClientDataJSON();
        String attestationObjectBase64 = request.getResponse().getAttestationObject();
        int publicKeyAlgorithm = request.getResponse().getPublicKeyAlgorithm();
        String type = request.getType();
        try {
            byte[] attestationObjectBytes = Base64.getUrlDecoder().decode(attestationObjectBase64);
            byte[] clientDataJSONBytes = Base64.getUrlDecoder().decode(clientDataJSON);

            // Tạo ServerProperty (origin và RP ID)
            ServerProperty serverProperty = new ServerProperty(new Origin(ORIGIN), RP_ID, new DefaultChallenge("PGNoYWxsZW5nZT4"));
            RegistrationRequest registrationRequest = new RegistrationRequest(attestationObjectBytes, clientDataJSONBytes);

            PublicKeyCredentialParameters parameters = new PublicKeyCredentialParameters(PublicKeyCredentialType.create(type), COSEAlgorithmIdentifier.create(publicKeyAlgorithm));
            List<PublicKeyCredentialParameters> credentials = Collections.singletonList(parameters);
            val verify = webAuthnManager.verify(registrationRequest, new RegistrationParameters(serverProperty, credentials, false));
            return true;
        } catch (Exception e) {
            log.error("Error verifying credential: {}", e.getMessage());
            return false;
        }
    }

    public void saveCredential(PasskeyRegistrationRequest request, String userId) {

        val userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String userHandle = request.getRawId();
        String credentialId = request.getCredentialId();
        String publicKeyCose = request.getResponse().getPublicKey();

        WebAuthnCredential credential = new WebAuthnCredential();
        credential.setUserHandle(userHandle);
        credential.setCredentialId(credentialId);
        credential.setPublicKeyCose(publicKeyCose);
        credential.setSignatureCount(0);
        credential.setActive(true);
        credential.setCreatedAt(java.time.LocalDateTime.now());
        credential.setUser(userEntity);
        Set<WebAuthnCredential> credentials = userEntity.getCredentials();
        credentials.add(credential);
        userEntity.setCredentials(credentials);

        credentialRepository.save(credential);
        userRepository.save(userEntity);
    }


}

