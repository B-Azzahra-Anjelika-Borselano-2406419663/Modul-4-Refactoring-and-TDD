package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {

    private final List<Payment> paymentData = new ArrayList<>();

    public Payment save(Payment payment) {
        int i = 0;
        for (Payment savedPayment : paymentData) {
            if (savedPayment.getId().equals(payment.getId())) {
                paymentData.set(i, payment);
                return payment;
            }
            i += 1;
        }
        paymentData.add(payment);
        return payment;
    }

    public Payment findById(String paymentId) {
        for (Payment payment : paymentData) {
            if (payment.getId().equals(paymentId)) {
                return payment;
            }
        }
        return null;
    }
}