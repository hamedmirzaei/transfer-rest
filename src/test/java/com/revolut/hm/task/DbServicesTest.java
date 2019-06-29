package com.revolut.hm.task;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.revolut.hm.task.config.GuiceModule;
import com.revolut.hm.task.exception.ResourceAlreadyExistsException;
import com.revolut.hm.task.model.Account;
import com.revolut.hm.task.model.Transaction;
import com.revolut.hm.task.repository.AccountRepository;
import com.revolut.hm.task.repository.TransactionRepository;
import com.revolut.hm.task.service.AccountService;
import com.revolut.hm.task.service.TransactionService;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DbServicesTest {
    private static final String TEST_PERSISTENCE_UNIT_NAME = "transferRestDB";
    private static final String REPOSITORIES_BASE_PACKAGE_NAME = AccountRepository.class.getPackage().getName();

    private AccountRepository accountRepository;
    private AccountService accountService;
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;

    public DbServicesTest() {
        Injector injector = createInjector();
        accountRepository = injector.getInstance(AccountRepository.class);
        accountService = injector.getInstance(AccountService.class);
        transactionRepository = injector.getInstance(TransactionRepository.class);
        transactionService = injector.getInstance(TransactionService.class);
    }

    private static Injector createInjector() {
        return Guice.createInjector(Stage.DEVELOPMENT,
                new GuiceModule(TEST_PERSISTENCE_UNIT_NAME, REPOSITORIES_BASE_PACKAGE_NAME));
    }

    @Before
    public void clear() {
        accountRepository.deleteAll();
    }

    @Test
    public void testAddAccountWithoutTransaction() {

        accountService.add(new Account(1l, "11111", 100000l, null));
        accountService.add(new Account(2l, "22222", 200000l, null));

        assertNotNull(accountService.get(1l));
        assertNotNull(accountService.get(2l));
        assertNull(accountService.get(3l));
        assertTrue(transactionService.listAll().size() == 0);

    }

    @Test
    public void testAddAccountWithTransaction() {

        Account account1 = new Account();
        account1.setId(1l);
        account1.setAccountNumber("646517323");
        account1.setBalance(2000000l);
        account1.setTransactions(Arrays.asList(new Transaction(1l, 2000l, account1),
                                               new Transaction(2l, 4000l, account1)));

        accountService.add(account1);

        assertNotNull(accountService.get(1l));
        assertNull(accountService.get(2l));
        assertNotNull(transactionService.get(1l));
        assertNotNull(transactionService.get(2l));
        assertNull(transactionService.get(3l));
        assertTrue(transactionService.listAll().size() == 2);

    }

    @Test(expected = ResourceAlreadyExistsException.class)
    public void testAddDuplicateIdAccount() {
        accountService.add(new Account(1l, "11111", 100000l, null));
        accountService.add(new Account(1l, "22222", 200000l, null));
    }

    @Test(expected = ResourceAlreadyExistsException.class)
    public void testAddDuplicateAccountNumberAccount() {
        accountService.add(new Account(1l, "11111", 100000l, null));
        accountService.add(new Account(2l, "11111", 200000l, null));
    }

}
