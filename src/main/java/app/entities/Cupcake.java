package app.entities;

public class Cupcake {

    private String top;
    private String bottom;
    private String price;


    public Cupcake(String top, String bottom, String price) {
        this.top = top;
        this.bottom = bottom;
        this.price = price;
    }

    public String getTop() {
        return top;
    }

    public String getBottom() {
        return bottom;
    }

    public String getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Cupcake{" +
                "top='" + top + '\'' +
                ", bottom='" + bottom + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
