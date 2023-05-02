package com.example.post.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter             //자동으로 getter생성
@NoArgsConstructor  //인자없는 생성자생성
public class Users {
    @Id     //pirmary key를 만들어줌
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //데이터베이스에 저장된 테이블의 Identity 값으로 자동으로 생성
    private Long id;
    //nullable : null 허용여부
    //unique : 중복허용 여부 (false일때 중복허용)
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    public Users(String username, String password, UserRoleEnum role  ){
        this.username = username;
        this.password = password;
        this.role = role;

    }




}