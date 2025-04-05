package app.persistence;

import app.entities.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CupcakeMapper {

    public static int addToBasket(ConnectionPool connectionPool, int userId, String topName, String bottomName, double totalPrice, int quantity) {
        String sql = "INSERT INTO basket (user_id, topping_name, bottom_name, quantity, total_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, topName);
            ps.setString(3, bottomName);
            ps.setInt(4, quantity);
            ps.setDouble(5, totalPrice);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Return generated order ID
            }
        } catch (SQLException e) {
            System.out.println("Error adding to basket: " + e.getMessage());
        }
        return -1;
    }





    public static void removeFromBasket(ConnectionPool connectionPool, int userId, String topping, String bottom) {

        String sql = "DELETE FROM basket WHERE ctid IN (" +
                "SELECT ctid FROM basket " +
                "WHERE user_id = ? AND topping_name = ? AND bottom_name = ? " +
                "LIMIT 1" +
                ")";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, topping);
            ps.setString(3, bottom);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Item deleted from DB: " + topping + " " + bottom);
            } else {
                System.out.println("❌ No item found in DB for deletion: " + topping + " " + bottom);
            }

        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }


    public static String getToppingNameById(ConnectionPool connectionPool, int id) {
        String query = "SELECT topping FROM cupcake_top WHERE top_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("topping");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return null if not found
    }

    public static String getBottomNameById(ConnectionPool connectionPool, int id) {
        String query = "SELECT bottom FROM cupcake_bot WHERE bot_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("bottom");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return null if not found
    }

    public static double getToppingPriceById(ConnectionPool connectionPool, int id) {
        String query = "SELECT price FROM cupcake_top WHERE top_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("price");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0; // Return 0 if not found
    }

    public static double getBottomPriceById(ConnectionPool connectionPool, int id) {
        String query = "SELECT price FROM cupcake_bot WHERE bot_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("price");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0; // Return 0 if not found
    }

    public static void checkoutBasket(ConnectionPool connectionPool, int userId) {
        String insertSql = "INSERT INTO orders (user_id, topping_name, bottom_name, total_price, quantity) " +
                "SELECT user_id, topping_name, bottom_name, total_price, quantity FROM basket WHERE user_id = ?";
        String deleteSql = "DELETE FROM basket WHERE user_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement insertStmt = connection.prepareStatement(insertSql);
             PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {

            // Insert items from basket into orders table
            insertStmt.setInt(1, userId);
            insertStmt.executeUpdate();

            // Remove items from the basket after they have been transferred
            deleteStmt.setInt(1, userId);
            deleteStmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("DB error during checkout: " + e.getMessage());
        }
    }
}