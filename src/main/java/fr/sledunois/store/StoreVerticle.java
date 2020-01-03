package fr.sledunois.store;

import fr.sledunois.common.BaseMicroService;
import fr.sledunois.inventory.InventoryService;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.servicediscovery.types.EventBusService;

public class StoreVerticle extends BaseMicroService {

  private InventoryService inventoryService;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start();

    /*
      Creates router. We only add a new route that will retrieve products from
      product microservice.
     */
    Router router = Router.router(vertx);
    router.get("/").handler(this::getProducts);
    router.get("/records").handler(this::getRecords);

    /*
      Retrieve InventoryClass microservice interface using service discovery
     */
    EventBusService.getProxy(discovery, InventoryService.class, ar -> {
      if (ar.failed()) {
        System.out.println("Failed to retrieve InventoryService Proxy: " + ar.cause());
      } else {
        inventoryService = ar.result();

        // Creates HTTP server used to expose product microservice response
        vertx.createHttpServer()
          .requestHandler(router)
          .listen(8000, http -> {
            if (http.succeeded()) {
              System.out.println("Server listening on 8000 port");
            }
          });
      }
    });
  }

  private void getRecords(RoutingContext rc) {
    rc.response().end(registeredRecords.toString());
  }

  private void getProducts(RoutingContext rc) {
    /*
      Retrieve products and render the response to the client
      We use product microservice we previously retrieve from the service discovery
     */
    circuitBreaker.executeWithFallback(future -> inventoryService.getProducts(future), t -> new JsonArray())
      .setHandler(ar -> rc.response().end(ar.result().encodePrettily()));
  }
}
