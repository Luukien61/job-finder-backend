package com.kienluu.jobfinderbackend.mapper;

import com.kienluu.jobfinderbackend.model.UserRole;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

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
}
