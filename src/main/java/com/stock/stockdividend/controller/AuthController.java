package com.stock.stockdividend.controller;

import com.stock.stockdividend.model.Auth;
import com.stock.stockdividend.persist.entity.MemberEntity;
import com.stock.stockdividend.security.TokenProvider;
import com.stock.stockdividend.sevice.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    private final TokenProvider tokenProvider;

    /**
     * 회원가입
     *
     * @param signUp
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody Auth.SignUp signUp) {
        // 회원가입 API
        MemberEntity memberEntity = this.memberService.register(signUp);

        log.info("user signUp -> " + memberEntity.getUsername());
        return ResponseEntity.ok(memberEntity);
    }

    /**
     * 로그인
     *
     * @param signIn
     * @return
     */
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody Auth.SignIn signIn) {
        // 로그인 API
        // 패스워드 일치 여부
        MemberEntity memberEntity = this.memberService.authenticate(signIn);
        String token = this.tokenProvider.generateToken(
                memberEntity.getUsername(), memberEntity.getRoles());

        log.info("user login -> " + memberEntity.getUsername());
        return ResponseEntity.ok(token);
    }
}
