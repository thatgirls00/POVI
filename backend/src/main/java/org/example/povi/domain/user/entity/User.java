package org.example.povi.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.povi.global.entity.BaseEntity;
import org.example.povi.domain.transcription.entity.Transcription;

import java.util.ArrayList;
import java.util.List;

import org.example.povi.auth.enums.AuthProvider;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends BaseEntity {
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "profile_img_url")
    private String profileImgUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    private String providerId;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;

    @Column(nullable = false)
    private boolean isEmailVerified = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Transcription> transcriptions = new ArrayList<>();

    public void verifyEmail() {
        this.isEmailVerified = true;
    }

    public String getProvider() {
        return this.provider.name();
    }
}