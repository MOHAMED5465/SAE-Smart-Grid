package fr.imta.smartgrid.server;
import io.vertx.core.json.JsonArray;
import fr.imta.smartgrid.model.*;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

import java.util.List;

public class GridsHandler implements Handler<RoutingContext> {
    EntityManager db;

    public GridsHandler(EntityManager db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext event) {
        List<Integer> gridIds = (List<Integer>) db.createNativeQuery("SELECT id FROM grid").getResultList();
        JsonArray array = new JsonArray(gridIds);
        event.json(array);

    }
    
}
