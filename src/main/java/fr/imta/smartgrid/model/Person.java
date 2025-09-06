package fr.imta.smartgrid.model;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonObject;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "grid")
    private Grid grid;

    @ManyToMany
    @JoinTable(
            name = "person_sensor", joinColumns = @JoinColumn(name = "person_id"), inverseJoinColumns = @JoinColumn(name = "sensor_id"))
    private List<Sensor> sensors = new ArrayList<>();

    public Person(){}

    public Person(String firstName, String lastName, Grid grid, List<Sensor> sensors){
        this.firstName = firstName;
        this.lastName = lastName;
        this.grid = grid;
        this.sensors = sensors;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

public JsonObject toJson(){
        JsonObject o = new JsonObject();
        o.put("id", this.getId());
        o.put("first_name", this.getFirstName());
        o.put("last_name", this.getLastName());
        o.put("grid", this.getGrid().getId());
        List<Sensor> sensors = this.getSensors();
        int n = sensors.size();
        ArrayList<Integer> sensors_id=new ArrayList<>();
        for(int i=0; i<n; i++){
            sensors_id.add(sensors.get(i).getId());
        }
        o.put("owned_sensors",sensors_id);
        return o;
    }
}
