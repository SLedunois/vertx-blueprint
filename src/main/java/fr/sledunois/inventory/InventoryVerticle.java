package fr.sledunois.inventory;

import fr.sledunois.common.BaseMicroService;
import fr.sledunois.inventory.impl.InventoryServiceImpl;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.serviceproxy.ServiceBinder;

import static fr.sledunois.inventory.InventoryService.SERVICE_ADDRESS;
import static fr.sledunois.inventory.InventoryService.SERVICE_NAME;

public class InventoryVerticle extends BaseMicroService {

  private InventoryService inventoryService;
  public static boolean slowMode = false;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    super.start();

    Router router = Router.router(vertx);
    router.get("/slow-mode").handler(this::setSlowMode);
    /*
     Service instantiation
     */
    inventoryService = new InventoryServiceImpl(vertx);

    /*
      Register a new service binder that listen on SERVICE_ADDRESS.
      It binds actions on generated sources built with @ProxyGen and @VertxGen annotation
     */
    new ServiceBinder(vertx).setAddress(SERVICE_ADDRESS).register(InventoryService.class, inventoryService);

    /*
      Publish the service on the discovery object
     */
    publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, InventoryService.class)
      .setHandler(ar -> {
        vertx.createHttpServer()
          .requestHandler(router)
          .listen(8001, http -> {
            if (http.succeeded()) {
              System.out.println("Server listening on 8001 port");
            }
            startFuture.complete();
          });
      });
  }

  private void setSlowMode(RoutingContext rc) {
    slowMode = !slowMode;
    rc.response().end("Slow mode enabled: " + slowMode);
  }
}
