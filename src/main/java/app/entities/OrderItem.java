package app.entities;

public class OrderItem {
    private int toppingId;
    private int bottomId;
    private String toppingName;  // ✅ Added
    private String bottomName;   // ✅ Added
    private double totalPrice;
    private double toppingPrice;
    private double bottomPrice;

    public OrderItem(int toppingId, int bottomId, String toppingName, String bottomName, double totalPrice, double toppingPrice, double bottomPrice) {
        this.toppingId = toppingId;
        this.bottomId = bottomId;
        this.toppingName = toppingName;  // ✅ Assign name
        this.bottomName = bottomName;    // ✅ Assign name
        this.totalPrice = totalPrice;
        this.toppingPrice = toppingPrice;
        this.bottomPrice = bottomPrice;
    }

    public int getToppingId() {
        return toppingId;
    }

    public int getBottomId() {
        return bottomId;
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

    @Override
    public String toString() {
        return "Topping: " + toppingName + " ($" + toppingPrice + "), " +
                "Bottom: " + bottomName + " ($" + bottomPrice + "), " +
                "Total Price: $" + totalPrice;
    }
}
