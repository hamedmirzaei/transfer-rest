package com.revolut.hm.task.controller;

import com.google.gson.Gson;
import com.revolut.hm.task.exception.DeleteFailedException;
import com.revolut.hm.task.exception.ResourceAlreadyExistsException;
import com.revolut.hm.task.exception.ResourceNotFoundException;
import com.revolut.hm.task.model.Account;
import com.revolut.hm.task.service.AccountService;
import com.revolut.hm.task.service.TransactionService;

import javax.inject.Inject;

import static spark.Spark.*;

public class TransferController {

    @Inject
    private AccountService accountService;

    @Inject
    private TransactionService transactionService;

    public void startTransferRoutes() {

        port(8080);
        Gson gson = new Gson();
        get("/health", (req, res) -> "Hello World", gson::toJson);

        exceptionHandling();

        // Account rest API
        path("/accounts", () -> {
            // Get all accounts
            get("", "application/json", (request, response) ->
                    accountService.listAll(), gson::toJson);

            // Get an account
            get("/:id", "application/json", (request, response) ->
                    accountService.get(Long.parseLong(request.params(":id"))), gson::toJson);

            // Save an account
            post("", "application/json", (request, response) -> {
                Account account = gson.fromJson(request.body(), Account.class);
                return accountService.add(account);
            }, gson::toJson);

            // Update an account
            put("", "application/json", (request, response) -> {
                Account account = gson.fromJson(request.body(), Account.class);
                return accountService.update(account);
            }, gson::toJson);

            // Delete an account
            delete("/:id", "application/json", (request, response) -> {
                accountService.delete(Long.parseLong(request.params(":id")));
                return "OK";
            });

            path("/:id/transactions", () -> {
                // Get all transactions of an account
                get("", "application/json", (request, response) ->
                        accountService.get(Long.parseLong(request.params(":id"))).getTransactions(), gson::toJson);

            });

        });

        get("/transactions", (request, response) -> {
            return transactionService.listAll();
        });

    }

    private void exceptionHandling() {
        // Using string/html
        notFound("<html><body><h1>Custom 404 handling</h1></body></html>");

        // Using string/html
        internalServerError("<html><body><h1>Custom 500 handling</h1></body></html>");

        exception(ResourceAlreadyExistsException.class, (exception, request, response) -> {
            response.body("<html><body><h1>" + exception.getResourceName() + " with field " + exception.getFieldName() +
                    "=" + exception.getFieldValue() + " is already exists</h1></body></html>");
        });

        exception(ResourceNotFoundException.class, (exception, request, response) -> {
            response.body("<html><body><h1>" + exception.getResourceName() + " with field " + exception.getFieldName() +
                    "=" + exception.getFieldValue() + " does not exists</h1></body></html>");
        });

        exception(DeleteFailedException.class, (exception, request, response) -> {
            response.body("<html><body><h1>" + exception.getResourceName() + " with field " + exception.getFieldName() +
                    "=" + exception.getFieldValue() + " failed to be deleted</h1></body></html>");
        });
    }
}
