package fr.imta.smartgrid.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.imta.smartgrid.model.DataPoint;
import fr.imta.smartgrid.model.EVCharger;
import fr.imta.smartgrid.model.Measurement;
import fr.imta.smartgrid.model.Sensor;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;
import io.vertx.core.json.JsonObject;

public class MeasurementValuesHandler implements Handler<RoutingContext> {
    EntityManager db;

    public MeasurementValuesHandler(EntityManager db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext event) {
        String idString=event.pathParam("id");
        int id = Integer.valueOf(idString);
        Measurement m=db.find(Measurement.class, id);
        event.json(m.toJSONValues());
    }
}
