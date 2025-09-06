package fr.imta.smartgrid.server;
import io.vertx.core.json.JsonArray;
import fr.imta.smartgrid.model.*;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.stream.Collectors;

public class ProducersHandler implements Handler<RoutingContext> {
    EntityManager db;

    public ProducersHandler(EntityManager db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext event) {
        List<Producer> producers = (List<Producer>) db.createQuery("SELECT p from Producer p",Producer.class).getResultList();
        JsonArray array = new JsonArray(producers.stream().map(Producer::toJSON).collect(Collectors.toList()));
        event.json(array);
    }
    
}



