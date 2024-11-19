package com.kienluu.jobfinderbackend.mapper;


import com.kienluu.jobfinderbackend.dto.UserDTO;
import com.kienluu.jobfinderbackend.dto.request.JobCreateRequest;
import com.kienluu.jobfinderbackend.dto.request.UserCreationRequest;
import com.kienluu.jobfinderbackend.dto.response.JobResponse;
import com.kienluu.jobfinderbackend.dto.response.UserResponse;
import com.kienluu.jobfinderbackend.entity.JobEntity;
import com.kienluu.jobfinderbackend.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = MapperHelper.class)
public interface CustomMapper {
;
    @Mapping(source = "role",target = "role", qualifiedByName = "userDtoRoleToEntity")
    @Mapping(target = "savedJobs", ignore = true)
    @Mapping(target = "appliedJobs", ignore = true)
    UserEntity userDtoToUserEntity(UserDTO userDTO);
    @Mapping(source = "role", target = "role", qualifiedByName = "userEntityRoleToDto" )
    UserResponse toUserResponse(UserEntity user);
    UserEntity toUserEntity(UserCreationRequest request);

    @Mapping(target = "role", expression = "java(user.getRole().toString())")
    @Mapping(target = "appliedJobs", source = "appliedJobs", qualifiedByName = "jobsToJobId")
    @Mapping(target = "savedJobs", source = "savedJobs", qualifiedByName = "jobsToJobId")
    UserDTO toUserDTO(UserEntity user);

    @Mapping(target = "company", ignore = true)
    JobEntity toJobEntity(JobCreateRequest request);

    @Mapping(target = "companyName", expression = "java(job.getCompany().getName())")
    @Mapping(target = "companyId", expression = "java(job.getCompany().getCompanyId())")
    @Mapping(target = "salary", expression = "java(job.getMinSalary() + \" - \" + job.getMaxSalary() + \" triệu\")")
    JobResponse toJobResponse(JobEntity job);
}
