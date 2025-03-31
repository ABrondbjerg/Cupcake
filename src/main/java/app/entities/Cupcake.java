package app.entities;




    public class Cupcake {
        private int quantity;
        private String topName;
        private String bottomName;
        private double totalPrice;

        public Cupcake(int quantity, String topName, String bottomName, double totalPrice) {
            this.quantity = quantity;
            this.topName = topName;
            this.bottomName = bottomName;
            this.totalPrice = totalPrice;
        }

        @Override
        public String toString() {
            return "Cupcake{" +
                    "quantity=" + quantity +
                    ", topName='" + topName + '\'' +
                    ", bottomName='" + bottomName + '\'' +
                    ", totalPrice=" + totalPrice +
                    '}';
        }

        public int getQuantity() { return quantity; }
        public String getTopName() { return topName; }
        public String getBottomName() { return bottomName; }
        public double getTotalPrice() { return totalPrice; }
    }





