package fr.imta.smartgrid.model;

import io.vertx.core.json.JsonObject;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "wind_turbine")
@PrimaryKeyJoinColumn(name = "id")
public class WindTurbine extends Producer {
    private Double height;
    private Double bladeLength;

    public Double getHeight() {
        return height;
    }
    public void setHeight(Double height) {
        this.height = height;
    }
    public Double getBladeLength() {
        return bladeLength;
    }
    public void setBladeLength(Double bladeLength) {
        this.bladeLength = bladeLength;
    }
    @Override
    public JsonObject toJSON() {
        JsonObject res = super.toJSON();          // reuse the parent part
        res.put("height", this.getHeight());
        res.put("blade_length", this.getBladeLength());
        return res;
    }

}
