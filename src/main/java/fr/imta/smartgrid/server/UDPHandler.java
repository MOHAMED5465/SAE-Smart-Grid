package fr.imta.smartgrid.server;

import fr.imta.smartgrid.model.DataPoint;
import fr.imta.smartgrid.model.Measurement;
import fr.imta.smartgrid.model.SolarPanel;
import io.vertx.core.Handler;
import io.vertx.core.datagram.DatagramPacket;
import jakarta.persistence.EntityManager;

public class UDPHandler implements Handler<DatagramPacket> {
    EntityManager db;
    public UDPHandler(EntityManager db) {
        this.db = db;
    }
    @Override
    public void handle(DatagramPacket packet) {
        String payload = packet.data().toString();
                // Parsing du message
                try {
                    // Découpage du message selon le format "id:temperature:power:timestamp"
                    String[] parts = payload.split(":");
        
                    // Vérification du nombre de segments
                    if (parts.length != 4) {
                        throw new IllegalArgumentException(
                            "Format invalide. Attendu : id:temperature:power:timestamp"
                        );
                    }
                    
                    // Conversion des valeurs avec vérification des types
                    int solarPannelId = Integer.parseInt(parts[0]);         // -> int
                    double temperature = Double.parseDouble(parts[1]); // -> double
                    double power = Double.parseDouble(parts[2]);       // -> double
                    long timestamp = Long.parseLong(parts[3]);         // -> long
                    if(db.find(SolarPanel.class,solarPannelId)==null){
                        System.out.println("Erreur 404 id not found for a solar pannel");
                    }else{
                        db.getTransaction().begin();
                        try{
                            Double energy_produced=power*60;
                            int id_total_energy_produced = (int) db.createNativeQuery("SELECT m.id FROM measurement m JOIN sensor s ON s.id = m.sensor WHERE m.name = ? and s.id=?")
                                .setParameter(1, "total_energy_produced") 
                                .setParameter(2,solarPannelId) 
                                .getSingleResult();                    
                            Double total_energy_produced=(Double)db.createNativeQuery("SELECT d.value FROM datapoint d WHERE d.measurement=? ORDER BY d.id desc LIMIT 1")
                                    .setParameter(1, id_total_energy_produced)
                                    .getSingleResult();
                            int id_temperature = (int) db.createNativeQuery("SELECT m.id FROM measurement m JOIN sensor s ON s.id = m.sensor WHERE m.name = ? and s.id=? ")
                                .setParameter(1, "temperature")
                                .setParameter(2, solarPannelId) 
                                .getSingleResult();
                            int id_power = (int) db.createNativeQuery("SELECT m.id FROM measurement m JOIN sensor s ON s.id = m.sensor WHERE m.name = ? and s.id=?")
                                .setParameter(1, "power")
                                .setParameter(2, solarPannelId)  
                                .getSingleResult();
                            total_energy_produced+=energy_produced ;    
                            Measurement measurement_temperature=db.find(Measurement.class,id_temperature);
                            Measurement measurement_power=db.find(Measurement.class,id_power);
                            Measurement measurement_total_energy_produced=db.find(Measurement.class,id_total_energy_produced);                                    
                            DataPoint d_temperature= new DataPoint(timestamp,temperature,measurement_temperature);
                            DataPoint d_power=new DataPoint(timestamp,power,measurement_power);
                            DataPoint d_total_energy_produced=new DataPoint(timestamp,total_energy_produced,measurement_total_energy_produced);
                            db.persist(d_temperature);
                            db.persist(d_power);
                            db.persist(d_total_energy_produced);
                            measurement_temperature.getDatapoints().add(d_temperature);
                            db.merge(measurement_temperature);
                            measurement_power.getDatapoints().add(d_power);
                            db.merge(measurement_power);
                            measurement_total_energy_produced.getDatapoints().add(d_total_energy_produced);
                            db.merge(measurement_total_energy_produced);
                            db.getTransaction().commit();
                            System.out.println("status: succes");
                        }catch (RuntimeException e) {
                            db.getTransaction().rollback();
                            throw e;
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("500 Conversion numérique échouée : " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.err.println("500 " + e.getMessage());
                }
        }    
}
