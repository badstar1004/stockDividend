package com.stock.stockdividend.controller;

import com.stock.stockdividend.exception.imp.CompanyException;
import com.stock.stockdividend.model.Company;
import com.stock.stockdividend.model.constants.CacheKey;
import com.stock.stockdividend.persist.entity.CompanyEntity;
import com.stock.stockdividend.sevice.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 공통적인 기능에 따라 controller 를 구분지을 수 있음
@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final CacheManager redisCacheManager;

    /**
     * 회사명 자동완성 조회
     *
     * @param keyword
     * @return
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autoComplete(@RequestParam String keyword) {
        List<String> result = this.companyService.getCompanyNamesByKeyword(keyword);
        return ResponseEntity.ok(result);
    }

    /**
     * 회사 전체 조회
     *
     * @param pageable
     * @return
     */
    @GetMapping
    @PreAuthorize("hasRole('READ')")    // READ 권한이 있는사람만 접근 가능
    public ResponseEntity<?> searchCompany(final Pageable pageable) {
        Page<CompanyEntity> companyEntities = this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companyEntities);
    }

    /**
     * 회사 저장
     *
     * @param requestCompany
     * @return
     */
    @PostMapping
    @PreAuthorize("hasRole('WRITE')")   // WRITE 권한이 있는사람만 접근 가능
    public ResponseEntity<?> addCompany(@RequestBody Company requestCompany) {

        String ticker = requestCompany.getTicker().trim();

        // 빈문자 예외 처리
        if (ObjectUtils.isEmpty(ticker)) {
            throw new CompanyException("TNULL");
        }

        Company company = this.companyService.save(ticker);

        // 회사를 저장할 때마다 trie 에 저장
        this.companyService.addAutoCompleteKeyword(company.getName());

        return ResponseEntity.ok(company);
    }

    /**
     * 회사 삭제
     *
     * @return
     */

    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")   // WRITE 권한이 있는사람만 접근 가능
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
        // 회사 데이터 삭제
        String companyName = this.companyService.deleteCompany(ticker);

        // 캐시 삭제
        this.clearFinanceCache(companyName);

        return ResponseEntity.ok(companyName);
    }

    /**
     * 캐시 삭제
     *
     * @param companyName
     */
    public void clearFinanceCache(String companyName) {
        this.redisCacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName);
    }
}
