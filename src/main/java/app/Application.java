package main.java.app;

/**
 * @author Matkovics Gergely<br>
 * E-mail: <a href=
 * "mailto:gergelymatkovics82@gmail.com">gergelymatkovics82@gmail.com</a>
 */


import main.java.app.data.Customer;
import main.java.app.data.Payment;
import main.java.app.fileio.FileReader;
import main.java.app.fileio.ReportWriter;
import main.java.app.report.ReportGenerator;

import java.util.*;

public class Application {
    private static final String CUSTOMER_FILE = "src/main/java/resources/customer.csv";
    private static final String PAYMENT_FILE = "src/main/java/resources/payments.csv";
    private static final String REPORT_01_FILE = "src/main/reports/report01.csv";
    private static final String REPORT_02_FILE = "src/main/reports/report02.csv";
    private static final String TOP_CUSTOMERS_FILE = "src/main/reports/top.csv";

    private List<Customer> customers;
    private List<Payment> payments;

    public static void main(String[] args) {
        Application application = new Application();
        application.run();
    }

    private void run() {
        initializeData();
        main.java.app.fileio.FileReader fileReader = new FileReader();
        customers = fileReader.readCustomersFromFile(CUSTOMER_FILE);
        payments = fileReader.readPaymentsFromFile(PAYMENT_FILE, customers);

        ReportGenerator reportGenerator = new ReportGenerator();
        List<Customer> topCustomers = reportGenerator.getTopCustomers(customers, payments);
        ReportWriter reportWriter = new ReportWriter();
        reportWriter.writeReport01(REPORT_01_FILE, topCustomers);
        reportWriter.writeReport02(REPORT_02_FILE, payments);
        reportWriter.writeTopCustomersReport(TOP_CUSTOMERS_FILE, topCustomers,payments);
    }

    private void initializeData() {
        customers = new ArrayList<>();
        payments = new ArrayList<>();
    }
}