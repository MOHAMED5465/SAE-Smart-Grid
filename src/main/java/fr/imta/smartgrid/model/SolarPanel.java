package fr.imta.smartgrid.model;

import io.vertx.core.json.JsonObject;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "solar_panel")
@PrimaryKeyJoinColumn(name = "id")
public class SolarPanel extends Producer {
    private float efficiency;

    public float getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(float efficiency) {
        this.efficiency = efficiency;
    }

    @Override
    public JsonObject toJSON() {
        JsonObject res = super.toJSON();          // reuse the parent part
        res.put("efficiency", this.getEfficiency());
        System.out.println("from SolarPanel");
        return res;
    }

}
