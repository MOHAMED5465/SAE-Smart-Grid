package fr.imta.smartgrid.model;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonObject;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "measurement")
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String unit;

    private String name;

    @ManyToOne
    @JoinColumn(name = "sensor")
    private Sensor sensor;

    @OneToMany(mappedBy = "measurement")
    private List<DataPoint> datapoints = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public List<DataPoint> getDatapoints() {
        return datapoints;
    }

    public void setDatapoints(List<DataPoint> datapoints) {
        this.datapoints = datapoints;
    } 
    public JsonObject toJSON(){
        JsonObject res = new JsonObject();
        res.put("id",this.getId());
        res.put("sensor",this.getSensor().getId());
        res.put("name",this.getName());
        res.put("unit",this.getUnit());
        return res;
    }
    public JsonObject toJSONValues() {
        JsonObject res = new JsonObject();
        List<DataPoint> datapoints=this.getDatapoints();
        res.put("sensor_id",this.getSensor().getId());
        res.put("measurement_id",this.getId());
        ArrayList<JsonObject> datapoints1 = new ArrayList<>();
        int n=this.datapoints.size();
        for(int i=0;i<n;i++){
            datapoints1.add(datapoints.get(i).toJSON());
        }
        res.put("values",datapoints1);
        return res;
    }    
}
