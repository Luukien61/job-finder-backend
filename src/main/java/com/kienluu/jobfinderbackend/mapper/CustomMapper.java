package com.kienluu.jobfinderbackend.mapper;


import com.kienluu.jobfinderbackend.dto.UserDTO;
import com.kienluu.jobfinderbackend.dto.request.UserCreationRequest;
import com.kienluu.jobfinderbackend.dto.response.UserResponse;
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
}
