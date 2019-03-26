package com.dongkwon.finance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dongkwon.finance.domain.SupportSummary;
import com.dongkwon.finance.domain.Institute;

@Repository
public interface SupportSummaryRepository extends JpaRepository<SupportSummary, String> {
    Optional<SupportSummary> findFirstByOrderBySumAmountDesc();
    Optional<SupportSummary> findFirstByInstituteOrderByAverageAmountAsc(Institute institute);
    Optional<SupportSummary> findFirstByInstituteOrderByAverageAmountDesc(Institute institute);
}
