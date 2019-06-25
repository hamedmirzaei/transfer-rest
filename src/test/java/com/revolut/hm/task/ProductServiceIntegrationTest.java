package com.revolut.hm.task;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import com.revolut.hm.task.config.GuiceModule;
import com.revolut.hm.task.model.Product;
import com.revolut.hm.task.repository.ProductRepository;
import com.revolut.hm.task.service.ProductService;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

public class ProductServiceIntegrationTest {
    private static final String TEST_PERSISTENCE_UNIT_NAME = "transferRestDB";
    private static final String REPOSITORIES_BASE_PACKAGE_NAME = ProductRepository.class.getPackage().getName();

    private ProductService productService;
    private ProductRepository productRepository;

    public ProductServiceIntegrationTest() {
        Injector injector = createInjector();
        productService = injector.getInstance(ProductService.class);
        productRepository = injector.getInstance(ProductRepository.class);
    }

    private static Injector createInjector() {
        return Guice.createInjector(Stage.DEVELOPMENT,
                new GuiceModule(TEST_PERSISTENCE_UNIT_NAME, REPOSITORIES_BASE_PACKAGE_NAME));
    }

    @Before
    public void clear() {
        productRepository.deleteAll();
    }

    @Test
    public void testAdd() {
        productService.add(new Product(1l, "Ligth bulb"));
        productService.add(new Product(2l, "Dijon Mustarde"));

        assertNotNull(productService.get(1l));
        assertNotNull(productService.get(2l));
        assertNull(productService.get(3l));

    }

    @Test
    public void testFilter() {
        productService.add(new Product(1l, "Apple"));
        productService.add(new Product(2l, "Strawberry"));
        productService.add(new Product(3l, "Blue Berry"));
        productService.add(new Product(4l, "Lemon"));

        List<Product> filteredProducts = productService.filter("berry");
        assertTrue(filteredProducts.size() == 2);
        assertTrue(filteredProducts.get(0).getId() == 2l);
        assertTrue(filteredProducts.get(1).getId() == 3l);
    }

    @Test
    public void testAddAll() {
        productService.addAll(Arrays.asList(
                new Product(1l, "Book"),
                new Product(2l, "Towel"),
                new Product(3l, "Chair")
        ));

        assertNotNull(productService.get(1l));
        assertNotNull(productService.get(2l));
        assertNotNull(productService.get(3l));

    }

    @Test
    public void testAddAllRollback() {
        try {
            productService.addAll(Arrays.asList(
                    new Product(1l, "Bear"),
                    new Product(2l, "Cat"),
                    new Product(1l, "Tiger")
            ));
            fail("Expected exception for id duplication");
        } catch (RuntimeException e) {
            //Expected rollback done
            assertNull(productService.get(1l));
            assertNull(productService.get(2l));
        }
    }

}