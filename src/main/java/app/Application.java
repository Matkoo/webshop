package main.java.app;

/**
 * @author Matkovics Gergely<br>
 * E-mail: <a href=
 * "mailto:gergelymatkovics82@gmail.com">gergelymatkovics82@gmail.com</a>
 */


import main.java.app.data.Customer;
import main.java.app.data.CustomerKey;
import main.java.app.data.Payment;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Application {
    private static final String CUSTOMER_FILE = "customer.csv";
    private static final String PAYMENT_FILE = "payments.csv";
    private static final String LOG_FILE = "application.log";
    private static final String REPORT_01_FILE = "report01.csv";
    private static final String REPORT_02_FILE = "report02.csv";
    private static final String TOP_CUSTOMERS_FILE = "top.csv";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

    private List<Customer> customers;
    private List<Payment> payments;
    private Map<String, Integer> shopCardSales;
    private Map<String, Integer> shopTransferSales;

    public static void main(String[] args) {
        Application application = new Application();
        application.run();
    }

    private void run() {
        customers = new ArrayList<>();
        payments = new ArrayList<>();
        shopCardSales = new HashMap<>();
        shopTransferSales = new HashMap<>();

        readCustomersFromFile();
        readPaymentsFromFile();

        generateReport01();
        generateReport02();
        generateTopCustomersReport();
    }

    private void readCustomersFromFile() {
        Set<String> customerKeys = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMER_FILE))) {
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
    }

    private void readPaymentsFromFile() {
        Set<CustomerKey> customerKeys = new HashSet<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        try (BufferedReader reader = new BufferedReader(new FileReader(PAYMENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length == 7) {
                    try {
                        String webshopId = data[0];
                        String customerId = data[1];
                        String paymentMethod = data[2];
                        int amount = Integer.parseInt(data[3]);
                        String bankAccount = data[4];
                        String cardNumber = data[5];
                        Date paymentDate = dateFormat.parse(data[6]);

                        CustomerKey customerKey = new CustomerKey(webshopId, customerId);
                        if (!customerKeys.contains(customerKey)) {
                            if (customerExists(webshopId, customerId)) {
                                Payment payment = new Payment(webshopId, customerId, paymentMethod, amount,
                                        bankAccount, cardNumber, paymentDate);
                                payments.add(payment);
                                customerKeys.add(customerKey);

                                updateShopSales(paymentMethod, webshopId, amount);
                            } else {
                                logError("Customer does not exist for webshop: " + webshopId + ", customer ID: " + customerId);
                            }
                        } else {
                            logError("Duplicate customer ID in payments for webshop: " + webshopId + ", customer ID: " + customerId);
                        }
                    } catch (NumberFormatException | ParseException e) {
                        logError("Invalid payment data: " + line);
                    }
                } else {
                    logError("Invalid payment data: " + line);
                }
            }
        } catch (IOException e) {
            logError("Error reading payment file: " + e.getMessage());
        }
    }

    private boolean customerExists(String webshopId, String customerId) {
        for (Customer customer : customers) {
            if (customer.getWebshopId().equals(webshopId) && customer.getCustomerId().equals(customerId)) {
                return true;
            }
        }
        return false;
    }

    private void updateShopSales(String paymentMethod, String webshopId, int amount) {
        if (paymentMethod.equals("card")) {
            shopCardSales.put(webshopId, shopCardSales.getOrDefault(webshopId, 0) + amount);
        } else if (paymentMethod.equals("transfer")) {
            shopTransferSales.put(webshopId, shopTransferSales.getOrDefault(webshopId, 0) + amount);
        }
    }

    private void generateReport01() {
        Map<String, Integer> customerTotalSpending = new HashMap<>();
        Map<String, Customer> customerMap = new HashMap<>();

        for (Payment payment : payments) {
            String customerId = payment.getCustomerId();
            int amount = payment.getAmount();

            customerTotalSpending.put(customerId, customerTotalSpending.getOrDefault(customerId, 0) + amount);

            if (!customerMap.containsKey(customerId)) {
                Customer customer = getCustomerById(customerId);
                if (customer != null) {
                    customerMap.put(customerId, customer);
                }
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(REPORT_01_FILE))) {

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

    private Customer getCustomerById(String customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }
        return null;
    }

    private int getTotalAmountForCustomer(String customerId) {
        int totalAmount = 0;
        for (Payment payment : payments) {
            if (payment.getCustomerId().equals(customerId)) {
                totalAmount += payment.getAmount();
            }
        }
        return totalAmount;
    }

    private void generateReport02() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(REPORT_02_FILE))) {
            for (String webshopId : shopCardSales.keySet()) {
                int cardSales = shopCardSales.getOrDefault(webshopId, 0);
                int transferSales = shopTransferSales.getOrDefault(webshopId, 0);
                writer.println(webshopId + ";" + cardSales + ";" + transferSales);
            }
        } catch (IOException e) {
            logError("Error generating report 02: " + e.getMessage());
        }
    }

    private void generateTopCustomersReport() {
        List<Customer> topCustomers = getTopCustomers();
        try (PrintWriter writer = new PrintWriter(new FileWriter(TOP_CUSTOMERS_FILE))) {
            for (Customer customer : topCustomers) {
                int totalAmount = getTotalAmountForCustomer(customer.getCustomerId());
                writer.println(customer.getName() + ";" + customer.getAddress() + ";" + totalAmount);
            }
        } catch (IOException e) {
            logError("Error generating top customers report: " + e.getMessage());
        }
    }

    private List<Customer> getTopCustomers() {
        List<Customer> topCustomers = new ArrayList<>(customers);
        topCustomers.sort(Comparator.comparingInt(c -> -getTotalAmountForCustomer(c.getCustomerId())));
        if (topCustomers.size() > 2) {
            topCustomers = topCustomers.subList(0, 2);
        }
        return topCustomers;
    }

    private void logError(String errorMessage) {
        LocalDateTime timestamp = LocalDateTime.now();
        String formattedTimestamp = timestamp.format(TIMESTAMP_FORMATTER);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(formattedTimestamp + " - " + errorMessage);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
}