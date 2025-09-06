package fr.imta.smartgrid.model;

import io.vertx.core.json.JsonObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "producer")
@PrimaryKeyJoinColumn(name = "id")
public abstract class Producer extends Sensor {
    @Column(name = "power_source")
    private String powerSource;

    public String getPowerSource() {
        return powerSource;
    }

    public void setPowerSource(String powerSource) {
        this.powerSource = powerSource;
    }

    @Override
    public JsonObject toJSON() {
        JsonObject res = super.toJSON();          // reuse the parent part
        res.put("power_source", this.getPowerSource());
        System.out.println("from producer");
        return res;
    }

}
