package fr.sledunois.inventory;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

@ProxyGen
@VertxGen
public interface InventoryService {
  /**
   * The name of the microservice
   */
  String SERVICE_NAME = "inventory-eb-service";

  /**
   * The address of the microservice
   */
  String SERVICE_ADDRESS = "service.inventory";

  @Fluent
  InventoryService getProducts(Handler<AsyncResult<JsonArray>> handler);
}
