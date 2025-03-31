package app.persistence;

import java.sql.*;

public class CupcakeMapper {


    public static void addToBasket(ConnectionPool connectionPool, int userId, String toppingName, String bottomName, double totalPrice) {
        String sql = "INSERT INTO basket (user_id, topping_name, bottom_name, total_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, toppingName);  // Use correct column name
            stmt.setString(3, bottomName);   // Use correct column name
            stmt.setDouble(4, totalPrice);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeFromBasket(ConnectionPool connectionPool, int userId, String toppingName, String bottomName) {
        String sql = "DELETE FROM basket WHERE user_id = ? AND topping_name = ? AND bottom_name = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, toppingName);
            stmt.setString(3, bottomName);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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


}