package com.kienluu.jobfinderbackend.service.implement;

import com.kienluu.jobfinderbackend.dto.UserDTO;
import com.kienluu.jobfinderbackend.dto.request.LoginRequest;
import com.kienluu.jobfinderbackend.dto.request.UserCreationRequest;
import com.kienluu.jobfinderbackend.dto.response.UserResponse;
import com.kienluu.jobfinderbackend.entity.UserEntity;
import com.kienluu.jobfinderbackend.mapper.CustomMapper;
import com.kienluu.jobfinderbackend.model.CodeExchange;
import com.kienluu.jobfinderbackend.model.MailTemplate;
import com.kienluu.jobfinderbackend.model.UserRole;
import com.kienluu.jobfinderbackend.repository.UserRepository;
import com.kienluu.jobfinderbackend.service.IUserService;
import com.kienluu.jobfinderbackend.util.AppUtil;
import com.kienluu.jobfinderbackend.websocket.model.GoogleUserInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

    private final GoogleCodeExchange googleCodeExchange;
    private final UserRepository userRepository;
    private final CustomMapper mapper;
    private final MailService mailService;


    @Override
    public String sendSignupCode(MailTemplate template) throws MessagingException, GeneralSecurityException, IOException {
        userRepository.findByEmail(template.getTo())
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
        userRepository.findByEmail(request.getEmail())
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
        UserEntity userEntity = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("This email has not been registered! Please sign up first!"));
        if(!userEntity.getUserId().startsWith("google_")) {
            throw new RuntimeException("This email is not an google account!");
        }
        return mapper.toUserResponse(userEntity);
    }

    @Override
    public UserResponse sigUpWithGoogle(CodeExchange codeExchange) {
        GoogleUserInfo userInfo = googleCodeExchange.exchange(codeExchange.getCode());
        Optional<UserEntity> userEntity = userRepository.findByEmail(userInfo.getEmail());
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
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return false;
        } else {
            String id = user.getUserId();
            return id.startsWith("google");
        }
    }
}
