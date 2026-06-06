import com.fangxiong.utils.json.JsonUtils;
import com.fangxiong.jsonUtilsCore.enums.SafetyCheckLevel;

public class MultiClassColdStart {
    public static class User {
        private String name;
        private int age;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }
    public static class Product {
        private String productName;
        private double price;
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }
    public static class Order {
        private String orderId;
        private int amount;
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        public int getAmount() { return amount; }
        public void setAmount(int amount) { this.amount = amount; }
    }
    public static class Address {
        private String city;
        private String street;
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
    }
    public static class LogEntry {
        private String level;
        private String message;
        private long timestamp;
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public static void main(String[] args) {
        String[] jsons = {
                "{\"name\":\"Alice\",\"age\":30}",
                "{\"productName\":\"Widget\",\"price\":9.99}",
                "{\"orderId\":\"ORD-001\",\"amount\":5}",
                "{\"city\":\"Beijing\",\"street\":\"Chang'an Ave\"}",
                "{\"level\":\"INFO\",\"message\":\"started\",\"timestamp\":1717000000000}"
        };
        Class<?>[] classes = {User.class, Product.class, Order.class, Address.class, LogEntry.class};

        for (int i = 0; i < jsons.length; i++) {
            long start = System.nanoTime();
            Object obj = JsonUtils.jsonToBean(jsons[i], classes[i], SafetyCheckLevel.SKIP);
            String back = JsonUtils.beanToJson(obj);
            long end = System.nanoTime();
            double ms = (end - start) / 1_000_000.0;
            System.out.println("Class " + classes[i].getSimpleName() + ": " + ms + " ms");
        }
    }
}