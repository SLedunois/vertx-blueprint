package fr.sledunois.inventory.impl;

import fr.sledunois.inventory.InventoryService;
import fr.sledunois.inventory.InventoryVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;

public class InventoryServiceImpl implements InventoryService {
  private Vertx vertx;

  public InventoryServiceImpl(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public InventoryService getProducts(Handler<AsyncResult<JsonArray>> handler) {
    System.out.println("getProducts method in InventoryService");

    // Timer could not be less than 1ms
    long timeout = 1;
    if (InventoryVerticle.slowMode) {
      System.out.println("Slow mode enabled");
      timeout = 5000;
    }

    vertx.setTimer(timeout, timer -> {
      handler.handle(Future.succeededFuture(new JsonArray().add("First product").add("Second product")));
    });

    return this;
  }
}
