package fr.imta.smartgrid.server;
import io.vertx.core.json.JsonArray;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

import java.util.List;

public class PersonsHandler implements Handler<RoutingContext> {
    EntityManager db;

    public PersonsHandler(EntityManager db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext event) {
        List<Integer> personIds = (List<Integer>) db.createNativeQuery("SELECT id FROM person").getResultList();
        JsonArray array = new JsonArray(personIds);
        event.json(array);
    }
}
