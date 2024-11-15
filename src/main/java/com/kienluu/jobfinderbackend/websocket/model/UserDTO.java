package com.kienluu.jobfinderbackend.websocket.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private String id;
    private String userName;
    private String password;
    private String email;
    private String phone;
    private String avatar;
}
