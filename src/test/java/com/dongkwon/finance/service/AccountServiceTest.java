package com.dongkwon.finance.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.dongkwon.finance.FinanceApplication;
import com.dongkwon.finance.domain.Account;
import com.dongkwon.finance.exception.IdAlreadyUsedException;
import com.dongkwon.finance.exception.IdNotFoundException;
import com.dongkwon.finance.exception.InvalidPasswordException;
import com.dongkwon.finance.repository.AccountRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FinanceApplication.class)
@Transactional
public class AccountServiceTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void testRegisterAccount() {
        // when
        accountService.registerAccount("someId", "somePassword");

        // then
        List<Account> allAcounts = accountRepository.findAll();
        assertThat(allAcounts.size()).isEqualTo(1);
        assertThat(allAcounts.get(0).getUserId()).isEqualTo("someId");
        assertThat(passwordEncoder.matches("somePassword", allAcounts.get(0).getUserPasswordHash())).isTrue();
    }

    @Test(expected = IdAlreadyUsedException.class)
    public void testRegisterAccountWithAlreadyRegisteredId() {
        // given
        accountService.registerAccount("aaa", "bbb");

        // when
        accountService.registerAccount("aaa", "bbb");

        // then
        // Expect IdAlreadyUsedException to be thrown
    }

    @Test
    public void testValidateAccount() {
        // given
        accountService.registerAccount("ccc", "ddd");

        // when
        accountService.validateAccount("ccc", "ddd");

        // then
        // Success if no exception is thrown
    }

    @Test(expected = IdNotFoundException.class)
    public void testValidateAccountNotPresent() {
        // when
        accountService.validateAccount("eee", "fff");

        // then
        // Expect IdNotFoundException to be thrown
    }

    @Test(expected = InvalidPasswordException.class)
    public void testValidateAccountInvalidPassword() {
        // given
        accountService.registerAccount("ggg", "hhh");

        // when
        accountService.validateAccount("ggg", "zzz");

        // then
        // Expect InvalidPasswordException to be thrown
    }
}
