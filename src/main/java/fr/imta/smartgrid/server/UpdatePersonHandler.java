package fr.imta.smartgrid.server;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.util.ArrayList;
import java.util.List;
import fr.imta.smartgrid.model.*;
public class UpdatePersonHandler implements Handler<RoutingContext> {
    EntityManager db;

    public UpdatePersonHandler(EntityManager db) {
        this.db = db;
    }
    
    @Override
    public void handle(RoutingContext event) {
        JsonObject newInfo = event.body().asJsonObject();
        int person_id = Integer.valueOf(event.pathParam("id"));
        Person person = db.find(Person.class, person_id);
        if(person == null){
            event.json("404");
        }
        else{
            db.getTransaction().begin();
            try{
                String newFirstName = newInfo.getString("first_name");
                String newLastName = newInfo.getString("last_name");
                Integer newGridId = newInfo.getInteger("grid");
                JsonArray newOwnedSensorsIds = newInfo.getJsonArray("owned_sensors");
                if(newGridId != null){
                    Grid newGrid = db.find(Grid.class, newGridId);
                    person.setGrid(newGrid);
                };
                if(newLastName != null){
                    person.setLastName(newLastName);
                };
                if(newFirstName != null){
                    person.setFirstName(newFirstName);
                };
                if(newOwnedSensorsIds != null){
                    List<Sensor> newOwnedSensors = new ArrayList<Sensor>();
                    List<Sensor> oldOwnedSensors = person.getSensors();
                    for (int i = 0; i < oldOwnedSensors.size();i++){
                        Sensor sensor = oldOwnedSensors.get(i);
                        if(!newOwnedSensorsIds.contains(sensor.getId())){
                            sensor.getOwners().remove(person);
                            db.merge(sensor);
                        }
                    }
                    for (int i = 0; i < newOwnedSensorsIds.size();i++){
                        Sensor sensor = db.find(Sensor.class, newOwnedSensorsIds.getInteger(i));
                        if(!sensor.getOwners().contains(person)){
                            sensor.getOwners().add(person);
                            db.merge(sensor);
                        }
                        newOwnedSensors.add(sensor);
                    }
                    person.setSensors(newOwnedSensors);
                }
                db.merge(person);
                db.getTransaction().commit();
                event.json("200");
            }catch(RuntimeException e){
                db.getTransaction().rollback();
                event.json("500");
            }

        }
    }
}

