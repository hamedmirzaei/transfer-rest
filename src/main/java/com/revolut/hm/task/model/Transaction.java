package com.revolut.hm.task.model;

import javax.persistence.*;

@Entity
@Table(name = "TRANSACTION")
public class Transaction {

    @Id
    @Column(name = "TRANSACTION_ID")
    private Long id;

    @Column(name = "TRANSACTION_AMOUNT")
    private Long transactionAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    private Account account;

    public Transaction() {
    }

    public Transaction(Long transactionAmount, Account account) {
        this.transactionAmount = transactionAmount;
        this.account = account;
    }

    public Transaction(Long id, Long transactionAmount, Account account) {
        this.id = id;
        this.transactionAmount = transactionAmount;
        this.account = account;
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", transactionAmount=" + transactionAmount +
                ", account=" + account +
                '}';
    }

}
