package app.persistence;

import app.entities.CupcakePart;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CupcakeMapper {

    public static List<CupcakePart> getCupcakeTops(ConnectionPool connectionPool) throws SQLException {
        List<CupcakePart> tops = new ArrayList<>();
        String sql = "SELECT top_id, topping, price FROM cupcake_top";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int top_id = rs.getInt("top_id");
                String topping = rs.getString("topping");
                double price = rs.getDouble("price");
                tops.add(new CupcakePart(top_id, topping, price));
            }
        }
        return tops;
    }

    public static List<CupcakePart> getCupcakeBottoms(ConnectionPool connectionPool) throws SQLException {
        List<CupcakePart> bottoms = new ArrayList<>();
        String sql = "SELECT bot_id, bottom, price FROM cupcake_bot";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("bot_id");  // ✅ Correct column name
                String name = rs.getString("bottom"); // ✅ Correct column name
                double price = rs.getDouble("price");
                bottoms.add(new CupcakePart(id, name, price));
            }
        }
        return bottoms;
    }
}
