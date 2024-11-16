package com.kienluu.jobfinderbackend.mapper;

import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.model.UserRole;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class MapperHelper {
    @Named("userEntityRoleToDto")
    public String toUserDtoRole(UserRole userRole) {
        return userRole.toString();
    }

    @Named("userDtoRoleToEntity")
    public UserRole toUserRole(String role) {
        return UserRole.fromString(role);
    }

    @Named("jobsToJobId")
    public Set<Long> toJobId(Set<JobEntity> jobs) {
        Set<Long> jobIds = new HashSet<>();
        for (JobEntity job : jobs) {
            jobIds.add(job.getJobId());
        }
        return jobIds;
    }
}
