# REST API using SparkJava + Google Guice (Inject and Repository) + HSQLDB + Hibernate
This is a simple financial business API implemented using SparkJava + Google Guice (Inject and Repository) + HSQLDB + Hibernate.

# Libraries and Tools
* [Web Framework] [`SparkJava`](http://sparkjava.com/)
* [DI Framework] [`Google Guice`](https://github.com/google/guice)
* [ORM Framework] [`Hibernate`](https://hibernate.org/)
* [JPA Layer] [`Google Guice-Repository`](https://code.google.com/archive/p/guice-repository/wikis/DevGuide.wiki) which uses `Spring Data JPA`
* [In-Memory Database] [`HSQLDB`](http://hsqldb.org/)
* [Automatic Test Framework] [`JUnit`](https://junit.org/junit5/)

# How it works
This demo implements a simple financial business API. It has a simple data model of an `Account` with a lot of `Transaction`s 
created around (So there is a `One-to-Many` relationship between those two).

# How to run
By simply executing `TransferRESTApp` class, the application starts listening on `localhost:8080` with the following
paths. 


