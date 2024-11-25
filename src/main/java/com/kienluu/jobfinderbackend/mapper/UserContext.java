package com.kienluu.jobfinderbackend.mapper;

import com.kienluu.jobfinderbackend.entity.UserEntity;

public class UserContext {
    private final UserEntity user;

    public UserContext(UserEntity user) {
        this.user = user;
    }

    public UserEntity getUser() {
        return user;
    }
}

