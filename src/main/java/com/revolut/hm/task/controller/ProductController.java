package com.revolut.hm.task.controller;

import static spark.Spark.get;
import static spark.Spark.port;

public class ProductController {
    public void startRoutes() {
        port(8080);
        get("/hello", (req, res) -> "Hello World");
    }
}
