package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.CupcakeController;
import app.controllers.UserController;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {

    // private static final Logger LOGGER = Logger.getLogger(Main.class.getName()); // POTENTIELT FORKERT

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "cupcake";

    
   private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args)
    {
        // Initializing Javalin and Jetty webserver

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler ->  handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));}).start(7070);

        // Routing
        app.get("/", ctx ->  ctx.render("cupcake.html"));
        UserController.addRoutes(app,connectionPool);
        CupcakeController.addRoutes(app, connectionPool);
    }

}
