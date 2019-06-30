# REST API using SparkJava + Google Guice (Inject and Repository) + HSQLDB + Hibernate
This is a simple financial business Rest API implemented using SparkJava + Google Guice (Inject and Repository) + HSQLDB + Hibernate.

## Libraries and Tools
* [Web Framework] [`SparkJava`](http://sparkjava.com/)
* [DI Framework] [`Google Guice`](https://github.com/google/guice)
* [ORM Framework] [`Hibernate`](https://hibernate.org/)
* [JPA Layer] [`Google Guice-Repository`](https://code.google.com/archive/p/guice-repository/wikis/DevGuide.wiki) which uses [`Spring Data JPA`](https://spring.io/projects/spring-data-jpa) as the underlying data abstraction layer
* [In-Memory Database] [`HSQLDB`](http://hsqldb.org/)
* [Automatic Test Framework] [`JUnit`](https://junit.org/junit5/)

## How it works
This demo implements a simple financial business Rest API. It has a simple data model of an `Account` with an `One-to-Many` 
relationship to `Transaction`. Through Rest API you are able to request for any `CRUD` operation. Because of the `Transactional`
annotation provided by `Google Guice Repository` all the CRUD operations are ACID-enabled. 
All the data will be stored in an In-Memory `HSQLDB` database named `transferRestDB`. 

## How to run
By simply executing `TransferRESTApp` class, the application starts listening on `localhost:8080` with the following
API:

* `GET /health`: health-check

* `GET /accounts`: get all the accounts
* `GET /accounts/:id`: get a single account
* `POST /accounts`: save an account
* `PUT /accounts`: update an account
* `DELETE /accounts/:id`: delete an account

* `GET /accounts/:id/transactions`: get transactions of an account
* `GET /accounts/:id/transactions/:tid`: get a single transaction of an account
* `POST /accounts/:id/transactions`: save a transaction of an account
* `PUT /accounts/:id/transactions`: update a transaction of an account
* `DELETE /accounts/:id/transactions/:tid`: delete a transaction of an account


