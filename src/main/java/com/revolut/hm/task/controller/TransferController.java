package com.revolut.hm.task.controller;

import com.revolut.hm.task.service.AccountService;

import javax.inject.Inject;

import static spark.Spark.get;
import static spark.Spark.port;

public class TransferController {

    @Inject
    private AccountService accountService;

    public void startTransferRoutes() {
        port(8080);

        get("/health", (req, res) -> "Hello World");

        get("/accounts", (request, response) -> {
            return accountService.listAll();
        });

    }
}
