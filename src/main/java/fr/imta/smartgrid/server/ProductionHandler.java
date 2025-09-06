package fr.imta.smartgrid.server;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;


public class ProductionHandler implements Handler<RoutingContext> {
    EntityManager db;

    public ProductionHandler(EntityManager db) {
        this.db = db;
    }


    @Override
    public void handle(RoutingContext event) {
        int grid_id = Integer.valueOf(event.pathParam("id"));
        String sql = """
SELECT SUM(max_value) FROM (
    SELECT MAX(d.value) AS max_value
    FROM producer p
    JOIN sensor s ON p.id = s.id
    JOIN measurement m ON p.id = m.sensor
    JOIN datapoint d ON m.id = d.measurement
    WHERE s.grid = ? AND m.name = 'total_energy_produced'
    GROUP BY p.id
) AS per_producer_max
""";
        double production = (double) db.createNativeQuery(sql).setParameter(1, grid_id).getSingleResult();
        event.json(production);

    }
    
}
