package app.controllers;

import app.entities.Task;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.TaskMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static app.controllers.UserController.login;

public class TaskController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("addtask", ctx ->addtask(ctx,connectionPool));
        app.post("deletetask", ctx ->deletetask(ctx,connectionPool));

    }

    private static void deletetask( Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");
        try {
            //todo mangler taskId!
            int taskId = Integer.parseInt(ctx.formParam("taskId"));
            TaskMapper.delete(taskId,connectionPool);
            List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserId(),connectionPool);
            ctx.attribute("taskList", taskList);
            ctx.render ("task.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", "Noget gik galt. Prøv igen");
            ctx.render("task.html");
        }
    }

    private static void addtask(Context ctx, ConnectionPool connectionPool){
        String taskName = ctx.formParam("taskname"); //todo Ændre til 'Order'.
        if (taskName.length()>3){
            User user = ctx.sessionAttribute("currentUser");
            try {
                //todo mangler taskId!
                int taskId = Integer.parseInt(ctx.formParam("taskId"));
                Task newTask = TaskMapper.addTask(user, taskName, connectionPool);
                List<Task> taskList = TaskMapper.getAllTasksPerUser(user.getUserId(),connectionPool);
                ctx.attribute("taskList", taskList);
                ctx.render ("task.html");
            } catch (DatabaseException e) {
                ctx.attribute("message", "Noget gik galt. Prøv igen");
                ctx.render("task.html");
            }

        }

    }
}
