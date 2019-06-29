package com.revolut.hm.task.repository;

import com.revolut.hm.task.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    public Account findByAccountNumberOrId(String accountNumber, Long id);

}
