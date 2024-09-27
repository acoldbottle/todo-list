package com.acoldbottle.todolist.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.acoldbottle.todolist.domain.UserRole.USER;

/**
 * 사용자 클래스 -> BaseEntity 를 상속받아 사용자 생성시간, 수정시간 관리
 *
 * username = 사용자 이름, oauth2로만 로그인하기 때문에 중복을 피하기 위해 provider + "_" + providerID 와 같은 형식. -> ex) google_hasdh12kjnhfa712f9ajfsj
 * email = 사용자 이메일
 * role = 사용자 권한 (사용자, 관리자)
 * provider = 구글, 페이스북, 네이버와 같은 사용자가 로그인한 소셜
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String username;

    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role = USER;

    private String provider;


    @Builder
    public User(String username, String email, String provider) {
        this.username = username;
        this.email = email;
        this.provider = provider;
    }
}
