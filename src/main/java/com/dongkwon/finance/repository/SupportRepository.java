package com.dongkwon.finance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dongkwon.finance.domain.Institute;
import com.dongkwon.finance.domain.Support;

@Repository
public interface SupportRepository extends JpaRepository<Support, String> {
    Optional<Support> findOneByInstituteAndYearAndMonth(Institute institute, Integer year, Integer month);
}
