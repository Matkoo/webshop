package main.java.app.data;

/**
 * @author Matkovics Gergely<br>
 * E-mail: <a href=
 * "mailto:gergelymatkovics82@gmail.com">gergelymatkovics82@gmail.com</a>
 */


public abstract class Identifiers {
    protected String webshopId;
    protected String customerId;

    public Identifiers(String webShopID, String clientID) {
        this.webshopId = webShopID;
        this.customerId = clientID;
    }

    public String getWebshopId() {
        return webshopId;
    }

    public String getCustomerId() {
        return customerId;
    }
}