package app.controllers;

import app.entities.OrderItem;
import app.entities.User;
import app.persistence.ConnectionPool;
import app.persistence.CupcakeMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class CupcakeController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/cupcake", CupcakeController::showCupcakePage);
        app.post("/add-to-basket", ctx -> CupcakeController.addToBasket(ctx, connectionPool));
        app.post("/remove-from-basket", CupcakeController::removeFromBasket); // No need to pass connectionPool here
    }




    private static void showCupcakePage(Context ctx) {
        // Retrieve basket from session (or create a new one)
        List<OrderItem> basket = ctx.sessionAttribute("basket");
        if (basket == null) {
            basket = new ArrayList<>();
            ctx.sessionAttribute("basket", basket);
        }

        // Pass basket to Thymeleaf
        ctx.attribute("basket", basket);
        ctx.attribute("totalPrice", calculateTotalPrice(basket)); // Pass total price
        ctx.render("cupcake.html");
    }


    private static void addToBasket(Context ctx, ConnectionPool connectionPool) {
        // Retrieve the logged-in user from the session
        User user = ctx.sessionAttribute("currentUser");

        if (user == null) {
            System.out.println("❌ No user logged in.");
            ctx.redirect("/login"); // Redirect to login if no user is found
            return;
        }

        int userId = user.getUserId();  // Extract user_id from the session
        System.out.println("✅ User logged in: " + userId);

        String top = ctx.formParam("top");
        String bottom = ctx.formParam("bottom");

        if (top != null && bottom != null && !top.isEmpty() && !bottom.isEmpty()) {
            try {
                int topId = Integer.parseInt(top);
                int bottomId = Integer.parseInt(bottom);

                // 🔹 Fetch names from IDs
                String topName = CupcakeMapper.getToppingNameById(connectionPool, topId);
                String bottomName = CupcakeMapper.getBottomNameById(connectionPool, bottomId);

                double topPrice = CupcakeMapper.getToppingPriceById(connectionPool, topId);
                double bottomPrice = CupcakeMapper.getBottomPriceById(connectionPool, bottomId);
                double totalPrice = topPrice + bottomPrice;

                System.out.println("Selected: " + topName + " and " + bottomName + " - Total: $" + totalPrice);

                // ✅ Pass names to OrderItem
                OrderItem orderItem = new OrderItem(topId, bottomId, topName, bottomName, totalPrice, topPrice, bottomPrice);

                List<OrderItem> basket = ctx.sessionAttribute("basket");
                if (basket == null) {
                    basket = new ArrayList<>();
                }

                basket.add(orderItem);
                ctx.sessionAttribute("basket", basket);

                // Insert into database using the correct user ID
                CupcakeMapper.addToBasket(connectionPool, userId, topName, bottomName, totalPrice);

            } catch (NumberFormatException e) {
                System.out.println("Error parsing numbers: " + e.getMessage());
            }
        }

        ctx.redirect("/cupcake");
    }





    private static void removeFromBasket(Context ctx) {
        // Get the index from the form
        String indexStr = ctx.formParam("index");

        if (indexStr != null) {
            try {
                int index = Integer.parseInt(indexStr);
                List<OrderItem> basket = ctx.sessionAttribute("basket");

                if (basket != null && index >= 0 && index < basket.size()) {
                    // Get the item to remove from the basket
                    OrderItem item = basket.get(index);

                    // Remove from the basket list in the session
                    basket.remove(index);
                    ctx.sessionAttribute("basket", basket);

                    // Get the connection pool from the context
                    ConnectionPool connectionPool = ctx.attribute("connectionPool");

                    // Remove from the database
                    int userId = 1; // TODO: Replace with actual user ID from session
                    CupcakeMapper.removeFromBasket(connectionPool, userId, item.getTopName(), item.getBottomName());
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid index received: " + indexStr);
            }
        }

        // Redirect back to the cupcake page
        ctx.redirect("/cupcake");
    }



    // Calculate the total price for the entire basket
    private static double calculateTotalPrice(List<OrderItem> basket) {
        double total = 0.0;
        for (OrderItem item : basket) {
            total += item.getTotalPrice();
        }
        return total;
    }

}