package com.dongkwon.finance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dongkwon.finance.domain.Institute;

@Repository
public interface InstituteRepository extends JpaRepository<Institute, String> {
    Optional<Institute> findOneByName(String userId);
}
