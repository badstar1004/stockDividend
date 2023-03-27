package com.stock.stockdividend.persist;

import com.stock.stockdividend.persist.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    /**
     * 회원명 기준 회원정보 조회
     *
     * @param username
     * @return
     */
    Optional<MemberEntity> findByUsername(String username);

    /**
     * 회원명 기준 회원 아이디 유무
     *
     * @param username
     * @return
     */
    boolean existsByUsername(String username);
}
