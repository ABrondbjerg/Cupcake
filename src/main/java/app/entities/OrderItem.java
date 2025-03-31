package app.entities;

public class OrderItem {
    private int topId;
    private int bottomId;
    private String topName;
    private String bottomName;
    private double totalPrice;
    private double topPrice;
    private double bottomPrice;

    public OrderItem(int topId, int bottomId, String topName, String bottomName, double totalPrice, double topPrice, double bottomPrice) {
        this.topId = topId;
        this.bottomId = bottomId;
        this.topName = topName;
        this.bottomName = bottomName;
        this.totalPrice = totalPrice;
        this.topPrice = topPrice;
        this.bottomPrice = bottomPrice;
    }

    public int getTopId() {
        return topId;
    }

    public void setTopId(int topId) {
        this.topId = topId;
    }

    public int getBottomId() {
        return bottomId;
    }

    public void setBottomId(int bottomId) {
        this.bottomId = bottomId;
    }

    public String getTopName() {
        return topName;
    }

    public void setTopName(String topName) {
        this.topName = topName;
    }

    public String getBottomName() {
        return bottomName;
    }

    public void setBottomName(String bottomName) {
        this.bottomName = bottomName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTopPrice() {
        return topPrice;
    }

    public void setTopPrice(double topPrice) {
        this.topPrice = topPrice;
    }

    public double getBottomPrice() {
        return bottomPrice;
    }

    public void setBottomPrice(double bottomPrice) {
        this.bottomPrice = bottomPrice;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "topId=" + topId +
                ", bottomId=" + bottomId +
                ", topName='" + topName + '\'' +
                ", bottomName='" + bottomName + '\'' +
                ", totalPrice=" + totalPrice +
                ", topPrice=" + topPrice +
                ", bottomPrice=" + bottomPrice +
                '}';
    }
}

