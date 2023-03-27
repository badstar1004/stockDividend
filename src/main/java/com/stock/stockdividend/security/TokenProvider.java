package com.stock.stockdividend.security;

import com.stock.stockdividend.sevice.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final String KEY_ROLES = "roles";
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60;   // 1시간
    private final MemberService memberService;

    // 비밀키 값
    @Value("${spring.jwt.secret}")
    private String secretKey;

    /**
     * 토큰 생성 (발급)
     *
     * @param username
     * @param roles
     * @return
     */
    public String generateToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        // Claims 은 key-value 로 넣어줘야함
        claims.put(KEY_ROLES, roles);

        // 토큰이 생성된 시간
        Date now = new Date();
        // 토큰 만료시간
        Date expiredDate = new Date(now.getTime() * TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)               // 토큰 생성 시간
                .setExpiration(expiredDate)     // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS512, this.secretKey)     // 사용할 암호화 알고리즘, 비밀키
                .compact();
    }

    /**
     * jwt 인증 정보 조회
     *
     * @param jwt
     * @return
     */
    public Authentication getAuthentication(String jwt) {

        UserDetails userDetails = this.memberService.loadUserByUsername(
                this.getUsername(jwt));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * 토큰의 회원정보
     *
     * @param token
     * @return
     */
    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 토큰 유효성 검사
     *
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        // token 의 값이 빈값이면
        if (!StringUtils.hasText(token)) {
            return false;
        }

        // 토큰 유효성 검사
        var claims = this.parseClaims(token);
        // 토큰의 만료 시간이 현재 시간보다 이전 여부
        return !claims.getExpiration().before(new Date());
    }

    /**
     * 토큰 Claims 정보 파싱
     *
     * @param token
     * @return
     */
    private Claims parseClaims(String token) {
        try {
            // 토큰 만료 기간이 경과된 상태에서 파싱 시 ExpiredJwtException 이 발생 할 수 있음
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
