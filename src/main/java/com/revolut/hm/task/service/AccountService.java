package com.revolut.hm.task.service;

import com.revolut.hm.task.exception.DeleteFailedException;
import com.revolut.hm.task.exception.ResourceAlreadyExistsException;
import com.revolut.hm.task.exception.ResourceNotFoundException;
import com.revolut.hm.task.exception.ResourcesDoNotMatchException;
import com.revolut.hm.task.model.Account;
import com.revolut.hm.task.model.Transaction;
import com.revolut.hm.task.repository.AccountRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

public class AccountService {

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private TransactionService transactionService;

    public AccountService() {
    }

    /**
     * @throws ResourceNotFoundException if the account does not exist.
     */
    public Account get(Long accountId) throws ResourceNotFoundException {
        Account account = accountRepository.findById(accountId);
        if (account == null)
            throw new ResourceNotFoundException("Account", "id", accountId);
        return account;
    }

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    /**
     * @throws ResourceAlreadyExistsException if the account exists.
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
     * @throws ResourceNotFoundException if the account does not exist.
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
     * @throws ResourceNotFoundException if the account does not exist.
     * @throws DeleteFailedException     if the delete operation failed.
     */
    @Transactional
    public void delete(Long accountId) throws ResourceNotFoundException, DeleteFailedException {
        Account account = get(accountId);
        try {
            accountRepository.delete(account);
        } catch (Exception e) {
            throw new DeleteFailedException("Account", "id", accountId);
        }
    }

    /**
     * @throws ResourcesDoNotMatchException if the transaction doest not belong to account
     * @throws ResourceNotFoundException    if the transaction or account does not exist.
     */
    public Transaction getTransaction(Long accountId, Long transactionId) throws ResourcesDoNotMatchException, ResourceNotFoundException {
        Account account = get(accountId);
        Transaction transaction = transactionService.get(transactionId);
        for (Transaction t : account.getTransactions()) {
            if (t.getId() == transaction.getId())
                return transaction;
        }
        throw new ResourcesDoNotMatchException("Account", "id", accountId, "Transaction", "id", transaction.getId());
    }

    /**
     * @throws ResourceNotFoundException if the account does not exist.
     */
    public List<Transaction> getAllTransactions(Long accountId) throws ResourceNotFoundException {
        Account account = get(accountId);
        return account.getTransactions();
    }

    /**
     * @throws ResourceAlreadyExistsException if the transaction exists.
     * @throws ResourceNotFoundException      if the account or transaction does not exist.
     */
    @Transactional
    public Transaction addTransaction(Long accountId, Transaction transactionDetails) throws ResourceAlreadyExistsException, ResourceNotFoundException {
        Account account = get(accountId);
        try {
            transactionService.get(transactionDetails.getId());
            throw new ResourceAlreadyExistsException("Transaction", "id", transactionDetails.getId());
        } catch (ResourceNotFoundException ex) {
            transactionDetails.setAccount(account);
            return transactionService.add(transactionDetails);
        }
    }

    /**
     * @throws ResourceNotFoundException    if the transaction or account does not exist.
     * @throws ResourcesDoNotMatchException if the transaction does not belong to account
     */
    @Transactional
    public Transaction updateTransaction(Long accountId, Transaction transactionDetails) throws ResourceNotFoundException, ResourcesDoNotMatchException {
        Account account = get(accountId);
        Transaction transaction = transactionService.get(transactionDetails.getId());

        if (transaction.getAccount().getId() != accountId)
            throw new ResourcesDoNotMatchException("Account", "id", accountId, "Transaction", "id", transactionDetails.getId());
        transaction.setTransactionAmount(transactionDetails.getTransactionAmount());

        Transaction updatedTransaction = transactionService.update(transaction);
        return updatedTransaction;
    }

    /**
     * @throws ResourceNotFoundException    if the transaction or account does not exist.
     * @throws DeleteFailedException        if the delete operation failed.
     * @throws ResourcesDoNotMatchException if the transaction does not belong to account
     */
    @Transactional
    public void deleteTransaction(Long accountId, Long transactionId) throws ResourceNotFoundException, DeleteFailedException, ResourcesDoNotMatchException {
        Account account = get(accountId);
        Transaction transaction = transactionService.get(transactionId);

        if (transaction.getAccount().getId() != accountId)
            throw new ResourcesDoNotMatchException("Account", "id", accountId, "Transaction", "id", transactionId);

        transactionService.delete(transactionId);
    }

    @Transactional
    public void deleteAll() {
        accountRepository.deleteAll();
    }
}