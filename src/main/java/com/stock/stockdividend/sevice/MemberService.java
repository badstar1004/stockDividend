package com.stock.stockdividend.sevice;

import com.stock.stockdividend.exception.imp.UserException;
import com.stock.stockdividend.model.Auth;
import com.stock.stockdividend.persist.MemberRepository;
import com.stock.stockdividend.persist.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 패스워드 암호화
    private final PasswordEncoder passwordEncoder;


    /**
     * 스프링 시큐리티에서 지원하는 기능을 사용하기 위한 메서드
     *
     * @param username the username identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Couldn't Find user -> " + username));
    }

    /**
     * 회원가입
     *
     * @param signUp
     * @return
     */
    public MemberEntity register(Auth.SignUp signUp) {
        boolean existsByUserName =
                this.memberRepository.existsByUsername(signUp.getUsername());

        // 이미 가입이 되있으면 예외처리
        if (existsByUserName) {
            throw new UserException("EI");
        }

        // 비밀번호를 암호화해서 저장
        signUp.setPassword(this.passwordEncoder.encode(signUp.getPassword()));
        MemberEntity memberEntity = this.memberRepository.save(signUp.toEntity());
        return memberEntity;
    }

    /**
     * 로그인 검증
     *
     * @param signIn
     * @return
     */
    public MemberEntity authenticate(Auth.SignIn signIn) {
        MemberEntity memberEntity = this.memberRepository.findByUsername(signIn.getUsername())
                .orElseThrow(() -> new UserException("NEI"));

        // 비밀번호 일치 여부
        if (!this.passwordEncoder.matches(signIn.getPassword(), memberEntity.getPassword())) {
            throw new UserException("PMM");
        }

        return memberEntity;
    }
}
