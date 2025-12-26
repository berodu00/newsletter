package com.kz.magazine.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;

    private java.time.LocalDateTime deletedAt;

    // TODO: Add constructor
}
