package fr.imta.smartgrid.server;
import fr.imta.smartgrid.model.DataPoint;
import fr.imta.smartgrid.model.Measurement;
import fr.imta.smartgrid.model.WindTurbine;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class WindTurbineHandler implements Handler<RoutingContext> {
    EntityManager db;
    public WindTurbineHandler(EntityManager db) {
        this.db = db;
    }
    @Override
    public void handle(RoutingContext event) {
        try{
            JsonObject body=event.body().asJsonObject();
            Integer windTurbineId=body.getInteger("windturbine");
            Long timestamp = body.getLong("timestamp");
            JsonObject data = body.getJsonObject("data");
            Double speed = data.getDouble("speed");
            Double power = data.getDouble("power");
            if(db.find(WindTurbine.class,windTurbineId)==null){
                event.end("404 error");
            }else{
                Double energy_produced=power*60;
                db.getTransaction().begin();
                try{
                    int id_total_energy_produced = (int) db.createNativeQuery("SELECT m.id FROM measurement m JOIN sensor s ON s.id = m.sensor WHERE m.name = ? and s.id=?")
                        .setParameter(1, "total_energy_produced") 
                        .setParameter(2,windTurbineId) 
                        .getSingleResult();                    
                    Double total_energy_produced=(Double)db.createNativeQuery("SELECT d.value FROM datapoint d WHERE d.measurement=? ORDER BY d.id desc LIMIT 1")
                            .setParameter(1, id_total_energy_produced)
                            .getSingleResult();
                    int id_speed = (int) db.createNativeQuery("SELECT m.id FROM measurement m JOIN sensor s ON s.id = m.sensor WHERE m.name = ? and s.id=? ")
                        .setParameter(1, "speed")
                        .setParameter(2, windTurbineId) 
                        .getSingleResult();
                    int id_power = (int) db.createNativeQuery("SELECT m.id FROM measurement m JOIN sensor s ON s.id = m.sensor WHERE m.name = ? and s.id=?")
                        .setParameter(1, "power")
                        .setParameter(2, windTurbineId)  
                        .getSingleResult();
                    total_energy_produced+=energy_produced ;    
                    Measurement measurement_speed=db.find(Measurement.class,id_speed);
                    Measurement measurement_power=db.find(Measurement.class,id_power);
                    Measurement measurement_total_energy_produced=db.find(Measurement.class,id_total_energy_produced);                                    
                    DataPoint d_speed= new DataPoint(timestamp,speed,measurement_speed);
                    DataPoint d_power=new DataPoint(timestamp,power,measurement_power);
                    DataPoint d_total_energy_produced=new DataPoint(timestamp,total_energy_produced,measurement_total_energy_produced);
                    db.persist(d_speed);
                    db.persist(d_power);
                    db.persist(d_total_energy_produced);
                    measurement_power.getDatapoints().add(d_power);
                    db.merge(measurement_power);
                    measurement_speed.getDatapoints().add(d_speed);
                    db.merge(measurement_speed);
                    measurement_total_energy_produced.getDatapoints().add(d_total_energy_produced);
                    db.merge(measurement_total_energy_produced);
                    db.getTransaction().commit();
                    JsonObject s=new JsonObject();
                    s.put("status","success");
                    event.json(s);
                }catch (RuntimeException e) {
                    db.getTransaction().rollback();
                    throw e;
                }
            }
        }catch(Exception e){
            event.end("500 error"+e.getMessage());
        }
    }
}
