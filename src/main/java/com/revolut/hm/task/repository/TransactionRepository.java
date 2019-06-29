package com.revolut.hm.task.repository;

import com.revolut.hm.task.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    public Transaction findById(Long id);

}
