package com.acoldbottle.todolist.dto;

import com.acoldbottle.todolist.domain.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.acoldbottle.todolist.domain.UserRole.USER;

@Data
@NoArgsConstructor
public class UserDTO {

    private Long userId;

    private String username;

    private String email;

    private String provider;

    private UserRole role = USER;

    public UserDTO(Long userId, String username, String email, String provider) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.provider = provider;
    }
}
