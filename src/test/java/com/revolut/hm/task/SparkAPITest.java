package com.revolut.hm.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.revolut.hm.task.config.GuiceModule;
import com.revolut.hm.task.controller.TransferController;
import com.revolut.hm.task.model.Account;
import com.revolut.hm.task.model.Transaction;
import com.revolut.hm.task.repository.AccountRepository;
import com.revolut.hm.task.service.AccountService;
import com.revolut.hm.task.service.TransactionService;
import com.revolut.hm.task.utils.ExcludeProxiedFields;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;

public class SparkAPITest {

    private final String TEST_PERSISTENCE_UNIT_NAME = "transferRestDB";
    private final String REPOSITORIES_BASE_PACKAGE_NAME = AccountRepository.class.getPackage().getName();

    private final Type listAccountType = new TypeToken<List<Account>>() {
    }.getType();
    private final Type listTransactionType = new TypeToken<List<Transaction>>() {
    }.getType();

    private Injector injector;
    private AccountService accountService;
    private TransactionService transactionService;
    private TransferController transferController;
    private Gson gson;

    private Injector createInjector() {
        return Guice.createInjector(Stage.DEVELOPMENT,
                new GuiceModule(TEST_PERSISTENCE_UNIT_NAME, REPOSITORIES_BASE_PACKAGE_NAME));
    }

    public SparkAPITest() {
        injector = createInjector();
        accountService = injector.getInstance(AccountService.class);
        transactionService = injector.getInstance(TransactionService.class);
        transferController = injector.getInstance(TransferController.class);
        gson = new GsonBuilder().setExclusionStrategies(new ExcludeProxiedFields()).create();
    }

    @Before
    public void setUp() throws Exception {
        transactionService.deleteAll();
        accountService.deleteAll();

        Account account1 = new Account();
        account1.setId(1l);
        account1.setAccountNumber("11111");
        account1.setBalance(1000000l);
        account1.setTransactions(Arrays.asList(new Transaction(1l, 1000l, account1),
                new Transaction(2l, 2000l, account1)));
        accountService.add(account1);

        transferController.startTransferRoutes();
        awaitInitialization(); // waits for the embedded server to start
    }

    @After
    public void tearDown() throws Exception {
        stop();
    }

    @Test
    public void testHealthCheck() {
        String testUrl = "/health";

        ApiTestUtils.TestResponse res = ApiTestUtils.request("GET", testUrl, null);
        assertEquals(200, res.status);
        assertEquals("Hello World", res.body);
    }

    @Test
    public void testGetAllAccounts() {
        String testUrl = "/accounts";

        ApiTestUtils.TestResponse res = ApiTestUtils.request("GET", testUrl, null);
        assertEquals(200, res.status);

        Gson gson = new Gson();
        List<Account> accounts = gson.fromJson(res.body, listAccountType);
        assertTrue(accounts.size() == 1);
    }

    @Test
    public void testGetSingleAccount() {
        String testUrl = "/accounts/1";

        ApiTestUtils.TestResponse res = ApiTestUtils.request("GET", testUrl, null);
        assertEquals(200, res.status);

        Account account = gson.fromJson(res.body, Account.class);
        assertNotNull(account);
        assertTrue(account.getId() == 1);
    }

    @Test
    public void testGetAccountNotFound() {
        String testUrl = "/accounts/2";

        ApiTestUtils.TestResponse res = ApiTestUtils.request("GET", testUrl, null);
        assertEquals(404, res.status);
    }

    @Test
    public void testAddAccount() {
        String testUrl = "/accounts";
        Account account2 = new Account(2l, "22222", 200000l, null);

        ApiTestUtils.TestResponse res = ApiTestUtils.request("POST", testUrl, gson.toJson(account2));
        assertEquals(200, res.status);

        Account account = gson.fromJson(res.body, Account.class);
        assertNotNull(account);
        assertTrue(account.getId() == 2);
    }

    @Test
    public void testAddAccountDuplicateId() {
        String testUrl = "/accounts";
        Account account = new Account(1l, "22222", 200000l, null);

        ApiTestUtils.TestResponse res = ApiTestUtils.request("POST", testUrl, gson.toJson(account));
        assertEquals(403, res.status);
    }

    @Test
    public void testUpdateAccount() {
        String testUrl = "/accounts";
        Account account = accountService.get(1l);
        account.setBalance(2000000l);

        ApiTestUtils.TestResponse res = ApiTestUtils.request("PUT", testUrl, gson.toJson(account));
        assertEquals(200, res.status);

        Account accountUpdated = gson.fromJson(res.body, Account.class);
        assertNotNull(accountUpdated);
        assertTrue(accountUpdated.getBalance() == 2000000l);
        assertTrue(accountUpdated.getId() == 1l);
    }

    @Test
    public void testDeleteAccount() {
        String testUrl = "/accounts/1";

        ApiTestUtils.TestResponse res = ApiTestUtils.request("DELETE", testUrl, null);
        assertEquals(200, res.status);

        assertTrue(res.body.equals("OK"));
    }

    @Test
    public void testDeleteAccountNotFound() {
        String testUrl = "/accounts/2";

        ApiTestUtils.TestResponse res = ApiTestUtils.request("DELETE", testUrl, null);
        assertEquals(404, res.status);
    }

    @Test
    public void testGetAllTransactionsOfAccount() {
        String testUrl = "/accounts/1/transactions";

        ApiTestUtils.TestResponse res = ApiTestUtils.request("GET", testUrl, null);
        assertEquals(200, res.status);

        Gson gson = new Gson();
        List<Transaction> transactions = gson.fromJson(res.body, listTransactionType);
        assertTrue(transactions.size() == 2);
    }

    @Test
    public void testGetSingleTransactionOfAccount() {
        String testUrl = "/accounts/1/transactions/1";

        ApiTestUtils.TestResponse res = ApiTestUtils.request("GET", testUrl, null);
        assertEquals(200, res.status);

        Transaction transaction = gson.fromJson(res.body, Transaction.class);
        assertNotNull(transaction);
        assertTrue(transaction.getId() == 1);
    }

    @Test
    public void testGetSingleTransactionOfAccountNotFound1() {
        String testUrl = "/accounts/1/transactions/3";

        ApiTestUtils.TestResponse res = ApiTestUtils.request("GET", testUrl, null);
        assertEquals(404, res.status);
    }

    @Test
    public void testGetSingleTransactionOfAccountNotFound2() {
        String testUrl = "/accounts/2/transactions/1";

        ApiTestUtils.TestResponse res = ApiTestUtils.request("GET", testUrl, null);
        assertEquals(404, res.status);
    }

    @Test
    public void testAddTransaction() {
        String testUrl = "/accounts/1/transactions";
        Account account = accountService.get(1l);
        Transaction transaction = new Transaction(3l, 3000l, account);

        ApiTestUtils.TestResponse res = ApiTestUtils.request("POST", testUrl, gson.toJson(transaction));
        assertEquals(200, res.status);

        Account updatedAccount = accountService.get(1l);
        assertNotNull(updatedAccount);
        assertTrue(updatedAccount.getTransactions().size() == 3);
    }

    @Test
    public void testAddTransactionDuplicate() {
        String testUrl = "/accounts/1/transactions";
        Account account = accountService.get(1l);
        Transaction transaction = new Transaction(1l, 3000l, account);

        ApiTestUtils.TestResponse res = ApiTestUtils.request("POST", testUrl, gson.toJson(transaction));
        assertEquals(403, res.status);
    }

    @Test
    public void testUpdateTransaction() {
        String testUrl = "/accounts/1/transactions";
        Transaction transaction = transactionService.get(1l);

        transaction.setTransactionAmount(3000l);

        ApiTestUtils.TestResponse res = ApiTestUtils.request("PUT", testUrl, gson.toJson(transaction));
        assertEquals(200, res.status);

        Transaction transactionUpdated = gson.fromJson(res.body, Transaction.class);
        assertNotNull(transactionUpdated);
        assertTrue(transactionUpdated.getTransactionAmount() == 3000l);
    }

    @Test
    public void testUpdateTransactionDoNotMatch() {
        String testUrl = "/accounts/2/transactions";
        Transaction transaction = transactionService.get(1l);

        Account account2 = new Account();
        account2.setId(2l);
        account2.setAccountNumber("22222");
        account2.setBalance(2000000l);
        account2.setTransactions(Arrays.asList(new Transaction(3l, 3000l, account2),
                new Transaction(4l, 4000l, account2)));
        accountService.add(account2);

        ApiTestUtils.TestResponse res = ApiTestUtils.request("PUT", testUrl, gson.toJson(transaction));
        assertEquals(403, res.status);
    }

    @Test
    public void testDeleteTransaction() {
        String testUrl = "/accounts/1/transactions/1";

        ApiTestUtils.TestResponse res = ApiTestUtils.request("DELETE", testUrl, null);
        assertEquals(200, res.status);

        assertTrue(res.body.equals("OK"));
    }

}
