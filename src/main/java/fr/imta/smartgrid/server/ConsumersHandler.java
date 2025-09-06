package fr.imta.smartgrid.server;
import io.vertx.core.json.JsonArray;
import fr.imta.smartgrid.model.*;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.stream.Collectors;

public class ConsumersHandler implements Handler<RoutingContext> {
    EntityManager db;

    public ConsumersHandler(EntityManager db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext event) {
        List<Consumer> consumers = (List<Consumer>) db.createQuery("SELECT c from Consumer c",Consumer.class).getResultList();
        JsonArray array = new JsonArray(consumers.stream().map(Consumer::toJSON).collect(Collectors.toList()));
        event.json(array);
    }
    
}
