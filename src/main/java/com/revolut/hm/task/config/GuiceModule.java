package com.revolut.hm.task.config;

import com.google.code.guice.repository.configuration.ScanningJpaRepositoryModule;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.revolut.hm.task.controller.TransferController;
import com.revolut.hm.task.model.Account;
import com.revolut.hm.task.repository.AccountRepository;
import com.revolut.hm.task.repository.TransactionRepository;
import com.revolut.hm.task.service.AccountService;
import com.revolut.hm.task.service.TransactionService;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class GuiceModule extends AbstractModule {

    private String persistenceUnitName;
    private String repositoriesBasePackageName;

    public GuiceModule(String persistenceUnitName, String repositoriesBasePackageName) {
        System.out.println("persistenceUnitName: " + persistenceUnitName);
        System.out.println("repositoriesBasePackageName: " + repositoriesBasePackageName);
        this.persistenceUnitName = persistenceUnitName;
        this.repositoriesBasePackageName = repositoriesBasePackageName;
    }

    @Override
    protected void configure() {
        // Repository classes auto-scanned by package name
        install(new ScanningJpaRepositoryModule(repositoriesBasePackageName, persistenceUnitName));

        //bind(AccountService.class).in(Scopes.SINGLETON);
        //bind(TransferController.class).in(Scopes.SINGLETON);
        //bind(AccountRepository.class).in(Scopes.SINGLETON);

    }

}