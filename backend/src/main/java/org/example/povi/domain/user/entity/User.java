package org.example.povi.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.povi.auth.enums.AuthProvider;
import org.example.povi.domain.community.entity.PostLike;
import org.example.povi.domain.diary.comment.entity.DiaryComment;
import org.example.povi.domain.diary.like.entity.DiaryPostLike;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.transcription.entity.Transcription;
import org.example.povi.global.entity.BaseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Set<PostLike> likes = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<DiaryPost> diaryPosts = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<DiaryComment> diaryComments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<DiaryPostLike> diaryPostLikes = new HashSet<>();


    public void verifyEmail() {
        this.isEmailVerified = true;
    }

    public String getProvider() {
        return this.provider.name();
    }

    public void updateProfileImgUrl(String profileImageUrl) { this.profileImgUrl = profileImageUrl;}
    public void updateNickname(String nickname){
        this.nickname = nickname;
    }
    public void updateBio(String bio) {
        this.bio = bio;
    }
}