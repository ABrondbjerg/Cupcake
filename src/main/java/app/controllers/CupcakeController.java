package app.controllers;

import app.entities.OrderItem;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class CupcakeController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/cupcake", CupcakeController::showCupcakePage);
        app.post("/add-to-basket", CupcakeController::addToBasket);
        app.post("/remove-from-basket", CupcakeController::removeFromBasket); // New route for deletion
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

    private static void addToBasket(Context ctx) {
        String top = ctx.formParam("top");
        String bottom = ctx.formParam("bottom");
        String quantityStr = ctx.formParam("quantity");

        if (top != null && bottom != null && !top.isEmpty() && !bottom.isEmpty() && quantityStr != null && !quantityStr.isEmpty()) {
            try {
                int topId = Integer.parseInt(top);
                int bottomId = Integer.parseInt(bottom);
                int quantity = Integer.parseInt(quantityStr);

                // 🔹 Fetch names from IDs
                String topName = getToppingNameById(topId);
                String bottomName = getBottomNameById(bottomId);
                double topPrice = getToppingPriceById(topId);
                double bottomPrice = getBottomPriceById(bottomId);
                double totalPrice = (topPrice + bottomPrice) * quantity;

                System.out.println("Selected: " + topName + " and " + bottomName + "Quantity: " + quantity + " - Total: $" + totalPrice);

                // ✅ Pass names to OrderItem
                OrderItem orderItem = new OrderItem(topId, bottomId, topName, bottomName, totalPrice, topPrice, bottomPrice, quantity);

                List<OrderItem> basket = ctx.sessionAttribute("basket");
                if (basket == null) {
                    basket = new ArrayList<>();
                }

                basket.add(orderItem);
                ctx.sessionAttribute("basket", basket);

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
                    basket.remove(index); // Remove item at index
                    ctx.sessionAttribute("basket", basket);
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

    // Example of a simple lookup for topping prices
    private static double getToppingPriceById(int toppingId) {
        switch (toppingId) {
            case 1: return 5.00; // Chocolate
            case 2: return 5.00; // Blueberry
            case 3: return 5.00; // Raspberry
            case 4: return 6.00; // Crispy
            case 5: return 6.00; // Strawberry
            case 6: return 7.00; // Rum/Raisin
            case 7: return 8.00; // Orange
            case 8: return 8.00; // Lemon
            case 9: return 9.00; // Blue Cheese
            default: return 0.00; // Default if not found
        }
    }

    // Example of a simple lookup for bottom prices
    private static double getBottomPriceById(int bottomId) {
        switch (bottomId) {
            case 1: return 5.00; // Chocolate
            case 2: return 5.00; // Vanilla
            case 3: return 5.00; // Nutmeg
            case 4: return 6.00; // Pistachio
            case 5: return 7.00; // Almond
            default: return 0.00; // Default if not found
        }
    }



    private static String getToppingNameById(int toppingId) {
        switch (toppingId) {
            case 1: return "Chocolate";
            case 2: return "Blueberry";
            case 3: return "Raspberry";
            case 4: return "Crispy";
            case 5: return "Strawberry";
            case 6: return "Rum/Raisin";
            case 7: return "Orange";
            case 8: return "Lemon";
            case 9: return "Blue Cheese";
            default: return "Unknown";
        }
    }

    private static String getBottomNameById(int bottomId) {
        switch (bottomId) {
            case 1: return "Chocolate";
            case 2: return "Vanilla";
            case 3: return "Nutmeg";
            case 4: return "Pistachio";
            case 5: return "Almond";
            default: return "Unknown";
        }
    }


}
