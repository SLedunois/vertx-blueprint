package fr.sledunois.inventory;

import fr.sledunois.common.BaseMicroService;
import fr.sledunois.inventory.impl.InventoryServiceImpl;
import io.vertx.core.Future;
import io.vertx.serviceproxy.ServiceBinder;

import static fr.sledunois.inventory.InventoryService.SERVICE_ADDRESS;
import static fr.sledunois.inventory.InventoryService.SERVICE_NAME;

public class InventoryVerticle extends BaseMicroService {

  private InventoryService inventoryService;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    super.start();
    /*
     Service instantiation
     */
    inventoryService = new InventoryServiceImpl();

    /*
      Register a new service binder that listen on SERVICE_ADDRESS.
      It binds actions on generated sources built with @ProxyGen and @VertxGen annotation
     */
    new ServiceBinder(vertx).setAddress(SERVICE_ADDRESS).register(InventoryService.class, inventoryService);

    /*
      Publish the service on the discovery object
     */
    publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, InventoryService.class)
      .setHandler(startFuture.completer());
  }
}
