package com.revolut.hm.task.service;

import com.revolut.hm.task.exception.DeleteFailedException;
import com.revolut.hm.task.exception.ResourceAlreadyExistsException;
import com.revolut.hm.task.exception.ResourceNotFoundException;
import com.revolut.hm.task.model.Transaction;
import com.revolut.hm.task.repository.TransactionRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

public class TransactionService {

    @Inject
    private TransactionRepository transactionRepository;

    public TransactionService() {
        this.transactionRepository = transactionRepository;
    }

    /**
     * @throws ResourceNotFoundException if the transaction does not exist.
     */
    public Transaction get(Long transactionId) throws ResourceNotFoundException {
        Transaction transaction = transactionRepository.findById(transactionId);
        if (transaction == null)
            throw new ResourceNotFoundException("Transaction", "id", transactionId);
        return transaction;
    }

    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }

    /**
     * @throws ResourceAlreadyExistsException if the transaction exists.
     */
    @Transactional
    public Transaction add(Transaction transactionDetails) throws ResourceAlreadyExistsException {
        Transaction transaction = transactionRepository.findById(transactionDetails.getId());
        if (transaction != null) {
            throw new ResourceAlreadyExistsException("Transaction", "id", transactionDetails.getId());
        }
        return transactionRepository.save(transactionDetails);
    }

    /**
     * @throws ResourceNotFoundException if the transaction does not exist.
     */
    @Transactional
    public Transaction update(Transaction transactionDetails) throws ResourceNotFoundException {
        Transaction transaction = get(transactionDetails.getId());

        transaction.setTransactionAmount(transactionDetails.getTransactionAmount());
        transaction.setAccount(transactionDetails.getAccount());

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return updatedTransaction;
    }

    /**
     * @throws ResourceNotFoundException if the transaction does not exist.
     * @throws DeleteFailedException     if the delete operation failed.
     */
    @Transactional
    public void delete(Long transactionId) throws ResourceNotFoundException, DeleteFailedException {
        Transaction transaction = get(transactionId);
        try {
            transactionRepository.delete(transaction);
        } catch (Exception e) {
            throw new DeleteFailedException("Transaction", "id", transactionId);
        }
    }

    @Transactional
    public void deleteAll() {
        transactionRepository.deleteAll();
    }
}