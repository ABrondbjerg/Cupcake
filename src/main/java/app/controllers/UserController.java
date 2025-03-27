package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.nio.file.DirectoryNotEmptyException;

public class UserController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool)
    {
    app.post("/login", ctx ->login(ctx, connectionPool));
    app.get("createuser",ctx -> ctx.render("createuser.html"));
    app.post("createuser",ctx -> createUser(ctx,connectionPool));
    }


    private static void createUser(Context ctx, ConnectionPool connectionPool){

        String username= ctx.formParam("username");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");

        if(password1.equals(password2)) {
            try {
                UserMapper.createuser(username, password1, connectionPool);
                ctx.attribute("message", "Du er hermed oprettet med brugernavn: " + username + ". Nu skal du logge på.");
                ctx.render("index.html");

            } catch (DatabaseException e) {

                ctx.attribute("message", "Dit brugernavn findes allerede. Prøv igen, eller log ind");
                ctx.render("createuser.html");
            }

        } else {
            ctx.attribute("message", "Dine to passwords matcher ikke! Prøv igen");
            ctx.render("createuser.html");

        }


    }


    public static void login(Context ctx, ConnectionPool connectionPool) {
        //hent form parametre
        String username= ctx.formParam("username");
        String password = ctx.formParam("password");

        // check om bruger findes i DB med de angivne username + password
        try {
            User user = UserMapper.login(username,password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
            ctx.render("task.html");

        } catch (DatabaseException e)
        {
            ctx.attribute("message", e.getMessage());
            ctx.render("index.html");
        }

    }
}
