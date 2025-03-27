package com.recipefind.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    private String id;
    private String username;
    private String password;
    private String email;
    private String userPhoto;

    public UserDTO(String id, String username, String email, String userPhoto) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.userPhoto = userPhoto;
    }
}
