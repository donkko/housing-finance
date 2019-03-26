package com.dongkwon.finance.service;

import java.util.Optional;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dongkwon.finance.domain.Account;
import com.dongkwon.finance.exception.IdAlreadyUsedException;
import com.dongkwon.finance.exception.IdNotFoundException;
import com.dongkwon.finance.exception.InvalidPasswordException;
import com.dongkwon.finance.repository.AccountRepository;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository,
                          PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerAccount(String id, String password) {
        try {
            final Account newAccount = new Account();
            newAccount.setUserId(id);
            newAccount.setUserPasswordHash(passwordEncoder.encode(password));
            accountRepository.save(newAccount);
        } catch (ConstraintViolationException | DataIntegrityViolationException ex) {
            throw new IdAlreadyUsedException("이미 사용중인 ID입니다.");
        }
    }

    public void validateAccount(String id, String password) {
        final Optional<Account> accountOptional = accountRepository.findOneByUserId(id);

        if (!accountOptional.isPresent()) {
            throw new IdNotFoundException("존재하지 않는 ID입니다.");
        }

        if (!passwordEncoder.matches(password, accountOptional.get().getUserPasswordHash())) {
            throw new InvalidPasswordException("패스워드가 일치하지 않습니다.");
        }
    }
}
