package fr.imta.smartgrid.server;
import io.vertx.core.json.JsonArray;
import fr.imta.smartgrid.model.*;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

public class SensorsHandler implements Handler<RoutingContext> {
    EntityManager db;

    public SensorsHandler(EntityManager db) {
        this.db = db;
    }


    @Override
    public void handle(RoutingContext event) {
        List<Sensor> sensors = (List<Sensor>) db.createQuery("SELECT s from Sensor s",Sensor.class).getResultList();
        JsonArray array = new JsonArray(sensors.stream().map(Sensor::toJSON).collect(Collectors.toList()));
        event.json(array);
    }
    
}
