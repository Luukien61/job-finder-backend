package com.kienluu.jobfinderbackend.service.implement;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.kienluu.jobfinderbackend.dto.UserDTO;
import com.kienluu.jobfinderbackend.dto.request.*;
import com.kienluu.jobfinderbackend.dto.response.JobCardResponse;
import com.kienluu.jobfinderbackend.dto.response.RegisterBiometricResponse;
import com.kienluu.jobfinderbackend.dto.response.TokenResponse;
import com.kienluu.jobfinderbackend.dto.response.UserResponse;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.entity.UserEntity;
import com.kienluu.jobfinderbackend.event.UserSearchEvent;
import com.kienluu.jobfinderbackend.mapper.CustomMapper;
import com.kienluu.jobfinderbackend.model.*;
import com.kienluu.jobfinderbackend.repository.JobRepository;
import com.kienluu.jobfinderbackend.repository.UserRepository;
import com.kienluu.jobfinderbackend.security.jwt.provider.IJWTProvider;
import com.kienluu.jobfinderbackend.service.IUserService;
import com.kienluu.jobfinderbackend.util.AppUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService, UserDetailsService {
    private final CustomMapper customMapper;

    public static final String SHA_256_WITH_RSA = "SHA256withRSA";
    private static final long CHALLENGE_VALIDITY_SECONDS = 60; // 60 seconds validity
    private final GoogleCodeExchange googleCodeExchange;
    private final UserRepository userRepository;
    private final CustomMapper mapper;
    private final ThirdPartyMailService mailService;
    private final S3Service s3Service;
    private final JobRepository jobRepository;
    @Value("${oauth.google.client-id}")
    private String googleClientId;
    private final IJWTProvider jwtProvider;


    @Override
    public String sendSignupCode(MailTemplate template) throws IOException, jakarta.mail.MessagingException {
        userRepository.findByEmail(template.getTo().trim())
                .ifPresent(user -> {
                    throw new RuntimeException("This email already exists!");
                });
        return sendEmailCode(template);
    }

    private String sendEmailCode(MailTemplate template) throws IOException, jakarta.mail.MessagingException {
        return mailService.send(template);
    }

    @Override
    public UserResponse registerUser(UserCreationRequest request) {
        userRepository.findByEmail(request.getEmail().trim())
                .ifPresent(user -> {
                    throw new RuntimeException("This email already exists!");
                });
        UserEntity user = mapper.toUserEntity(request);
        user.setId("u_" + AppUtil.generateCustomUserId());
        user.setRole(UserRole.EMPLOYEE);
        if(request.getCreatedAt()!=null){
            user.setCreatedAt(request.getCreatedAt());
        }
        user = userRepository.save(user);
        return createLoginResponse(user);
    }

    @Override
    public UserResponse loginUser(LoginRequest request) {
        if (isGoogleAccount(request.getEmail())) throw new RuntimeException("This email use google log in!");
        UserEntity user = userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword())
                .orElseThrow(() -> new RuntimeException("Invalid email or password!"));
        return createLoginResponse(user);
    }

    @Override
    public UserResponse updateUser(UserDTO userDTO) {
        return null;
    }

    @Override
    public UserResponse loginWithGoogle(CodeExchange codeExchange) {
        GoogleUserInfo userInfo = googleCodeExchange.exchange(codeExchange.getCode());
        UserEntity userEntity = userRepository.findByEmail(userInfo.getEmail().trim())
                .orElseThrow(() ->
                        new RuntimeException("This email has not been registered! Please sign up first!"));
        if (!userEntity.getId().startsWith("google_")) {
            throw new RuntimeException("This email is not an google account!");
        }
        return mapper.toUserResponse(userEntity);
    }

    @Override
    public UserResponse loginWithGoogleMobile(String idToken) {
        try {
            GoogleIdToken.Payload payload = getGooglePayload(idToken);
            String email = payload.getEmail();
            UserEntity userEntity = userRepository.findByEmail(email.trim())
                    .orElseThrow(() ->
                            new IllegalArgumentException("This email has not been registered! Please sign up first!"));
            return createLoginResponse(userEntity);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Invalid token!");
        }
    }

    @Override
    public UserResponse signUpWithGoogle(CodeExchange codeExchange) {
        GoogleUserInfo userInfo = googleCodeExchange.exchange(codeExchange.getCode());
        Optional<UserEntity> userEntity = userRepository.findByEmail(userInfo.getEmail().trim());
        if (userEntity.isPresent()) throw new RuntimeException("This email has already been registered!");
        UserEntity user = UserEntity.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .avatar(userInfo.getPicture())
                .createdAt(LocalDate.now())
                .role(UserRole.EMPLOYEE)
                .id("google_" + userInfo.getId())
                .address(userInfo.getLocale())
                .build();
        user = userRepository.save(user);
        return mapper.toUserResponse(user);
    }

    @Override
    public UserResponse signUpWithGoogleMobile(String idToken) {
        try {
            GoogleIdToken.Payload payload = getGooglePayload(idToken);
            String email = payload.getEmail();
            String id = payload.getSubject();
            Optional<UserEntity> userEntity = userRepository.findByEmail(email.trim());
            if (userEntity.isPresent()) throw new IllegalArgumentException("This email has already been registered!");
            UserEntity user = UserEntity.builder()
                    .email(email)
                    .name((String) payload.get("name"))
                    .avatar((String) payload.get("picture"))
                    .createdAt(LocalDate.now())
                    .role(UserRole.EMPLOYEE)
                    .id("google_" + id)
                    .address((String) payload.get("locale"))
                    .build();
            user = userRepository.save(user);
            return createLoginResponse(user);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error while verifying token!");
        }
    }

    public boolean isGoogleAccount(String email) {
        UserEntity user = userRepository.findByEmail(email.trim()).orElse(null);
        if (user == null) {
            return false;
        } else {
            String id = user.getId();
            return id.startsWith("google");
        }
    }

    @Override
    public UserDTO getUserById(String id) {
        UserEntity user = userRepository.findById(id.trim())
                .orElseThrow(() -> new RuntimeException("This email has not been registered!"));
        return mapper.toUserDTO(user);
    }

    @Override
    public UserResponse getUserInfoById(String id) {
        UserEntity user = userRepository.findById(id.trim())
                .orElseThrow(() -> new RuntimeException("This email has not been registered!"));
        return mapper.toUserResponse(user);
    }

    @Override
    public UserResponse userCompleted(UserDTO userDTO) {
        UserEntity user = updateInfo(userDTO);
        return createLoginResponse(user);
    }

    @Override
    public void uploadCv(String userId, MultipartFile file) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        List<String> cv = user.getCv();
        String url = s3Service.uploadFile(file);
        cv.add(url);
        user.setCv(cv);
        userRepository.save(user);
    }

    @Override
    public void deleteCvById(String userId, String cvUrl) {
        UserEntity user = userRepository.findById(userId.trim())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        var cvs = user.getCv();
        if (cvs.contains(cvUrl)) {
            s3Service.deleteFileFromS3ByUrl(cvUrl);
            cvs.remove(cvUrl);
            user.setCv(cvs);
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public List<JobCardResponse> findAllSavedJobs(String userId) {
        UserEntity user = userRepository.findById(userId.trim())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        Set<JobEntity> savedJobs = user.getSavedJobs();
        return savedJobs.stream().map(customMapper::toJobCardResponse).toList();
    }

    @Override
    public List<Long> findAllSavedJobIds(String userId) {
        UserEntity user = userRepository.findById(userId.trim())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        Set<JobEntity> savedJobs = user.getSavedJobs();
        return savedJobs.stream().map(JobEntity::getJobId).toList();
    }

    @Override
    public List<JobCardResponse> findAllAppliedJobs(String userId) {
        UserEntity user = userRepository.findById(userId.trim())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        Set<JobEntity> savedJobs = user.getAppliedJobs();
        return savedJobs.stream().map(customMapper::toJobCardResponse).toList();
    }

    @Override
    public UserResponse updateUserAccount(UserAccountUpdateRequest request) {
        UserEntity user = userRepository.findById(request.getId().trim())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        try{
            var isPasswordValid = user.getPassword().equals(request.getOldPassword());
            if(!isPasswordValid) throw new RuntimeException("Old password does not match!");
            Optional.ofNullable(request.getEmail())
                    .filter(email -> !email.isBlank()).ifPresent(user::setEmail);
            Optional.ofNullable(request.getNewPassword())
                    .filter(password -> !password.isBlank()).ifPresent(user::setPassword);
            UserEntity saved = userRepository.save(user);
            return mapper.toUserResponse(saved);
        }catch (NullPointerException e){
            throw new RuntimeException("This account does not support changing password!");
        }

    }

    @Override
    public String sendVerificationEmail(UserAccountUpdateRequest request) throws IOException, MessagingException {
        UserEntity user = userRepository.findById(request.getId().trim())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        if (!Objects.equals(user.getPassword(), request.getOldPassword())) {
            throw new RuntimeException("Old password does not match!");
        }
        Optional<UserEntity> optionalUser = userRepository.findByEmail(request.getEmail().trim());
        if (optionalUser.isPresent()) {
            if (!optionalUser.get().getId().equals(request.getId().trim())) {
                throw new RuntimeException("This email has been registered!");
            }
        }
        MailTemplate template = MailTemplate.builder()
                .to(request.getEmail().trim())
                .useCase("Thay đổi thông tin tài khoản")
                .build();
        return sendEmailCode(template);
    }

    @Override
    public UserDTO updateProfile(UserDTO userDTO) {
        UserEntity updatedUser = updateInfo(userDTO);
        return mapper.toUserDTO(updatedUser);
    }

    private UserEntity updateInfo(UserDTO userDTO) {
        UserEntity user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        Optional.ofNullable(userDTO.getEmail()).filter(s -> !s.isBlank()).ifPresent(user::setEmail);
        Optional.ofNullable(userDTO.getName()).filter(s -> !s.isBlank()).ifPresent(user::setName);
        Optional.ofNullable(userDTO.getPhone()).filter(s -> !s.isBlank()).ifPresent(user::setPhone);
        Optional.ofNullable(userDTO.getAvatar()).filter(s -> !s.isBlank()).ifPresent(user::setAvatar);
        Optional.ofNullable(userDTO.getAddress()).filter(s -> !s.isBlank()).ifPresent(user::setAddress);
        Optional.ofNullable(userDTO.getUniversity()).filter(s -> !s.isBlank()).ifPresent(user::setUniversity);
        Optional.ofNullable(userDTO.getDateOfBirth()).ifPresent(user::setDateOfBirth);
        Optional.ofNullable(userDTO.getEducationLevel()).filter(s -> !s.isBlank()).ifPresent(user::setEducationLevel);
        Optional.ofNullable(userDTO.getGender()).ifPresent(user::setGender);
        return userRepository.save(user);
    }

    @Override
    public boolean saveJob(String userId, Long jobId) {
        UserEntity user = userRepository.findById(userId.trim())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        JobEntity jobEntity = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Invalid job id!"));
        user.getSavedJobs().add(jobEntity);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean unsaveJob(String userId, Long jobId) {
        UserEntity user = userRepository.findById(userId.trim())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        JobEntity jobEntity = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Invalid job id!"));
        user.getSavedJobs().remove(jobEntity);
        userRepository.save(user);
        return false;
    }

    @Override
    public boolean isJobSaved(String userId, Long jobId) {
        return userRepository.isJobSaved(userId.trim(), jobId);
    }

    @Override
    public UserDTO getUserBasicInfo(String userId) {
        UserEntity user = userRepository.findById(userId.trim())
                .orElseThrow(() -> new RuntimeException("This email has not been registered!"));
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .address(user.getAddress())
                .university(user.getUniversity())
                .build();
    }

    @EventListener(UserSearchEvent.class)
    public void onUserSearch(UserSearchEvent event) {
        try {
            UserEntity user = userRepository.findUserById(event.getUserId())
                    .orElseThrow(() -> new RuntimeException("Invalid user id!"));
            List<String> searchHistory = user.getSearchHistory();
            if (searchHistory == null) searchHistory = new ArrayList<>();
            if (searchHistory.size() >= 5) {
                searchHistory.remove(0);
            }
            searchHistory.add(event.getData());
            searchHistory = new ArrayList<>(new LinkedHashSet<>(searchHistory));
            user.setSearchHistory(searchHistory);
            userRepository.save(user);
            userRepository.flush();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private GoogleIdToken.Payload getGooglePayload(String idToken) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken != null) {
            return googleIdToken.getPayload();
        } else {
            throw new RuntimeException("Invalid token!");
        }
    }

    @Override
    public List<String> getUserCv(String userId) {
        UserEntity user = userRepository.findById(userId.trim())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        return user.getCv();
    }


    @Override
    public void saveUserFcm(String userId, String fcmToken) {
        UserEntity user = userRepository.findById(userId.trim())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

    @Override
    public String getUserFcm(String userId) {
        UserEntity user = userRepository.findById(userId.trim())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        return user.getFcmToken();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        val user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id!"));
        return UserPrincipal.create(user);
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        var userEmail = jwtProvider.exactUserName(refreshToken);
        var userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id!"));
        var userDetail = UserPrincipal.create(userEntity);
        var isValid = jwtProvider.isTokenValid(refreshToken, userDetail);
        if (!isValid) throw new RuntimeException("The refresh token is invalid");
        return jwtProvider.generateTokenResponse(userDetail, refreshToken);
    }

    private UserResponse createLoginResponse(UserEntity existUser) {
        var userPrincipal = UserPrincipal.create(existUser);
        var tokenResponse = jwtProvider.generateTokenResponse(userPrincipal);
        var userResponse = mapper.toUserResponse(existUser);
        userResponse.setTokenResponse(tokenResponse);
        return userResponse;
    }

    @Override
    public RegisterBiometricResponse registerPublicKey(PublicKeyRequest request) {
        var user = userRepository.findUserById(request.getUserId().trim())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        user.setPublicBiometricKey(request.getPublicKey());
        userRepository.save(user);
        return new RegisterBiometricResponse(true);
    }

    public UserResponse verifyClientChallenge(VerifyChallengeRequest request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Invalid user id: " + request.getUserId()));
        boolean isValid = isSignatureValid(request, user);
        if (isValid) {
            return createLoginResponse(user);
        } else {
            log.error("Signature verification failed for user: {}", request.getUserId());
            throw new RuntimeException("Invalid signature!");
        }
    }

    private boolean isSignatureValid(VerifyChallengeRequest request, UserEntity user) {
        try {
            // Parse and validate challenge
            String[] challengeParts = request.getChallenge().split(":");
            if (challengeParts.length != 2) {
                log.error("Invalid challenge format for user: {}. Expected <timestamp>:<uuid>", request.getUserId());
                throw new RuntimeException("Invalid challenge format");
            }

            long timestamp;
            try {
                timestamp = Long.parseLong(challengeParts[0]);
            } catch (NumberFormatException e) {
                log.error("Invalid timestamp in challenge for user: {}: {}", request.getUserId(), challengeParts[0]);
                throw new RuntimeException("Invalid timestamp in challenge");
            }

            // Verify timestamp
            long currentTime = System.currentTimeMillis();
            long timeDiffSeconds = Math.abs(currentTime - timestamp) / 1000;
            if (timeDiffSeconds > CHALLENGE_VALIDITY_SECONDS) {
                log.error("Challenge expired for user: {}. Timestamp: {}, Current: {}, Diff: {}s",
                        request.getUserId(), timestamp, currentTime, timeDiffSeconds);
                throw new RuntimeException("Challenge expired");
            }

            // Sanitize and decode public key
            String publicKeyStr = user.getPublicBiometricKey().replaceAll("\\s+", "");
            if (!isValidBase64(publicKeyStr)) {
                log.error("Invalid Base64 format for public key: {}", publicKeyStr);
                throw new RuntimeException("Invalid Base64 format for public key");
            }
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");


            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            // Sanitize and decode signature
            String signatureStr = request.getSignature().replaceAll("\\s+", "");
            if (!isValidBase64(signatureStr)) {
                log.error("Invalid Base64 format for signature: {}", signatureStr);
                throw new RuntimeException("Invalid Base64 format for signature");
            }
            byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);

            // Verify signature
            Signature verifier = Signature.getInstance(SHA_256_WITH_RSA);
            verifier.initVerify(publicKey);
            verifier.update(request.getChallenge().getBytes());
            return verifier.verify(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValidBase64(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean clearBiometricPublicKey(VerifyChallengeRequest request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Invalid user id: " + request.getUserId()));
        boolean isValid = isSignatureValid(request, user);
        if (isValid) {
            user.setPublicBiometricKey(null);
            userRepository.save(user);
            return true;
        } else {
            throw new RuntimeException("Invalid signature!");
        }
    }

    @Override
    public Boolean updateUserCv(String userId, String cvUrl) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Invalid user id: " + userId));
        List<String> cvs = user.getCv();
        if (cvs == null) {
            cvs = new ArrayList<>();
        }
        if (!cvs.contains(cvUrl)) {
            cvs.add(cvUrl);
            user.setCv(cvs);
            userRepository.save(user);
        }
        return true;
    }

    @Override
    public void deleteUserCv(String userId, String cvUrl) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Invalid user id: " + userId));
        List<String> cvs = user.getCv();
        if (cvs.contains(cvUrl)) {
            cvs.remove(cvUrl);
            user.setCv(cvs);
            userRepository.save(user);
        }
    }
}
