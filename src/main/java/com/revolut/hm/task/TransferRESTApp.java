package com.revolut.hm.task;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.revolut.hm.task.config.GuiceModule;
import com.revolut.hm.task.controller.TransferController;
import com.revolut.hm.task.repository.AccountRepository;

public class TransferRESTApp {

    private static final String TEST_PERSISTENCE_UNIT_NAME = "transferRestDB";
    private static final String REPOSITORIES_BASE_PACKAGE_NAME = "com.revolut.hm.task.repository";

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new GuiceModule(TEST_PERSISTENCE_UNIT_NAME, REPOSITORIES_BASE_PACKAGE_NAME));

        //AccountRepository accountRepository = injector.getInstance(AccountRepository.class);
        TransferController transferController = injector.getInstance(TransferController.class);
        transferController.startTransferRoutes();

    }
}
