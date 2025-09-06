package fr.imta.smartgrid.server;

import fr.imta.smartgrid.model.DataPoint;
import fr.imta.smartgrid.model.EVCharger;
import fr.imta.smartgrid.model.Measurement;
import fr.imta.smartgrid.model.Sensor;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import fr.imta.smartgrid.model.Sensor;
import fr.imta.smartgrid.model.WindTurbine;
import fr.imta.smartgrid.model.SolarPanel;
import fr.imta.smartgrid.model.Producer;
import java.util.List;
import fr.imta.smartgrid.model.Consumer;
import fr.imta.smartgrid.model.EVCharger;

public class SensorKindHandler implements Handler<RoutingContext> {
    EntityManager db;

    public SensorKindHandler(EntityManager db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext event) {
        String kind = event.pathParam("kind");
        System.out.println(kind);
        if(kind.equals("SolarPanel")){
            List<Integer> gridIds = (List<Integer>) db.createNativeQuery("SELECT id FROM solar_panel").getResultList();
            JsonArray array = new JsonArray(gridIds);
            event.json(array);
        }
        else if(kind.equals("WindTurbine")){
            List<Integer> gridIds = (List<Integer>) db.createNativeQuery("SELECT id FROM wind_turbine").getResultList();
            JsonArray array = new JsonArray(gridIds);
            event.json(array);
        }
        else if(kind.equals("EVCharger")){
            List<Integer> gridIds = (List<Integer>) db.createNativeQuery("SELECT id FROM ev_charger").getResultList();
            JsonArray array = new JsonArray(gridIds);
            event.json(array);
        }
        else{
            event.end("404 Error");
        }

    }
}
