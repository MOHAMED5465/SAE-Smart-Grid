package fr.imta.smartgrid.server;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;
import fr.imta.smartgrid.model.*;
public class DeletePersonHandler implements Handler<RoutingContext> {
    EntityManager db;

    public DeletePersonHandler(EntityManager db) {
        this.db = db;
    }
    
    @Override
    public void handle(RoutingContext event) {
        int person_id = Integer.valueOf(event.pathParam("id"));
        Person person = db.find(Person.class, person_id);
        if(person == null){
            event.end("404");
        }
        else{
            db.getTransaction().begin();
            try{
                for(Sensor sensor : person.getSensors()){
                    sensor.getOwners().remove(person);
                    db.merge(sensor);
                }

                person.getSensors().clear();

                Grid grid = person.getGrid();
                if(grid != null){
                    grid.getPersons().remove(person);
                    db.merge(grid);
                }
                db.remove(person);
                db.getTransaction().commit();
                event.end("200");
            }catch(RuntimeException e){
                db.getTransaction().rollback();
                event.end("500");
                throw e;
            }

        }
    }
}

