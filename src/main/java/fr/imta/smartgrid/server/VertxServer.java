package fr.imta.smartgrid.server;

import java.util.HashMap;
import java.util.Map;
import io.vertx.core.datagram.DatagramSocket;
import org.eclipse.persistence.config.TargetServer;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import io.vertx.ext.web.handler.BodyHandler;
import static org.eclipse.persistence.config.PersistenceUnitProperties.*;

public class VertxServer {
    private Vertx vertx;
    private EntityManager db; // database object

    public VertxServer() {
        this.vertx = Vertx.vertx();

        // setup database connexion
        Map<String, String> properties = new HashMap<>();

        properties.put(LOGGING_LEVEL, "FINE");
        properties.put(CONNECTION_POOL_MIN, "1");

        properties.put(TARGET_SERVER, TargetServer.None);

        var emf = Persistence.createEntityManagerFactory("smart-grid", properties);
        db = emf.createEntityManager();
    }

    public void start() {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.get("/persons").handler(new PersonsHandler((this.db)));
        router.get("/grids").handler(new GridsHandler(this.db));
        router.get("/producers").handler(new ProducersHandler(this.db));
        router.get("/consumers").handler(new ConsumersHandler(this.db));
        router.get("/sensors").handler(new SensorsHandler(this.db));
        router.get("/grid/:id/production").handler(new ProductionHandler(this.db));
        router.get("/measurement/:id").handler(new MeasurementHandler(this.db));
        router.get("/measurement/:id/values").handler(new MeasurementValuesHandler(this.db));
        router.get("/grid/:id/consumption").handler(new ConsumptionHandler(this.db));
        router.get("/person/:id").handler(new PersonHandler(this.db));
        router.get("/sensor/:id").handler(new SensorHandler(this.db));
        router.get("/grid/:id").handler(new GridHandler(this.db));
        router.get("/sensors/:kind").handler(new SensorKindHandler(this.db));
        router.delete("/person/:id").handler(new DeletePersonHandler(this.db));

        router.put("/person").handler(new AddPersonHandler(this.db));

        router.post("/person/:id").handler(new UpdatePersonHandler(this.db));
        router.post("/ingress/windturbine").handler(new WindTurbineHandler(db));
        router.post("/sensor/:id").handler(new UpdateSensorHandler(this.db));

        router.route().handler(ctx -> {
            System.out.println("Incoming request: " + ctx.request().method() + " " + ctx.request().uri());
            ctx.next();
        });
        // start the server
        vertx.createHttpServer().requestHandler(router).listen(8080);

        // create a UDP socket
        DatagramSocket socket = vertx.createDatagramSocket();
        // register a handler for this server
        socket.handler(new UDPHandler(this.db));

        // start the server to listen on all interfaces on port 12345
        socket.listen(12345, "0.0.0.0");
    }

    public static void main(String[] args) {
        new VertxServer().start();
    }
}
