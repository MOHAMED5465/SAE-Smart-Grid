package fr.imta.smartgrid.server;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.util.ArrayList;
import java.util.List;
import fr.imta.smartgrid.model.*;
public class UpdateSensorHandler implements Handler<RoutingContext> {
    EntityManager db;

    public UpdateSensorHandler(EntityManager db) {
        this.db = db;
    }
    
    @Override
    public void handle(RoutingContext event) {
        JsonObject newInfo = event.body().asJsonObject();
        System.out.println(newInfo);
        int sensor_id = Integer.valueOf(event.pathParam("id"));
        Sensor sensor = db.find(Sensor.class,sensor_id);
        if(sensor == null){
            event.json("404");
        }
        else{
            db.getTransaction().begin();
            System.out.println("Sensor found: " + sensor.getId());
            try{
                String newName = newInfo.getString("name");
                String newDescription = newInfo.getString("description");
                JsonArray newOwnersIds = newInfo.getJsonArray("owners");
                List<Person> newOwners= new ArrayList<Person>();
                List<Person> oldOwners = sensor.getOwners();
                for (int i = 0; i < oldOwners.size();i++){
                    Person owner = oldOwners.get(i);
                    if(!newOwnersIds.contains(owner.getId())){
                        owner.getSensors().remove(sensor);
                        db.merge(owner);
                    }
                }
                for (int i = 0; i < newOwnersIds.size();i++){
                    Person owner = db.find(Person.class, newOwnersIds.getInteger(i));
                    if(!owner.getSensors().contains(sensor)){
                        owner.getSensors().add(sensor);
                        db.merge(owner);
                    }
                    newOwners.add(owner);
                }
                sensor.setName(newName);
                System.out.println("Sensor name updated: ");
                sensor.setDescription(newDescription);
                System.out.println("Sensor description updated: ");
                sensor.setOwners(newOwners);
                System.out.println("Sensor owners updated: ");
                if (sensor instanceof Producer){
                    String newPowerSource = newInfo.getString("power_source");
                    ((Producer)sensor).setPowerSource(newPowerSource);
                    if (sensor instanceof SolarPanel){
                        float newEfficiency = newInfo.getFloat("efficiency");
                        ((SolarPanel)sensor).setEfficiency(newEfficiency);
                    }
                    else if(sensor instanceof WindTurbine){
                    double newHeight= newInfo.getDouble("height");
                    ((WindTurbine)sensor).setHeight(newHeight);
                    double newBladeLength= newInfo.getDouble("blade_length");
                    ((WindTurbine)sensor).setBladeLength(newBladeLength);
                    }
                    System.out.println("Producer updated");
                }
                else if(sensor instanceof Consumer){
                    double newMaxPower = newInfo.getDouble("max_power");
                    ((Consumer)sensor).setMaxPower(newMaxPower);
                    if(sensor instanceof EVCharger){
                        String newType = newInfo.getString("type");
                        ((EVCharger)sensor).setType(newType);
                        int newVolatge= newInfo.getInteger("voltage");
                        ((EVCharger)sensor).setVoltage(newVolatge);
                        int newMaxAmp= newInfo.getInteger("maxAmp");
                        ((EVCharger)sensor).setMaxAmp(newMaxAmp);
                    }
                    System.out.println("Consumer updated");
                }
                db.merge(sensor);
                System.out.println("Sensor merged");
                db.getTransaction().commit();
                System.out.println("Transaction committed");
                event.json("200");

            }catch(RuntimeException e){
                db.getTransaction().rollback();
                throw e;
            }

        }
    }
}
