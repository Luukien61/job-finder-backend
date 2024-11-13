package com.kienluu.jobfinderbackend.websocket.service;

import com.kienluu.jobfinderbackend.entity.CompanyEntity;
import com.kienluu.jobfinderbackend.entity.UserEntity;
import com.kienluu.jobfinderbackend.repository.CompanyRepository;
import com.kienluu.jobfinderbackend.repository.UserRepository;
import com.kienluu.jobfinderbackend.websocket.entity.Participant;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ParticipantService {
    private UserRepository userRepository;
    private CompanyRepository companyRepository;
    public Participant findParticipantById(String id){
        Participant participant;
        if(id.startsWith("com")){
            CompanyEntity company = companyRepository.findByCompanyId(id);
            participant= Participant.builder()
                    .name(company.getName())
                    .avatar(company.getLogo())
                    .id(company.getCompanyId())
                    .build();
        }
        else {
            UserEntity user = userRepository.findByUserId(id);
            participant = Participant.builder()
                    .name(user.getName())
                    .id(user.getUserId())
                    .avatar(user.getAvatar())
                    .build();
        }
        return participant;
    }

}
