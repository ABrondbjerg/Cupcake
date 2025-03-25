package app.controllers;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {
    public static void addRoutes(Javalin app) {
    app.post("/login", ctx ->login(ctx));
    }
    public static void login(Context ctx){
        //hent form parametre
        String username= ctx.formParam("username");
        String password = ctx.formParam("password");

        // check om bruger findes i DB med de angivne username + password

        //hvis nej send tilbage til login siden med fejl besked
        //hvis ja send videre til task siden
        ctx.render ("task.html");
    }
}
