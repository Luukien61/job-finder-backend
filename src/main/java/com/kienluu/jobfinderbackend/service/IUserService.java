package com.kienluu.jobfinderbackend.service;

import com.kienluu.jobfinderbackend.dto.UserDTO;
import com.kienluu.jobfinderbackend.dto.request.LoginRequest;
import com.kienluu.jobfinderbackend.dto.request.UserCreationRequest;
import com.kienluu.jobfinderbackend.dto.response.UserResponse;
import com.kienluu.jobfinderbackend.model.CodeExchange;
import com.kienluu.jobfinderbackend.model.MailTemplate;

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

}
