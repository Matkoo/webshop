package main.java.app.data;

import java.util.Date;

/**
 * @author Matkovics Gergely<br>
 * E-mail: <a href=
 * "mailto:gergelymatkovics82@gmail.com">gergelymatkovics82@gmail.com</a>
 */


public class Payment extends Identifiers {

    private String paymentMethod;
    private int amount;
    private String bankAccount;
    private String cardNumber;
    private Date paymentDate;

    public Payment(String webShopID, String clientID, String paymentMethod, int amount, String bankAccountNumber, String cardNumber, Date paymentDate) {
        super(webShopID, clientID);
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.bankAccount = bankAccountNumber;
        this.cardNumber = cardNumber;
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public int getAmount() {
        return amount;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

}