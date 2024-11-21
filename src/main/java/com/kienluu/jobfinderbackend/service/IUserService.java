package com.kienluu.jobfinderbackend.service;

import com.kienluu.jobfinderbackend.dto.UserDTO;
import com.kienluu.jobfinderbackend.dto.request.LoginRequest;
import com.kienluu.jobfinderbackend.dto.request.UserCreationRequest;
import com.kienluu.jobfinderbackend.dto.response.UserResponse;
import com.kienluu.jobfinderbackend.entity.UserEntity;
import com.kienluu.jobfinderbackend.model.CodeExchange;
import com.kienluu.jobfinderbackend.model.MailTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;


public interface IUserService {
    String sendSignupCode(MailTemplate template) throws MessagingException, GeneralSecurityException, IOException;
    UserResponse registerUser(UserCreationRequest request);
    UserResponse loginUser(LoginRequest request);
    UserResponse updateUser(UserDTO userDTO);
    UserResponse loginWithGoogle(CodeExchange codeExchange);
    UserResponse sigUpWithGoogle(CodeExchange codeExchange);
    UserDTO getUserById(String id);
    UserResponse getUserInfoById(String id);
    UserResponse userCompleted(UserDTO userDTO);
    void uploadCv(String userId, MultipartFile file);


}
