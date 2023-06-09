package main.java.app.report;

import main.java.app.data.Customer;
import main.java.app.data.Payment;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Matkovics Gergely<br>
 * E-mail: <a href=
 * "mailto:gergelymatkovics82@gmail.com">gergelymatkovics82@gmail.com</a>
 */


public class ReportGenerator {
    public List<Customer> getTopCustomers(List<Customer> customers, List<Payment> payments) {
        Map<String, Integer> customerPayments = payments.stream()
                .collect(Collectors.groupingBy(Payment::getCustomerId, Collectors.summingInt(Payment::getAmount)));

        return customers.stream()
                .filter(c -> customerPayments.containsKey(c.getCustomerId()))
                .sorted(Comparator.comparingInt(c -> -customerPayments.get(c.getCustomerId())))
                .collect(Collectors.toList());
    }
}
