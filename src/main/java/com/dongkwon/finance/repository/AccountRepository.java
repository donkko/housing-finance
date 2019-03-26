package com.dongkwon.finance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dongkwon.finance.domain.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findOneByUserId(String userId);
}
