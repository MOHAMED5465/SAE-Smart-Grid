package fr.imta.smartgrid.server;

import fr.imta.smartgrid.model.DataPoint;
import fr.imta.smartgrid.model.EVCharger;
import fr.imta.smartgrid.model.Measurement;
import fr.imta.smartgrid.model.Sensor;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;
import io.vertx.core.json.JsonObject;
import fr.imta.smartgrid.model.Grid;
public class GridHandler implements Handler<RoutingContext> {
    EntityManager db;

    public GridHandler(EntityManager db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext event) {
        String idString = event.pathParam("id");
        int id = Integer.valueOf(idString);
        if(db.find(Grid.class,id)==null){
            event.end("404 Error");
        }
        else{
            Grid g=db.find(Grid.class,id);
            event.json(g.toJson());
        }
    }
}
