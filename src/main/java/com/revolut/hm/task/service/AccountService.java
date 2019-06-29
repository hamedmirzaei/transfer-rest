package com.revolut.hm.task.service;

import com.revolut.hm.task.exception.ResourceAlreadyExistsException;
import com.revolut.hm.task.model.Account;
import com.revolut.hm.task.repository.AccountRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

public class AccountService {

    @Inject
    private AccountRepository accountRepository;

    public AccountService() {
    }

    public Account get(long id) {
        return accountRepository.findOne(id);
    }

    public List<Account> listAll() {
        return accountRepository.findAll();
    }

    /**
     * @throws ResourceAlreadyExistsException if the entity exists.
     */
    @Transactional
    public void add(Account accountDetails) {
        Account account = accountRepository.findByAccountNumberOrId(accountDetails.getAccountNumber(), accountDetails.getId());
        if (account != null)
            throw new ResourceAlreadyExistsException("Account", "<id, accountNumber>",
                    "<" + accountDetails.getId() + ", " + accountDetails.getAccountNumber() + ">");
        accountRepository.save(accountDetails);
    }

    /**
     * @throws ResourceAlreadyExistsException if one of the entities exists.
     */
    @Transactional
    public void addAll(Collection<Account> accounts) {
        for (Account account : accounts)
            add(account);
    }

}