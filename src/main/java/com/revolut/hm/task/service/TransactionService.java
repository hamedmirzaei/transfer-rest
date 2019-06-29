package com.revolut.hm.task.service;

import com.revolut.hm.task.exception.ResourceAlreadyExistsException;
import com.revolut.hm.task.exception.ResourceNotFoundException;
import com.revolut.hm.task.model.Transaction;
import com.revolut.hm.task.repository.TransactionRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

public class TransactionService {

    private TransactionRepository transactionRepository;

    @Inject
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction get(long id) {
        return transactionRepository.findOne(id);
    }

    public List<Transaction> listAll() {
        return transactionRepository.findAll();
    }

    /**
     * @throws IllegalStateException if the entity exists.
     */
    @Transactional
    public void add(Transaction transaction) {
        boolean exists = transactionRepository.exists(transaction.getId());
        if (exists)
            throw new ResourceAlreadyExistsException("Transaction", "id", transaction.getId());
        transactionRepository.save(transaction);
    }

    /**
     * @throws IllegalStateException if one of the entities exists.
     */
    @Transactional
    public void addAll(Collection<Transaction> transactions) {
        transactions.parallelStream().forEach(transaction -> {
            add(transaction);
        });
    }

}