package com.revolut.hm.task.config;

import com.google.code.guice.repository.configuration.ScanningJpaRepositoryModule;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.revolut.hm.task.controller.ProductController;

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
        bind(ProductController.class).in(Scopes.SINGLETON);
    }

}