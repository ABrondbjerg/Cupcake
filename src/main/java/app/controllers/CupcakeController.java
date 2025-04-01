package app.controllers;

import app.entities.OrderItem;
import app.entities.User;
import app.persistence.ConnectionPool;
import app.persistence.CupcakeMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CupcakeController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/cupcake", CupcakeController::showCupcakePage);
        app.post("/add-to-basket", ctx -> CupcakeController.addToBasket(ctx, connectionPool));
        app.post("/remove-from-basket", CupcakeController::removeFromBasket); // No need to pass connectionPool here
        app.post("/checkout", ctx -> CupcakeController.confirmCheckout(ctx, connectionPool));
        app.get("/checkout", CupcakeController::showCheckoutPage);
        app.get("/login", CupcakeController::showLogin);

    }

    private static void showCupcakePage(Context ctx) {
        // Retrieve logged-in user
        User user = ctx.sessionAttribute("currentUser");

        if (user == null) {
            ctx.redirect("/login"); // Redirect to login if not authenticated
            return;
        }

        // Retrieve basket from session for this user
        String sessionKey = "basket_" + user.getUserId(); // Unique key per user
        List<OrderItem> basket = ctx.sessionAttribute(sessionKey);

        if (basket == null) {
            basket = new ArrayList<>();
            ctx.sessionAttribute(sessionKey, basket);
        }

        // Pass user-specific basket to Thymeleaf
        ctx.attribute("basket", basket);
        ctx.attribute("totalPrice", calculateTotalPrice(basket));
        ctx.render("cupcake.html");
    }

    private static void addToBasket(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");
        if (user == null) {
            ctx.redirect("/login");
            return;
        }

        String sessionKey = "basket_" + user.getUserId();
        List<OrderItem> basket = ctx.sessionAttribute(sessionKey);
        if (basket == null) {
            basket = new ArrayList<>();
        }

        // ✅ Get cupcake details
        int topId = Integer.parseInt(ctx.formParam("top"));
        int bottomId = Integer.parseInt(ctx.formParam("bottom"));
        int quantity = Integer.parseInt(ctx.formParam("quantity"));

        String topName = CupcakeMapper.getToppingNameById(connectionPool, topId);
        String bottomName = CupcakeMapper.getBottomNameById(connectionPool, bottomId);
        double topPrice = CupcakeMapper.getToppingPriceById(connectionPool, topId);
        double bottomPrice = CupcakeMapper.getBottomPriceById(connectionPool, bottomId);
        double totalPrice = (topPrice + bottomPrice) * quantity;

        // ✅ Add to database and get `order_id`
        int orderId = CupcakeMapper.addToBasket(connectionPool, user.getUserId(), topName, bottomName, totalPrice, quantity);

        // ✅ Create OrderItem with orderId
        OrderItem orderItem = new OrderItem(orderId, topName, bottomName, quantity, totalPrice);
        basket.add(orderItem);
        ctx.sessionAttribute(sessionKey, basket);

        // ✅ Redirect back to cupcake page after adding to basket
        ctx.redirect("/cupcake");
    }



    private static void removeFromBasket(Context ctx) {
        User user = ctx.sessionAttribute("currentUser");
        if (user == null) {
            ctx.redirect("/login");
            return;
        }

        String sessionKey = "basket_" + user.getUserId();
        List<OrderItem> basket = ctx.sessionAttribute(sessionKey);

        String indexStr = ctx.formParam("index");
        if (indexStr != null && basket != null) {
            try {
                int index = Integer.parseInt(indexStr);

                if (index >= 0 && index < basket.size()) {
                    // Get item to remove
                    OrderItem item = basket.get(index);

                    // ✅ Remove from session basket
                    basket.remove(index);
                    ctx.sessionAttribute(sessionKey, basket);

                    // ✅ Remove from database
                    ConnectionPool connectionPool = ctx.attribute("connectionPool");
                    CupcakeMapper.removeFromBasket(connectionPool, user.getUserId(), item.getToppingName(), item.getBottomName());
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid index received: " + indexStr);
            }
        }

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

    public static void showCheckoutPage(Context ctx) {
        User user = ctx.sessionAttribute("currentUser");
        if (user == null) {
            ctx.redirect("/login");  // Ensure the user is logged in
            return;
        }

        String sessionKey = "basket_" + user.getUserId();
        List<OrderItem> basket = ctx.sessionAttribute(sessionKey);

        // Debugging: Check if basket is null or empty
        if (basket == null || basket.isEmpty()) {
            System.out.println("Basket is empty or null, redirecting to cupcake page.");
            ctx.redirect("/cupcake");  // If basket is empty, redirect back to cupcake page
            return;
        }

        // Pass basket and total price to Thymeleaf template
        ctx.attribute("basket", basket);
        ctx.attribute("totalPrice", calculateTotalPrice(basket));

        // Render the checkout page
        try {
            ctx.render("checkout.html");  // Ensure checkout.html exists and is accessible
        } catch (Exception e) {
            System.out.println("Error rendering checkout page: " + e.getMessage());
            ctx.status(500).result("Error rendering checkout page.");
        }
    }



    public static void confirmCheckout(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");
        if (user == null) {
            ctx.redirect("/login");
            return;
        }

        // Use user-specific session key for basket
        String sessionKey = "basket_" + user.getUserId();
        List<OrderItem> basket = ctx.sessionAttribute(sessionKey);
        if (basket == null || basket.isEmpty()) {
            ctx.redirect("/cupcake");
            return;
        }

        // Proceed with the order processing
        try (Connection connection = connectionPool.getConnection()) {
            connection.setAutoCommit(false);  // Start transaction

            String insertOrderSQL = "INSERT INTO orders (user_id, topping_name, bottom_name, quantity, total_price) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psInsert = connection.prepareStatement(insertOrderSQL)) {
                for (OrderItem item : basket) {
                    psInsert.setInt(1, user.getUserId());
                    psInsert.setString(2, item.getToppingName());
                    psInsert.setString(3, item.getBottomName());
                    psInsert.setInt(4, item.getQuantity());
                    psInsert.setDouble(5, item.getTotalPrice());
                    psInsert.addBatch();
                }
                psInsert.executeBatch();
            }

            // Remove from basket after moving to orders
            String deleteSQL = "DELETE FROM basket WHERE user_id = ?";
            try (PreparedStatement psDelete = connection.prepareStatement(deleteSQL)) {
                psDelete.setInt(1, user.getUserId());
                psDelete.executeUpdate();
            }

            connection.commit();  // Commit transaction

            // Clear session basket and set success message
            ctx.sessionAttribute(sessionKey, new ArrayList<>());
            ctx.sessionAttribute("checkoutMessage", "Purchased successfully!");

            // Render checkout page with success message
            ctx.render("checkout.html");  // Ensure checkout.html has a placeholder for success message

        } catch (SQLException e) {
            System.out.println("Checkout error: " + e.getMessage());
        }
    }

    public static void showLogin(Context ctx) {
        ctx.attribute("message", "");
        ctx.render("index.html");
    }
}