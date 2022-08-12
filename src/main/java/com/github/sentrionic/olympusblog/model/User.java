package com.github.sentrionic.olympusblog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.AUTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    @Column(unique = true)
    @Email
    @NotEmpty(message = "Email is required")
    private String email;

    @Column(unique = true)
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private String bio = "";

    @NotBlank
    private String image;

    @ManyToMany
    @JoinTable(name = "user_followings")
    private Set<User> followers = new HashSet<>();

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
}

