package com.example.jpa_concurrency_performance_lab.domain.member;

import com.example.jpa_concurrency_performance_lab.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "member",
        indexes = {
                @Index(name = "idx_member_email", columnList = "email", unique = true)
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 200)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Builder
    private Member(String email, String name, String password, Role role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public static Member createUser(String email, String encodedPassword, String name) {
        return Member.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .role(Role.USER)
                .build();
    }

    public void changeName(String name) {
        this.name = name;
    }
}
