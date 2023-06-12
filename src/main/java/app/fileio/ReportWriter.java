package main.java.app.fileio;

import main.java.app.data.Customer;
import main.java.app.data.Payment;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Matkovics Gergely<br>
 * E-mail: <a href=
 * "mailto:gergelymatkovics82@gmail.com">gergelymatkovics82@gmail.com</a>
 */


public class ReportWriter {

    public void writeReport01(String filePath, List<Customer> customers) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Customer customer : customers) {
                writer.write(customer.getWebshopId() + ";" + customer.getCustomerId() + ";" +
                        customer.getName() + ";" + customer.getAddress() + "\n");
            }
        } catch (IOException e) {
            logError("Error writing report 01: " + e.getMessage());
        }
    }

    public void writeReport02(String filePath, List<Payment> payments) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Payment payment : payments) {
                writer.write(payment.getWebshopId() + ";" + payment.getCustomerId() + ";" +
                        payment.getPaymentMethod() + ";" + payment.getAmount() + ";" +
                        payment.getBankAccount() + ";" + payment.getCardNumber() + ";" +
                        payment.getPaymentDate() + "\n");
            }
        } catch (IOException e) {
            logError("Error writing report 02: " + e.getMessage());
        }
    }

    public void writeTopCustomersReport(String filePath, List<Customer> customers,List<Payment> payments) {
        List<Customer> topCustomers = getTopCustomers(customers,payments);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Customer customer : topCustomers) {
                int totalAmount = getTotalAmountForCustomer(customer.getCustomerId(),payments);
                writer.println(customer.getName() + ";" + customer.getAddress() + ";" + totalAmount);
            }
        } catch (IOException e) {
            logError("Error generating top customers report: " + e.getMessage());
        }
    }

    private List<Customer> getTopCustomers( List<Customer> customers,List<Payment> payments) {
        List<Customer> topCustomers = new ArrayList<>(customers);
        topCustomers.sort(Comparator.comparingInt(c -> -getTotalAmountForCustomer(c.getCustomerId(),payments)));
        if (topCustomers.size() > 2) {
            topCustomers = topCustomers.subList(0, 2);
        }
        return topCustomers;
    }

    private int getTotalAmountForCustomer(String customerId,List<Payment> payments) {
        int totalAmount = 0;
        for (Payment payment : payments) {
            if (payment.getCustomerId().equals(customerId)) {
                totalAmount += payment.getAmount();
            }
        }
        return totalAmount;
    }

    public static void logError(String errorMessage) {
        Logger.logError(errorMessage);
    }
}
