package com.revolut.hm.task.service;

import com.revolut.hm.task.exception.DeleteFailedException;
import com.revolut.hm.task.exception.ResourceAlreadyExistsException;
import com.revolut.hm.task.exception.ResourceNotFoundException;
import com.revolut.hm.task.model.Account;
import com.revolut.hm.task.repository.AccountRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

public class AccountService {

    @Inject
    private AccountRepository accountRepository;

    public AccountService() {
    }

    public Account get(long id) {
        Account account = accountRepository.findById(id);
        if (account == null)
            throw new ResourceNotFoundException("Account", "id", id);
        return account;
    }

    public List<Account> listAll() {
        return accountRepository.findAll();
    }

    /**
     * @throws ResourceAlreadyExistsException if the entity exists.
     */
    @Transactional
    public Account add(Account accountDetails) throws ResourceAlreadyExistsException {
        Account account = accountRepository.findByAccountNumberOrId(accountDetails.getAccountNumber(), accountDetails.getId());
        if (account != null) {
            if (account.getId() == accountDetails.getId())
                throw new ResourceAlreadyExistsException("Account", "id", accountDetails.getId());
            else
                throw new ResourceAlreadyExistsException("Account", "accountNumber", accountDetails.getAccountNumber());
        }
        return accountRepository.save(accountDetails);
    }

    /**
     * @throws ResourceNotFoundException if the entity exists.
     */
    @Transactional
    public Account update(Account accountDetails) throws ResourceNotFoundException {
        Account account = get(accountDetails.getId());

        account.setAccountNumber(accountDetails.getAccountNumber());
        account.setBalance(accountDetails.getBalance());

        Account updatedAccount = accountRepository.save(account);
        return updatedAccount;
    }

    /**
     * @throws ResourceNotFoundException if the entity exists.
     */
    @Transactional
    public void delete(Long id) throws ResourceNotFoundException, DeleteFailedException {
        Account account = get(id);
        try {
            accountRepository.delete(account);
        } catch (Exception e) {
            throw new DeleteFailedException("Account", "id", id);
        }
    }

}