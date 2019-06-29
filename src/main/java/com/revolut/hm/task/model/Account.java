package com.revolut.hm.task.model;

import javax.persistence.*;

@Entity
@Table(name = "ACCOUNT")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ACCOUNT_ID")
    private Long id;

    @Column(name = "ACCOUNT_NUMBER", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "BALANCE")
    private Long balance;

    public Account() {
    }

    public Account(Long id, String accountNumber, Long balance) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public Account(String accountNumber, Long balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                '}';
    }
    
}
