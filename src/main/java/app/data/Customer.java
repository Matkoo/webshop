package main.java.app.data;

/**
 * @author Matkovics Gergely<br>
 * E-mail: <a href=
 * "mailto:gergelymatkovics82@gmail.com">gergelymatkovics82@gmail.com</a>
 */


public class Customer extends Identifiers {
    private String name;
    private String address;

    public Customer(String webShopID, String clientID, String clientName, String clientAddress) {
        super(webShopID, clientID);
        this.name = clientName;
        this.address = clientAddress;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
