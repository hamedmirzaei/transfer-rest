package com.revolut.hm.task.service;

import com.revolut.hm.task.exception.ResourceAlreadyExistsException;
import com.revolut.hm.task.exception.ResourceNotFoundException;
import com.revolut.hm.task.model.Transaction;
import com.revolut.hm.task.repository.TransactionRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

public class TransactionService {

    private TransactionRepository transactionRepository;

    @Inject
    public TransactionService(TransactionRepository transactionRepository) {
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

}