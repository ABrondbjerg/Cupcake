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
    app.get("/admin", ctx-> {
        ensureAdmin(ctx);
        ctx.render("admin_placeholder.html");
    });
    }


    private static void createUser(Context ctx, ConnectionPool connectionPool){

        String username= ctx.formParam("username");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");


        if (!username.matches("[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")){
            ctx.attribute("message","Du skal bruge en gyldig email som brugernavn.");
            ctx.render("createuser.html");
            return;
        }



        if(password1.equals(password2)) {
            try {
                UserMapper.createuser(username, password1, "user", connectionPool);
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
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(username, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);

            System.out.println("🔹 User logged in: " + user.getUserId()); // Debugging
            System.out.println("🔹 Stored in session: " + ctx.sessionAttribute("currentUser"));

            if ("admin".equals(user.getRole())) {
                ctx.render("admin_placeholder.html");
            } else {
                ctx.redirect("/cupcake");
            }
        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("index.html");
        }
    }


    //Ny metode der sikre at det kun er admins der kan komme ind på admin siden
    public static void ensureAdmin(Context ctx){
        User currentUser = ctx.sessionAttribute("currentUser");
        // Checker om user er logget ind og om deres rolle er admin
        if (currentUser == null || !"admin".equals(currentUser.getRole())){
            //Hvis de ikke er Admin, bliver de sendt til cupcake.html
            ctx.redirect("/cupcake.html");
        }
    }
}
