package com.kz.magazine.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String name;

    private String email;

    private String department;

    @Column(nullable = false)
    private String role; // "ADMIN", "USER"

    @Column(nullable = false)
    private boolean isActive = true;

    // TODO: Add constructor
}
