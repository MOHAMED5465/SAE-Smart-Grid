package fr.imta.smartgrid.server;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class ConsumptionHandler implements Handler<RoutingContext> {
    EntityManager db;

    public ConsumptionHandler(EntityManager db) {
        this.db = db;
    }


    @Override
    public void handle(RoutingContext event) {
        int grid_id = Integer.valueOf(event.pathParam("id"));
        String sql = """
SELECT SUM(max_value) FROM (
    SELECT MAX(d.value) AS max_value
    FROM consumer c
    JOIN sensor s ON c.id = s.id
    JOIN measurement m ON c.id = m.sensor
    JOIN datapoint d ON m.id = d.measurement
    WHERE s.grid = ? AND m.name = 'total_energy_consumed'
    GROUP BY c.id
) AS per_consumption_max
""";
        double production = (double) db.createNativeQuery(sql).setParameter(1, grid_id).getSingleResult();
        event.json(production);

    }
    
}
