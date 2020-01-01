package fr.sledunois;

import fr.sledunois.inventory.InventoryVerticle;
import fr.sledunois.store.StoreVerticle;
import io.vertx.core.Vertx;

public class Application {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    /*
      Launch application
      First we launch Inventory microservice
      Then we launch Store microservice
      As Store is using Inventory microservice, we launch InventoryVerticle in first
     */
    vertx.deployVerticle(new InventoryVerticle(), ar -> vertx.deployVerticle(new StoreVerticle(), ar2 -> System.out.println("Started")));
  }
}
