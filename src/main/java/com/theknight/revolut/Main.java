package com.theknight.revolut;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.util.function.Consumer;

public class Main {

    public static void main(String[] args) {
        runApplication();
    }

    private static void runApplication() {

        VertxOptions options = new VertxOptions();

        System.setProperty("vertx.cwd", "./build");
        Consumer<Vertx> runner = vertx -> {
            try {
                vertx.deployVerticle("com.theknight.revolut.Application");
            } catch (Throwable t) {
                t.printStackTrace();
            }
        };
        Vertx vertx = Vertx.vertx(options);
        runner.accept(vertx);

    }
}
