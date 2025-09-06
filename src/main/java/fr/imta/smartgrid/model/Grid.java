package fr.imta.smartgrid.model;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonObject;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "grid")
public class Grid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "grid")
    private List<Person> persons = new ArrayList<>();

    @OneToMany(mappedBy = "grid")
    private List<Sensor> sensors = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }
public JsonObject toJson(){
        JsonObject o=new JsonObject();
        o.put("id",this.getId());
        o.put("name",this.getName());
        o.put("description",this.getDescription());
        List<Person> users = this.getPersons();
        List<Sensor>sensors=this.getSensors();
        int n=sensors.size();
        ArrayList<Integer> sensors_id=new ArrayList<>();
        for(int i=0;i<n;i++){
            sensors_id.add(sensors.get(i).getId());
        }
        o.put("sensors",sensors_id);
        int m=users.size();
        ArrayList<Integer> users_id=new ArrayList<>();
        for(int i=0;i<m;i++){
            users_id.add(users.get(i).getId());
        }
        o.put("users",users_id);
        return o;
}

    
}
