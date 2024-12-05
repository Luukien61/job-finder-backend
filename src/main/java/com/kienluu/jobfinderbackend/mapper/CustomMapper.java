package com.kienluu.jobfinderbackend.mapper;


import com.kienluu.jobfinderbackend.dto.*;
import com.kienluu.jobfinderbackend.dto.request.JobCreateRequest;
import com.kienluu.jobfinderbackend.dto.request.UserCreationRequest;
import com.kienluu.jobfinderbackend.dto.response.*;
import com.kienluu.jobfinderbackend.entity.*;
import org.mapstruct.*;

@Mapper(uses = MapperHelper.class)
public interface CustomMapper {
    ;

    @Mapping(source = "role", target = "role", qualifiedByName = "userDtoRoleToEntity")
    @Mapping(target = "savedJobs", ignore = true)
    @Mapping(target = "appliedJobs", ignore = true)
    UserEntity userDtoToUserEntity(UserDTO userDTO);

    @Mapping(source = "role", target = "role", qualifiedByName = "userEntityRoleToDto")
    UserResponse toUserResponse(UserEntity user);

    UserEntity toUserEntity(UserCreationRequest request);

    @Mapping(target = "role", expression = "java(user.getRole().toString())")
    @Mapping(target = "appliedJobs", source = "appliedJobs", qualifiedByName = "jobsToJobId")
    @Mapping(target = "savedJobs", source = "savedJobs", qualifiedByName = "jobsToJobId")
    UserDTO toUserDTO(UserEntity user);


    @Mapping(target = "company", ignore = true)
    JobEntity toJobEntity(JobCreateRequest request);

    @Mapping(target = "company",ignore = true)
    @Mapping(target = "applications",ignore = true)
    @Mapping(target = "reports",ignore = true)
    JobEntity toJobEntity(JobDto dto);

    @Mapping(target = "companyName", expression = "java(job.getCompany().getName())")
    @Mapping(target = "companyId", expression = "java(job.getCompany().getId())")
    @Mapping(target = "logo", expression = "java(job.getCompany().getLogo())")
//    @Mapping(target = "salary", expression = "java(job.getMinSalary() + \" - \" + job.getMaxSalary() + \" triệu\")")
    JobDto toJobResponse(JobEntity job);


    @Mapping(target = "companyName", expression = "java(job.getCompany().getName())")
    @Mapping(target = "companyId", expression = "java(job.getCompany().getId())")
    @Mapping(target = "logo", expression = "java(job.getCompany().getLogo())")
    JobCardResponse toJobCardResponse(JobEntity job);


    CompanyResponse toCompanyResponse(CompanyEntity company);

    CompanyEntity toCompanyEntity(CompanyDto companyDto);

    CompanyDto toCompanyDto(CompanyEntity companyEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CompanyEntity partialUpdate(CompanyDto companyDto, @MappingTarget CompanyEntity companyEntity);


    CompanyCreateResponse toCompanyCreateResponse(CompanyEntity companyEntity);

    @Mapping(target = "role", expression = "java(baseUserEntity.getRole().toString())")
    LoginResponse toLoginResponse(BaseUserEntity baseUserEntity);

    @Mapping(target = "userName", expression = "java(userContext.getUser().getName())")
    @Mapping(target = "userAvatar", expression = "java(userContext.getUser().getAvatar())")
    @Mapping(target = "jobId", expression = "java(entity.getJob().getJobId())")
    @Mapping(target = "userId", expression = "java(userContext.getUser().getId())")
    JobApplicationDto toJobApplicationDto(JobApplicationEntity entity, @Context UserContext userContext);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "job", ignore = true)
    JobApplicationEntity toJobApplicationEntity(JobApplicationDto app);

    @Mapping(target = "jobId",expression = "java(reportEntity.getJob().getJobId())")
    ReportDTO toReportResponse(ReportEntity reportEntity);
}
