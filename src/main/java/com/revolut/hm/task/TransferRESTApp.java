package com.revolut.hm.task;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.revolut.hm.task.config.GuiceModule;
import com.revolut.hm.task.controller.ProductController;
import com.revolut.hm.task.repository.ProductRepository;

public class TransferRESTApp {

    private static final String TEST_PERSISTENCE_UNIT_NAME = "transferRestDB";
    private static final String REPOSITORIES_BASE_PACKAGE_NAME = ProductRepository.class.getPackage().getName();

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(Stage.PRODUCTION,
                new GuiceModule(TEST_PERSISTENCE_UNIT_NAME, REPOSITORIES_BASE_PACKAGE_NAME));
        ProductController productController = injector.getInstance(ProductController.class);
        productController.startRoutes();
    }
}
