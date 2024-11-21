package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.dto.UserDTO;
import com.kienluu.jobfinderbackend.dto.request.LoginRequest;
import com.kienluu.jobfinderbackend.dto.request.UserAccountUpdateRequest;
import com.kienluu.jobfinderbackend.dto.request.UserCreationRequest;
import com.kienluu.jobfinderbackend.dto.response.JobResponse;
import com.kienluu.jobfinderbackend.dto.response.UserResponse;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.entity.UserEntity;
import com.kienluu.jobfinderbackend.mapper.CustomMapper;
import com.kienluu.jobfinderbackend.model.*;
import com.kienluu.jobfinderbackend.repository.UserRepository;
import com.kienluu.jobfinderbackend.service.IUserService;
import com.kienluu.jobfinderbackend.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final GoogleCodeExchange googleCodeExchange;
    private final UserRepository userRepository;
    private final CustomMapper mapper;
    private final MailService mailService;
    private final S3Service s3Service;


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
        user.setId("u_" + AppUtil.generateCustomUserId());
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
        if (!userEntity.getId().startsWith("google_")) {
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
                .id("google_" + AppUtil.generateCustomUserId())
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
        return mapper.toUserResponse(user);
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
    public List<JobResponse> findAllSavedJobs(String userId) {
        UserEntity user = userRepository.findById(userId.trim())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        Set<JobEntity> savedJobs = user.getSavedJobs();
        return savedJobs.stream().map(mapper::toJobResponse).toList();
    }

    @Override
    public List<JobResponse> findAllAppliedJobs(String userId) {
        UserEntity user = userRepository.findById(userId.trim())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        Set<JobEntity> savedJobs = user.getAppliedJobs();
        return savedJobs.stream().map(mapper::toJobResponse).toList();
    }

    @Override
    public UserResponse updateUserAccount(UserAccountUpdateRequest request)  {
        UserEntity user = userRepository.findById(request.getId().trim())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        user.setEmail(request.getEmail().trim());
        user.setPassword(request.getNewPassword());
        UserEntity saved = userRepository.save(user);
        return mapper.toUserResponse(saved);
    }

    @Override
    public String sendVerificationEmail(UserAccountUpdateRequest request) throws MessagingException, GeneralSecurityException, IOException {
        UserEntity user = userRepository.findById(request.getId().trim())
                .orElseThrow(() -> new RuntimeException("Invalid user id!"));
        if(!Objects.equals(user.getPassword(), request.getOldPassword())){
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
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setPhone(userDTO.getPhone());
        user.setAvatar(userDTO.getAvatar());
        user.setAddress(userDTO.getAddress());
        user.setUniversity(userDTO.getUniversity());
        user.setDateOfBirth(userDTO.getDateOfBirth());
        user.setEducationLevel(userDTO.getEducationLevel());
        user.setGender(userDTO.getGender());
        return userRepository.save(user);
    }
}
