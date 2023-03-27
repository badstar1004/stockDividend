package com.stock.stockdividend.model;

import com.stock.stockdividend.persist.entity.MemberEntity;
import lombok.Data;

import java.util.List;

public class Auth {

    /**
     * 로그인 inner 클래스
     */
    @Data
    public static class SignIn {
        private String username;
        private String password;

    }

    /**
     * 회원가입 inner 클래스
     */
    @Data
    public static class SignUp {
        private String username;
        private String password;
        private List<String> roles;

        // SignUp 클래스를 MemberEntity 로 변환
        public MemberEntity toEntity() {
            return MemberEntity.builder()
                    .username(this.username)
                    .password(this.password)
                    .roles(this.roles)
                    .build();
        }
    }
}
