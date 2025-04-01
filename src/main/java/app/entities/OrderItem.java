package app.entities;

public class OrderItem {
    private int order_id;
    private int toppingId;
    private int bottomId;
    private String toppingName;  // ✅ Added
    private String bottomName;   // ✅ Added
    private double totalPrice;
    private double toppingPrice;
    private double bottomPrice;
    private int quantity;

    public OrderItem(int order_id, String toppingName, String bottomName, int quantity, double totalPrice) {

        this.order_id = order_id;
        this.toppingName = toppingName;
        this.bottomName = bottomName;
        this.totalPrice = totalPrice;
        this.toppingPrice = toppingPrice;
        this.bottomPrice = bottomPrice;
        this.quantity = quantity; // ✅ Now correctly assigned
    }


    public int getOrder_id() {
        return order_id;
    }
    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }
    public String getToppingName() {  // ✅ Getter for name
        return toppingName;
    }

    public String getBottomName() {   // ✅ Getter for name
        return bottomName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public double getToppingPrice() {
        return toppingPrice;
    }

    public double getBottomPrice() {
        return bottomPrice;
    }

    public int getQuantity() { return quantity; }

    @Override
    public String toString() {
        return "Topping: " + toppingName + " ($" + toppingPrice + "), " +
                "Bottom: " + bottomName + " ($" + bottomPrice + "), " +
                "Total Price: $" + totalPrice;
    }
}