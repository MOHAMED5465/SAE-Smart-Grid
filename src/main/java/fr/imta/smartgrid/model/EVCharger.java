package fr.imta.smartgrid.model;

import io.vertx.core.json.JsonObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "ev_charger")
@PrimaryKeyJoinColumn(name = "id")
public class EVCharger extends Consumer {
    private int voltage;
    private int maxAmp;

    @Column(name = "connector_type")
    private String type;

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public int getMaxAmp() {
        return maxAmp;
    }

    public void setMaxAmp(int maxAmp) {
        this.maxAmp = maxAmp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public JsonObject toJSON() {
        JsonObject res = super.toJSON();        // reuse the parent part
        res.put("type", this.getType());
        res.put("voltage", this.getVoltage());
        res.put("maxAmp", this.getMaxAmp());
        return res;
    }
}
