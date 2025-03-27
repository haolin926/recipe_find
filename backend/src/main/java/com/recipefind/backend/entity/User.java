package com.recipefind.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", schema = "recipe_db")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "user_photo")
    private String userPhoto;

    public UserDTO convertToDTO() {
        return new UserDTO(this.id.toString(), this.username, this.email, this.userPhoto);
    }
}
