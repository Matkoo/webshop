package main.java.app.data;

/**
 * @author Matkovics Gergely<br>
 * E-mail: <a href=
 * "mailto:gergelymatkovics82@gmail.com">gergelymatkovics82@gmail.com</a>
 */


public class CustomerKey {
    private String webshopId;
    private String customerId;

    public CustomerKey(String webshopId, String customerId) {
        this.webshopId = webshopId;
        this.customerId = customerId;
    }

    public String getWebshopId() {
        return webshopId;
    }

    public String getCustomerId() {
        return customerId;
    }
}