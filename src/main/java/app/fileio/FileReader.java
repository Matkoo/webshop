package main.java.app.fileio;

import main.java.app.data.Customer;
import main.java.app.data.CustomerKey;
import main.java.app.data.Payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Matkovics Gergely<br>
 * E-mail: <a href=
 * "mailto:gergelymatkovics82@gmail.com">gergelymatkovics82@gmail.com</a>
 */


public class FileReader {

    private Map<String, Integer> shopCardSales;
    private Map<String, Integer> shopTransferSales;

    public List<Customer> readCustomersFromFile(String filePath) {
        List<Customer> customers = new ArrayList<>();
        Set<String> customerKeys = new HashSet();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length == 4) {
                    String webshopId = data[0];
                    String customerId = data[1];
                    String name = data[2];
                    String address = data[3];

                    String customerKey = webshopId + "-" + customerId;
                    if (!customerKeys.contains(customerKey)) {
                        Customer customer = new Customer(webshopId, customerId, name, address);
                        customers.add(customer);
                        customerKeys.add(customerKey);
                    } else {
                        logError("Duplicate customer ID for webshop: " + webshopId + ", customer ID: " + customerId);
                    }
                } else {
                    logError("Invalid customer data: " + line);
                }
            }
        } catch (IOException e) {
            logError("Error reading customer file: " + e.getMessage());
        }

        return customers;
    }

    public List<Payment> readPaymentsFromFile(String filePath, List<Customer> customers) {
        List<Payment> payments = new ArrayList<>();
        Set<CustomerKey> customerKeys = new HashSet<>();
        shopCardSales = new HashMap<>();
        shopTransferSales = new HashMap<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length == 7) {
                    String webshopId = data[0];
                    String customerId = data[1];
                    String paymentMethod = data[2];
                    int amount = Integer.parseInt(data[3]);
                    String bankAccount = data[4];
                    String cardNumber = data[5];
                    Date paymentDate = dateFormat.parse(data[6]);

                    CustomerKey customerKey = new CustomerKey(webshopId, customerId);
                    if (!customerKeys.contains(customerKey)) {
                        if (customerExists(customers, webshopId, customerId)) {
                            Payment payment = new Payment(webshopId, customerId, paymentMethod, amount,
                                    bankAccount, cardNumber, paymentDate);
                            payments.add(payment);
                            customerKeys.add(customerKey);

                            updateShopSales(paymentMethod, webshopId, amount);
                        } else {
                            logError("Customer not found for payment: " + webshopId + "-" + customerId);
                        }
                    } else {
                        logError("Duplicate payment for customer: " + webshopId + "-" + customerId);
                    }
                } else {
                    logError("Invalid payment data: " + line);
                }
            }
        } catch (IOException e) {
            logError("Error reading payment file: " + e.getMessage());
        } catch (ParseException e) {
            logError("Invalid payment data: " + e.getMessage());
        }

        return payments;
    }

    private boolean customerExists(List<Customer> customers, String webshopId, String customerId) {
        return customers.stream()
                .anyMatch(c -> c.getWebshopId().equals(webshopId) && c.getCustomerId().equals(customerId));
    }

    private void updateShopSales(String paymentMethod, String webshopId, int amount) {
        if (paymentMethod.equals("card")) {
            shopCardSales.merge(webshopId, amount, Integer::sum);
        } else if (paymentMethod.equals("transfer")) {
            shopTransferSales.merge(webshopId, amount, Integer::sum);
        }
    }

    private void logError(String errorMessage) {
        Logger.logError(errorMessage);
    }
}
