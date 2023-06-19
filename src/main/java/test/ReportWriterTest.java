package main.java.test;

import main.java.app.data.Customer;
import main.java.app.data.Payment;
import main.java.app.fileio.ReportWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


class ReportWriterTest {

    private static final String REPORT01_FILE = "src/main/java/test/resources/report01.csv";
    private static final String REPORT02_FILE = "src/main/java/test/resources/report02.csv";
    private static final String TOP_CUSTOMERS_FILE = "src/main/java/test/resources/top.csv";

    private ReportWriter reportWriter;

    @BeforeEach
    public void setUp() {
        reportWriter = new ReportWriter();
    }

    @Test
    public void testWriteReport01() throws IOException {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("webshop1", "customer1", "John Doe", "123 Main St"));
        customers.add(new Customer("webshop1", "customer2", "Jane Smith", "456 Elm St"));

        reportWriter.writeReport01(REPORT01_FILE, customers);

        List<String> expectedLines = new ArrayList<>();
        expectedLines.add("webshop1;customer1;John Doe;123 Main St");
        expectedLines.add("webshop1;customer2;Jane Smith;456 Elm St");

        assertFileContentEquals(REPORT01_FILE, expectedLines);
    }

    @Test
    public void testWriteReport02() throws IOException {
        List<Payment> payments = new ArrayList<>();
        payments.add(new Payment("webshop1", "customer1", "card", 100, "123456", "111111", parseDate("2023-01-01")));
        payments.add(new Payment("webshop1", "customer2", "card", 200, "654321", "222222", parseDate("2023-02-02")));

        reportWriter.writeReport02(REPORT02_FILE, payments);

        List<String> expectedLines = new ArrayList<>();
        expectedLines.add("webshop1;customer1;card;100;123456;111111;2023-01-01");
        expectedLines.add("webshop1;customer2;card;200;654321;222222;2023-02-02");

        assertFileContentEquals(REPORT02_FILE, expectedLines);
    }

    @Test
    public void testWriteTopCustomersReport() throws IOException {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("webshop1", "customer1", "John Doe", "123 Main St"));
        customers.add(new Customer("webshop1", "customer2", "Jane Smith", "456 Elm St"));
        customers.add(new Customer("webshop2", "customer3", "Alice Johnson", "789 Oak St"));

        List<Payment> payments = new ArrayList<>();
        payments.add(new Payment("webshop1", "customer1", "card", 100, "123456", "111111", parseDate("2023-01-01")));
        payments.add(new Payment("webshop1", "customer2", "card", 200, "654321", "222222", parseDate("2023-02-02")));
        payments.add(new Payment("webshop2", "customer3", "card", 300, "789456", "333333", parseDate("2023-03-03")));

        reportWriter.writeTopCustomersReport(TOP_CUSTOMERS_FILE, customers, payments);

        List<String> expectedLines = new ArrayList<>();
        expectedLines.add("Alice Johnson;789 Oak St;300");
        expectedLines.add("Jane Smith;456 Elm St;200");

        assertFileContentEquals(TOP_CUSTOMERS_FILE, expectedLines);
    }

    private void assertFileContentEquals(String filePath, List<String> expectedLines) throws IOException {
        List<String> actualLines = Files.readAllLines(Path.of(filePath));
        Assertions.assertEquals(expectedLines.size(), actualLines.size(), "Incorrect number of lines in file: " + filePath);
        for (int i = 0; i < expectedLines.size(); i++) {
            Assertions.assertEquals(expectedLines.get(i), actualLines.get(i), "Incorrect line content in file: " + filePath);
        }
    }

    private Date parseDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing date: " + dateString, e);
        }
    }
}

