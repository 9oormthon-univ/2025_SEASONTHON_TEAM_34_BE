package com.example.clearfootprint.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // Setter 추가

@Entity
@Getter
@Setter // update 메소드를 위해 Setter 추가
@NoArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    private String picture;


    public User(String email, String name, String picture) {
        this.email = email;
        this.name = name;
        this.picture = picture;
    }

    // 사용자 정보 업데이트를 위한 메소드 추가
    public void update(String name, String picture) {
        this.name = name;
        this.picture = picture;
    }
}