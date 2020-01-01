package fr.sledunois.common;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.types.EventBusService;

import java.util.Set;

public class BaseMicroService extends AbstractVerticle {
  private static final Logger logger = LoggerFactory.getLogger(BaseMicroService.class);

  protected ServiceDiscovery discovery;
  protected CircuitBreaker circuitBreaker;
  protected Set<Record> registeredRecords = new ConcurrentHashSet<>();

  @Override
  public void start() throws Exception {
    discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));
    CircuitBreakerOptions circuitBreakerOptions = new CircuitBreakerOptions()
      .setFallbackOnFailure(true)
      .setMaxFailures(2)
      .setResetTimeout(5000)
      .setTimeout(1000);

    circuitBreaker = CircuitBreaker.create("circuit-breaker", vertx, circuitBreakerOptions);
    circuitBreaker.openHandler(handler -> System.out.println("Circuit breaker opened"))
      .closeHandler(handler -> System.out.println("Circuit breaker closed"));
  }

  protected Future<Void> publishEventBusService(String name, String address, Class serviceClass) {
    Record record = EventBusService.createRecord(name, address, serviceClass);
    return publish(record);
  }

  private Future<Void> publish(Record record) {
    if (discovery == null) {
      try {
        start();
      } catch (Exception e) {
        throw new IllegalStateException("Cannot create discovery service");
      }
    }

    Future<Void> future = Future.future();
    //Publishing the service
    discovery.publish(record, ar -> {
      if (ar.succeeded()) {
        registeredRecords.add(record);
        logger.info("Service <" + ar.result().getName() + "> published");
        future.complete();
      } else {
        future.fail(ar.cause());
      }
    });

    return future;
  }
}
