package main.java.app.fileio;

import main.java.app.data.Customer;
import main.java.app.data.Payment;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author Matkovics Gergely<br>
 * E-mail: <a href=
 * "mailto:gergelymatkovics82@gmail.com">gergelymatkovics82@gmail.com</a>
 */


public class ReportWriter {

    public void writeReport01(String filePath, List<Customer> customers, List<Payment> payments) {
        Map<String, Integer> customerTotalSpending = new HashMap<>();
        Map<String, Customer> customerMap = new HashMap<>();

        for (Payment payment : payments) {
            String customerId = payment.getCustomerId();
            int amount = payment.getAmount();

            customerTotalSpending.put(customerId, customerTotalSpending.getOrDefault(customerId, 0) + amount);

            if (!customerMap.containsKey(customerId)) {
                Customer customer = getCustomerById(customerId,customers);
                if (customer != null) {
                    customerMap.put(customerId, customer);
                }
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {

            for (Customer customer : customerMap.values()) {
                String customerId = customer.getCustomerId();
                Integer totalSpending = customerTotalSpending.get(customerId);

                if (totalSpending != null) {
                    writer.println(customer.getName() + ";" + customer.getAddress() + ";" + totalSpending);
                }
            }
        } catch (IOException e) {
            logError("Error generating report 01: " + e.getMessage());
        }
    }

    private Customer getCustomerById(String customerId, List<Customer> customers) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }
        return null;
    }

    public void writeReport02(String filePath, Map<String, Integer> shopTransferSales, Map<String, Integer> shopCardSales) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (String webshopId : shopCardSales.keySet()) {
                int cardSales = shopCardSales.getOrDefault(webshopId, 0);
                int transferSales = shopTransferSales.getOrDefault(webshopId, 0);
                writer.write(webshopId + ";" + cardSales + ";" + transferSales + "\n");
            }
        } catch (IOException e) {
            logError("Error writing report 02: " + e.getMessage());
        }
    }

    public void writeTopCustomersReport(String filePath, List<Customer> customers, List<Payment> payments) {
        List<Customer> topCustomers = getTopCustomers(customers, payments);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Customer customer : topCustomers) {
                int totalAmount = getTotalAmountForCustomer(customer.getCustomerId(), payments);
                writer.println(customer.getName() + ";" + customer.getAddress() + ";" + totalAmount);
            }
        } catch (IOException e) {
            logError("Error generating top customers report: " + e.getMessage());
        }
    }

    private List<Customer> getTopCustomers(List<Customer> customers, List<Payment> payments) {
        List<Customer> topCustomers = new ArrayList<>(customers);
        topCustomers.sort(Comparator.comparingInt(c -> -getTotalAmountForCustomer(c.getCustomerId(), payments)));
        if (topCustomers.size() > 2) {
            topCustomers = topCustomers.subList(0, 2);
        }
        return topCustomers;
    }

    private int getTotalAmountForCustomer(String customerId, List<Payment> payments) {
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
