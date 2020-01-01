package fr.sledunois.inventory.impl;

import fr.sledunois.inventory.InventoryService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

public class InventoryServiceImpl implements InventoryService {
  @Override
  public InventoryService getProducts(Handler<AsyncResult<JsonArray>> handler) {
    System.out.println("getProducts method in InventoryService");

    handler.handle(Future.succeededFuture(new JsonArray().add("First product").add("Second product")));
    return this;
  }
}
