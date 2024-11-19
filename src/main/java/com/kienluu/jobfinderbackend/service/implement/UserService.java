package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.dto.UserDTO;
import com.kienluu.jobfinderbackend.dto.request.LoginRequest;
import com.kienluu.jobfinderbackend.dto.request.UserCreationRequest;
import com.kienluu.jobfinderbackend.dto.response.UserResponse;
import com.kienluu.jobfinderbackend.entity.UserEntity;
import com.kienluu.jobfinderbackend.mapper.CustomMapper;
import com.kienluu.jobfinderbackend.model.CodeExchange;
import com.kienluu.jobfinderbackend.model.GoogleUserInfo;
import com.kienluu.jobfinderbackend.model.MailTemplate;
import com.kienluu.jobfinderbackend.model.UserRole;
import com.kienluu.jobfinderbackend.repository.UserRepository;
import com.kienluu.jobfinderbackend.service.IUserService;
import com.kienluu.jobfinderbackend.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final GoogleCodeExchange googleCodeExchange;
    private final UserRepository userRepository;
    private final CustomMapper mapper;
    private final MailService mailService;
    private final S3UploadService s3UploadService;


    @Override
    public String sendSignupCode(MailTemplate template) throws MessagingException, GeneralSecurityException, IOException {
        userRepository.findByEmail(template.getTo().trim())
                .ifPresent(user -> {
                    throw new RuntimeException("This email already exists!");
                });
        return sendEmailCode(template);
    }

    private String sendEmailCode(MailTemplate template) throws MessagingException, GeneralSecurityException, IOException {
        return mailService.send(template);
    }

    @Override
    public UserResponse registerUser(UserCreationRequest request) {
        userRepository.findByEmail(request.getEmail().trim())
                .ifPresent(user -> {
                    throw new RuntimeException("This email already exists!");
                });
        UserEntity user = mapper.toUserEntity(request);
        user.setUserId("u_" + AppUtil.generateCustomUserId());
        user.setRole(UserRole.EMPLOYEE);
        user = userRepository.save(user);
        return mapper.toUserResponse(user);
    }

    @Override
    public UserResponse loginUser(LoginRequest request) {
        if (isGoogleAccount(request.getEmail())) throw new RuntimeException("This email use google log in!");
        UserEntity user = userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword())
                .orElseThrow(() -> new RuntimeException("Invalid email or password!"));
        return mapper.toUserResponse(user);
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
        if (!userEntity.getUserId().startsWith("google_")) {
            throw new RuntimeException("This email is not an google account!");
        }
        return mapper.toUserResponse(userEntity);
    }

    @Override
    public UserResponse sigUpWithGoogle(CodeExchange codeExchange) {
        GoogleUserInfo userInfo = googleCodeExchange.exchange(codeExchange.getCode());
        Optional<UserEntity> userEntity = userRepository.findByEmail(userInfo.getEmail().trim());
        if (userEntity.isPresent()) throw new RuntimeException("This email has already been registered!");
        UserEntity user = UserEntity.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .avatar(userInfo.getPicture())
                .role(UserRole.EMPLOYEE)
                .userId("google_" + AppUtil.generateCustomUserId())
                .address(userInfo.getLocale())
                .build();
        user = userRepository.save(user);
        return mapper.toUserResponse(user);
    }

    public boolean isGoogleAccount(String email) {
        UserEntity user = userRepository.findByEmail(email.trim()).orElse(null);
        if (user == null) {
            return false;
        } else {
            String id = user.getUserId();
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
        UserEntity user = userRepository.findById(userDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setPhone(userDTO.getPhone());
        user.setAvatar(userDTO.getAvatar());
        user.setAddress(userDTO.getAddress());
        user.setUniversity(userDTO.getUniversity());
        user.setDateOfBirth(userDTO.getDateOfBirth());
        user.setEducationLevel(userDTO.getEducationLevel());
        user.setGender(userDTO.getGender());
        user = userRepository.save(user);
        return mapper.toUserResponse(user);
    }

    @Override
    public void uploadCv(String userId, MultipartFile file) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        List<String> cv = user.getCv();
        String url = s3UploadService.uploadFile(file);
        cv.add(url);
        user.setCv(cv);
        userRepository.save(user);
    }

    @Override
    public UserEntity inActiveUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(()
                -> new RuntimeException("Invalid user id!"));
        userEntity.setActiveState(false);
        userRepository.save(userEntity);
        return userEntity;
    }

    @Override
    public void deleteUser(String id) {
        UserEntity userEntity = userRepository.findByUserId(id).orElseThrow(()
                -> new RuntimeException("Invalid user id!"));
        userRepository.delete(userEntity);
    }
}