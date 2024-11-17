package com.kienluu.jobfinderbackend.websocket.service;

import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.entity.UserEntity;
import com.kienluu.jobfinderbackend.repository.CompanyRepository;
import com.kienluu.jobfinderbackend.repository.UserRepository;
import com.kienluu.jobfinderbackend.websocket.model.Participant;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ParticipantService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public Participant findParticipantById(String id) {
        if(id.trim().startsWith("u_")) {
            return findUserById(id);
        }else return findCompanyById(id);
    }

    private Participant findUserById(String id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        return Participant.builder()
                .name(user.getName())
                .avatar(user.getAvatar())
                .id(user.getUserId())
                .build();
    }
    private Participant findCompanyById(String id) {
        CompanyEntity company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company Not Found"));
        return Participant.builder()
                .name(company.getName())
                .id(company.getCompanyId())
                .avatar(company.getLogo())
                .build();
    }
}
