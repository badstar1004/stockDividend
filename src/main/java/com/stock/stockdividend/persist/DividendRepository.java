package com.stock.stockdividend.persist;

import com.stock.stockdividend.persist.entity.DividendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity, Long> {

    /**
     * 회사 id 로 배당금 정보 조회
     *
     * @param companyId
     * @return
     */
    List<DividendEntity> findAllByCompanyId(Long companyId);

    /**
     * 배당금 존재 여부
     *
     * @param companyId
     * @param date
     * @return
     */
    boolean existsByCompanyIdAndDate(Long companyId, LocalDateTime date);

    @Transactional
    void deleteByCompanyId(Long id);
}
