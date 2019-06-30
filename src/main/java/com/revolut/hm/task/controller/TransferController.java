package com.revolut.hm.task.controller;

import com.google.gson.Gson;
import com.revolut.hm.task.exception.DeleteFailedException;
import com.revolut.hm.task.exception.ResourceAlreadyExistsException;
import com.revolut.hm.task.exception.ResourceNotFoundException;
import com.revolut.hm.task.exception.ResourcesDoNotMatchException;
import com.revolut.hm.task.model.Account;
import com.revolut.hm.task.model.Transaction;
import com.revolut.hm.task.service.AccountService;

import javax.inject.Inject;

import static spark.Spark.*;

public class TransferController {

    @Inject
    private AccountService accountService;

    public void startTransferRoutes() {

        port(8080);
        Gson gson = new Gson();
        get("/health", (req, res) -> "Hello World", gson::toJson);

        handleExceptions();

        // Account rest API
        path("/accounts", () -> {
            // Get all accounts
            get("", "application/json", (request, response) ->
                    accountService.getAll(), gson::toJson);

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

                // Get one transaction of an account
                get("/:tid", "application/json", (request, response) ->
                        accountService.getTransaction(Long.parseLong(request.params(":id")), Long.parseLong(request.params(":tid"))), gson::toJson);

                // Add a transaction to an account
                post("", "application/json", (request, response) -> {
                    Transaction transaction = gson.fromJson(request.body(), Transaction.class);
                    return accountService.addTransaction(Long.parseLong(request.params(":id")), transaction);
                }, gson::toJson);

                // Update a transaction of An account
                put("", "application/json", (request, response) -> {
                    Transaction transaction = gson.fromJson(request.body(), Transaction.class);
                    return accountService.updateTransaction(Long.parseLong(request.params(":id")), transaction);
                }, gson::toJson);

                // Delete a transaction of an account
                delete("/:tid", "application/json", (request, response) -> {
                    accountService.deleteTransaction(Long.parseLong(request.params(":id")), Long.parseLong(request.params(":tid")));
                    return "OK";
                });

            });

        });

    }

    private void handleExceptions() {
        // Using string/html
        notFound("<html><body><h1>Custom 404 handling</h1></body></html>");

        // Using string/html
        internalServerError("<html><body><h1>Custom 500 handling</h1></body></html>");

        exception(ResourcesDoNotMatchException.class, (exception, request, response) -> {
            response.body("<html><body><h1>" +
                    exception.getResourceName1() + " with field " + exception.getFieldName1() +
                    "=" + exception.getFieldValue1() + " doest not match by " +
                    exception.getResourceName2() + " with field " + exception.getFieldName2() +
                    "=" + exception.getFieldValue2() +
                    " is already exists</h1></body></html>");
        });

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
