package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool)
    {
    app.post("/login", ctx ->login(ctx, connectionPool));
    }


    public static void login(Context ctx, ConnectionPool connectionPool) {
        //hent form parametre
        String username= ctx.formParam("username");
        String password = ctx.formParam("password");

        // check om bruger findes i DB med de angivne username + password
        try {
            User user = UserMapper.login(username,password, connectionPool);
            ctx.render("task.html");

        } catch (DatabaseException e)
        {
            ctx.render("index.html");
        }

    }
}
