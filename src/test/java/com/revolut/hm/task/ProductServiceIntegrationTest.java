package com.revolut.hm.task;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.revolut.hm.task.config.GuiceModule;
import com.revolut.hm.task.model.Account;
import com.revolut.hm.task.repository.AccountRepository;
import com.revolut.hm.task.service.AccountService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ProductServiceIntegrationTest {
    private static final String TEST_PERSISTENCE_UNIT_NAME = "transferRestDB";
    private static final String REPOSITORIES_BASE_PACKAGE_NAME = AccountRepository.class.getPackage().getName();

    private AccountRepository accountRepository;

    public ProductServiceIntegrationTest() {
        Injector injector = createInjector();
        accountRepository = injector.getInstance(AccountRepository.class);
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
    public void testAdd() {
        accountRepository.save(new Account(1l, "12441", 1200l));
        accountRepository.save(new Account(2l, "75723", 1200l));

        assertNotNull(accountRepository.findOne(1l));
        assertNotNull(accountRepository.findOne(2l));
        assertNull(accountRepository.findOne(3l));

    }

}
