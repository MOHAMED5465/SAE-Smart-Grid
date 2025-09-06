package fr.imta.smartgrid.server;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.util.ArrayList;
import java.util.List;
import fr.imta.smartgrid.model.*;
public class AddPersonHandler implements Handler<RoutingContext> {
    EntityManager db;

    public AddPersonHandler(EntityManager db) {
        this.db = db;
    }
    
    @Override
    public void handle(RoutingContext event) {
        JsonObject Info = event.body().asJsonObject();
        String firstName = Info.getString("first_name");
        System.out.println("First name: " + firstName);
        String lastName = Info.getString("last_name");
        System.out.println("Last name: " + lastName);
        Integer gridId = Info.getInteger("grid");
        System.out.println("Grid id: " + gridId);
        JsonArray ownedSensorsIds = Info.getJsonArray("owned_sensors");
        System.out.println("Owned sensors ids: " + ownedSensorsIds);
        if(Info == null || firstName == null || lastName == null || gridId == null ){
            event.json("500");
        }
        else{
            db.getTransaction().begin();
            try{
                Grid grid = db.find(Grid.class, gridId);
                System.out.println("Grid found: " + grid);
                List<Sensor> ownedSensors = new ArrayList<Sensor>();
                System.out.println("Owned sensors: " + ownedSensors);
                Person person = new Person(firstName, lastName, grid, ownedSensors);
                db.persist(person);
                if(ownedSensorsIds != null){
                    for (int i = 0; i < ownedSensorsIds.size();i++){
                        Sensor sensor = db.find(Sensor.class, ownedSensorsIds.getInteger(i));
                        person.getSensors().add(sensor);
                        sensor.getOwners().add(person);
                        db.merge(sensor);
                    }
                }
                db.merge(person);
                db.getTransaction().commit();
                JsonObject res = new JsonObject();
                res.put("id",person.getId());
                event.json(res);
            }catch(RuntimeException e){
                db.getTransaction().rollback();
                throw e;
            }

        }
    }
}

