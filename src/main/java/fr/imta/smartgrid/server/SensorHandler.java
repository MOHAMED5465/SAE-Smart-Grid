package fr.imta.smartgrid.server;

import fr.imta.smartgrid.model.DataPoint;
import fr.imta.smartgrid.model.EVCharger;
import fr.imta.smartgrid.model.Measurement;
import fr.imta.smartgrid.model.Sensor;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;
import io.vertx.core.json.JsonObject;
import fr.imta.smartgrid.model.Sensor;
import fr.imta.smartgrid.model.WindTurbine;
import fr.imta.smartgrid.model.SolarPanel;
import fr.imta.smartgrid.model.Producer;
import fr.imta.smartgrid.model.Consumer;
import fr.imta.smartgrid.model.EVCharger;
public class SensorHandler implements Handler<RoutingContext> {
    EntityManager db;

    public SensorHandler(EntityManager db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext event) {
        String idString = event.pathParam("id");
        int id = Integer.valueOf(idString);
        if(db.find(Sensor.class,id)==null){
            event.end("404 Error");
        }
        else{
            Sensor s=db.find(Sensor.class,id);
            if (s instanceof SolarPanel){
                event.json(((SolarPanel)s).toJSON());
            }
            else if(s instanceof WindTurbine){
                event.json(((WindTurbine)s).toJSON());
            }
            else if(s instanceof EVCharger){
                event.json(((EVCharger)s).toJSON());
            }
        }
    }
}
