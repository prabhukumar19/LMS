package com.lms.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @NotBlank
    private String name;
    @Email
    @NotBlank
    @Length(min = 6)
    @Column(nullable = false,unique = true)
    private String email;
    //It should be unique and works as username
    @NotBlank
    private String password;
    @Column(nullable = true)
    private boolean isAdmin;
    @Column(name = "OTP")
    private String OTP;

    private LocalDateTime OtpGeneratedAt;

    @Column(nullable = true)
    private boolean isActive;
    @Column(nullable = true)
    private boolean isLocked;
    @Column(nullable = true)
    private boolean isVerified;
    @NotNull
    private int loginCount;
    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;
    private LocalDateTime updatedAt;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

//    @OneToMany
//    private Course courses;
}
