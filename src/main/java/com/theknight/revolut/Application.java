package com.theknight.revolut;

import com.theknight.revolut.data.Account;
import com.theknight.revolut.data.CreationStatus;
import com.theknight.revolut.data.TransferStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.math.BigDecimal;

import static com.theknight.revolut.utils.NumericUtils.parseInt;
import static com.theknight.revolut.utils.NumericUtils.parseMoney;
import static java.lang.String.format;

public class Application extends AbstractVerticle {
    public static final Logger log = LoggerFactory.getLogger(Application.class);

    private AccountManager accountManager = new AccountManager();

    @Override
    public void start() throws Exception {
        log.info("Start simple money application");

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.put("/create").handler(this::create);
        router.get("/status/:account").handler(this::status);
        router.post("/transfer").handler(this::transfer);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    private void create(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();

        Integer accountId = parseInt(request.getParam("account"));
        BigDecimal initialAmount = parseMoney(request.getParam("amount"));

        log.info(format("Received create parameters: account=%s, amount=%s", accountId, initialAmount));

        HttpServerResponse response = routingContext.response();

        CreationStatus status = accountManager.create(accountId, initialAmount);

        response.putHeader("content-type", "application/json")
                .end(new JsonObject()
                        .put("id", accountId)
                        .put("status", status)
                        .encodePrettily());
        log.info(format("Creation account %s status: %s", accountId, status));
    }

    private void status(RoutingContext routingContext) {
        Integer accountId = parseInt(routingContext.request().getParam("account"));

        log.info(format("Received parameter for status: account=%s", accountId));

        HttpServerResponse response = routingContext.response();

        if (accountId != null) {
            Account account = accountManager.getAccount(accountId);
            if (account != null) {
                response.putHeader("content-type", "application/json")
                        .end(new JsonObject()
                                .put("id", accountId)
                                .put("amount", account.getAmount().toString())
                                .encodePrettily());
            } else {
                response.putHeader("content-type", "application/json")
                        .setStatusCode(404)
                        .end(new JsonObject()
                                .put("status", "ERROR")
                                .put("reason", "NOT_FOUND")
                                .encodePrettily());
                log.error(format("Unable to found account %s", accountId));
            }
        } else {
            sendError(400, response);
        }
    }

    private void transfer(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();

        Integer fromId = parseInt(request.getParam("from"));
        Integer toId = parseInt(request.getParam("to"));
        BigDecimal amount = parseMoney(request.getParam("amount"));

        log.info(format("Received parameters for transfer from=%s, to=%s, amount=%s", fromId, toId, amount));

        HttpServerResponse response = routingContext.response();

        TransferStatus status = accountManager.transfer(fromId, toId, amount);

        response.putHeader("content-type", "application/json")
                .end(new JsonObject()
                        .put("from", fromId)
                        .put("to", toId)
                        .put("status", status)
                        .encodePrettily());
        log.info(format("Transfer from %s to %s amount %s status: %s", fromId, toId, amount, status));
    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }
}
