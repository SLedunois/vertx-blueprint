package fr.sledunois;

import fr.sledunois.inventory.InventoryVerticle;
import fr.sledunois.store.StoreVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class Application {

  public static void main(String[] args) {
    VertxOptions options = new VertxOptions().setHAEnabled(true);
    Vertx.clusteredVertx(options, cluster -> {
      if (cluster.failed()) {
        System.out.println("Failed to get clustered vertx from Host " + System.getenv("HOSTNAME") + ". Cause: " + cluster.cause().toString());
        return;
      }

      Vertx vertx = cluster.result();
      DeploymentOptions verticleOptions = new DeploymentOptions().setHa(true);
      /*
      Launch application
      First we launch Inventory microservice
      Then we launch Store microservice
      As Store is using Inventory microservice, we launch InventoryVerticle in first
     */
      vertx.deployVerticle(new InventoryVerticle(), verticleOptions, ar -> vertx.deployVerticle(new StoreVerticle(), verticleOptions, ar2 -> System.out.println("Started")));
    });
  }
}
