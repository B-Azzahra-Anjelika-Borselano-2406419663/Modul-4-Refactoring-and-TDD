package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    private final Map<String, Order> paymentOrderMap = new HashMap<>();

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                method,
                PaymentStatus.REJECTED.getValue(),
                paymentData
        );
        paymentRepository.save(payment);
        paymentOrderMap.put(payment.getId(), order);
        return payment;
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        payment.setStatus(status);
        Order order = paymentOrderMap.get(payment.getId());

        if (order != null) {
            if (PaymentStatus.SUCCESS.getValue().equals(status)) {
                order.setStatus(OrderStatus.SUCCESS.getValue());
            }
            if (PaymentStatus.REJECTED.getValue().equals(status)) {
                order.setStatus(OrderStatus.FAILED.getValue());
            }
        }
        paymentRepository.save(payment);
        return payment;
    }

    @Override
    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public Iterable<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}