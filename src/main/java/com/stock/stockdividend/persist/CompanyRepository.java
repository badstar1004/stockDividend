package com.stock.stockdividend.persist;

import com.stock.stockdividend.persist.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    /**
     * 회사 여부
     *
     * @param ticker
     * @return
     */
    boolean existsByTicker(String ticker);

    /**
     * 회사명 기준 회사 정보 조회
     *
     * @param name
     * @return
     */
    Optional<CompanyEntity> findByName(String name);

    /**
     * Like 연산자로 조회
     *
     * @param str
     * @return
     */
    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String str, Pageable pageable);

    /**
     * ticker 기준 조회
     *
     * @param ticker
     * @return
     */
    Optional<CompanyEntity> findByTicker(String ticker);

}
