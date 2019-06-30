package com.revolut.hm.task;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.revolut.hm.task.config.GuiceModule;
import com.revolut.hm.task.exception.ResourceAlreadyExistsException;
import com.revolut.hm.task.exception.ResourceNotFoundException;
import com.revolut.hm.task.exception.ResourcesDoNotMatchException;
import com.revolut.hm.task.model.Account;
import com.revolut.hm.task.model.Transaction;
import com.revolut.hm.task.repository.AccountRepository;
import com.revolut.hm.task.service.AccountService;
import com.revolut.hm.task.service.TransactionService;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DbServicesTest {
    private final String TEST_PERSISTENCE_UNIT_NAME = "transferRestDB";
    private final String REPOSITORIES_BASE_PACKAGE_NAME = AccountRepository.class.getPackage().getName();

    private Injector injector;
    private AccountService accountService;
    private TransactionService transactionService;

    private Injector createInjector() {
        return Guice.createInjector(Stage.DEVELOPMENT,
                new GuiceModule(TEST_PERSISTENCE_UNIT_NAME, REPOSITORIES_BASE_PACKAGE_NAME));
    }

    public DbServicesTest() {
        injector = createInjector();
        accountService = injector.getInstance(AccountService.class);
        transactionService = injector.getInstance(TransactionService.class);
    }


    @Before
    public void setUp() {
        transactionService.deleteAll();
        accountService.deleteAll();
    }

    @Test
    public void testAddAccountWithoutTransaction() {

        accountService.add(new Account(1l, "11111", 100000l, null));
        accountService.add(new Account(2l, "22222", 200000l, null));

        assertNotNull(accountService.get(1l));
        assertNotNull(accountService.get(2l));
        assertTrue(transactionService.getAll().size() == 0);

    }

    @Test
    public void testAddAccountWithTransaction() {

        Account account1 = new Account();
        account1.setId(1l);
        account1.setAccountNumber("11111");
        account1.setBalance(1000000l);
        account1.setTransactions(Arrays.asList(new Transaction(1l, 1000l, account1),
                new Transaction(2l, 2000l, account1)));

        accountService.add(account1);

        assertNotNull(accountService.get(1l));
        assertNotNull(transactionService.get(1l));
        assertNotNull(transactionService.get(2l));
        assertTrue(transactionService.getAll().size() == 2);

    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetNotFoundAccount() {
        accountService.get(1l);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetNotFoundTransaction() {
        transactionService.get(1l);
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

    @Test(expected = ResourceAlreadyExistsException.class)
    public void testAddDuplicateTransaction() {
        transactionService.add(new Transaction(1l, 2000l, null));
        transactionService.add(new Transaction(1l, 2000l, null));
    }

    @Test
    public void testUpdateAccount() {
        Account account = new Account(1l, "11111", 100000l, null);
        accountService.add(account);

        account.setAccountNumber("22222");
        account.setBalance(200000l);
        accountService.update(account);

        Account updatedAccount = accountService.get(1l);
        assertTrue(updatedAccount.getAccountNumber().equals("22222"));
        assertTrue(updatedAccount.getBalance() == 200000l);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDeleteNotFoundAccount() {
        accountService.delete(1l);
    }

    @Test
    public void testDeleteAccount() {
        accountService.add(new Account(1l, "11111", 100000l, null));
        assertTrue(accountService.getAll().size() == 1);
        accountService.delete(1l);
        assertTrue(accountService.getAll().size() == 0);
    }

    @Test
    public void testGetTransactionOfAccount() {

        Account account1 = new Account();
        account1.setId(1l);
        account1.setAccountNumber("11111");
        account1.setBalance(1000000l);

        Transaction transaction1 = new Transaction(1l, 1000l, account1);
        account1.setTransactions(Arrays.asList(transaction1));

        accountService.add(account1);

        Transaction transaction = accountService.getTransaction(1l, 1l);
        assertTrue(transaction.getId() == 1l);
        assertTrue(transaction.getTransactionAmount() == 1000l);

    }

    @Test(expected = ResourcesDoNotMatchException.class)
    public void testGetTransactionOfAccountDoNotMatch() {

        Account account1 = new Account();
        account1.setId(1l);
        account1.setAccountNumber("11111");
        account1.setBalance(1000000l);
        account1.setTransactions(Arrays.asList(new Transaction(1l, 1000l, account1)));
        accountService.add(account1);

        Account account2 = new Account();
        account2.setId(2l);
        account2.setAccountNumber("22222");
        account2.setBalance(2000000l);
        account2.setTransactions(Arrays.asList(new Transaction(2l, 2000l, account2)));
        accountService.add(account2);

        accountService.getTransaction(1l, 2l);

    }

    @Test
    public void testGetAllTransactionsOfAccount() {
        Account account1 = new Account();
        account1.setId(1l);
        account1.setAccountNumber("11111");
        account1.setBalance(1000000l);
        account1.setTransactions(Arrays.asList(new Transaction(1l, 1000l, account1),
                new Transaction(2l, 2000l, account1)));
        accountService.add(account1);

        List<Transaction> transactions = accountService.getAllTransactions(1l);
        assertTrue(transactions.size() == 2);
        assertTrue(transactions.get(0).getId() == 1l);
        assertTrue(transactions.get(1).getId() == 2l);
    }

    @Test(expected = ResourceAlreadyExistsException.class)
    public void testAddExistingTransactionToAccount() {
        Account account1 = new Account();
        account1.setId(1l);
        account1.setAccountNumber("11111");
        account1.setBalance(1000000l);
        account1.setTransactions(Arrays.asList(new Transaction(1l, 1000l, account1)));
        accountService.add(account1);

        Account account2 = new Account();
        account2.setId(2l);
        account2.setAccountNumber("22222");
        account2.setBalance(2000000l);
        account2.setTransactions(Arrays.asList(new Transaction(2l, 2000l, account2)));
        accountService.add(account2);

        accountService.addTransaction(1l, new Transaction(2l, 2000l, account1));
    }

    @Test
    public void testAddTransactionToAccount() {
        Account account1 = new Account();
        account1.setId(1l);
        account1.setAccountNumber("11111");
        account1.setBalance(1000000l);
        account1.setTransactions(Arrays.asList(new Transaction(1l, 1000l, account1)));
        accountService.add(account1);

        accountService.addTransaction(1l, new Transaction(2l, 2000l, account1));

        List<Transaction> transactions = accountService.getAllTransactions(1l);
        assertTrue(transactions.size() == 2);
        assertTrue(transactions.get(0).getId() == 1l);
        assertTrue(transactions.get(1).getId() == 2l);

    }

    @Test(expected = ResourcesDoNotMatchException.class)
    public void testUpdateTransactionOfAccountDoNotMatch() {
        Account account1 = new Account();
        account1.setId(1l);
        account1.setAccountNumber("11111");
        account1.setBalance(1000000l);
        account1.setTransactions(Arrays.asList(new Transaction(1l, 1000l, account1)));
        accountService.add(account1);

        Account account2 = new Account();
        account2.setId(2l);
        account2.setAccountNumber("22222");
        account2.setBalance(2000000l);
        account2.setTransactions(Arrays.asList(new Transaction(2l, 2000l, account2)));
        accountService.add(account2);

        accountService.updateTransaction(1l, new Transaction(2l, 3000l, account2));
    }

    @Test
    public void testUpdateTransactionOfAccount() {
        Account account1 = new Account();
        account1.setId(1l);
        account1.setAccountNumber("11111");
        account1.setBalance(1000000l);
        account1.setTransactions(Arrays.asList(new Transaction(1l, 1000l, account1)));
        accountService.add(account1);

        Transaction transaction = transactionService.get(1l);
        transaction.setTransactionAmount(3000l);

        accountService.updateTransaction(1l, transaction);

        Transaction transactionUpdated = accountService.getTransaction(1l, 1l);
        assertTrue(transaction.getTransactionAmount() == 3000l);
    }

}
