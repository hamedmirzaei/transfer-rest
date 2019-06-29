package com.revolut.hm.task.model;

import javax.persistence.*;

@Entity
@Table(name = "TRANSACTION")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TRANSACTION_ID")
    private Long id;

    @Column(name = "TRANSACTION_AMOUNT")
    private Long transactionAmount;

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    public Transaction() {
    }

    public Transaction(Long id, Long transactionAmount, Long accountId) {
        this.id = id;
        this.transactionAmount = transactionAmount;
        this.accountId = accountId;
    }

    public Transaction(Long transactionAmount, Long accountId) {
        this.transactionAmount = transactionAmount;
        this.accountId = accountId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Long transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", transactionAmount=" + transactionAmount +
                ", accountId=" + accountId +
                '}';
    }
    
}
