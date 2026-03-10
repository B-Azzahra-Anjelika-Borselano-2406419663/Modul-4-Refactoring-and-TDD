package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import id.ac.ui.cs.advprog.eshop.service.processor.CODProcessor;
import id.ac.ui.cs.advprog.eshop.service.processor.PaymentProcessor;
import id.ac.ui.cs.advprog.eshop.service.processor.VoucherProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    private final Map<String, Order> paymentOrderMap = new HashMap<>();
    private final List<PaymentProcessor> processors = List.of(
            new VoucherProcessor(),
            new CODProcessor()
    );

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        boolean valid = isPaymentValid(method, paymentData);
        Payment payment = createPayment(method, paymentData, valid);
        updateOrderStatus(order, valid);
        savePayment(payment, order);
        return payment;
    }

    private boolean isPaymentValid(String method, Map<String, String> paymentData) {
        for (PaymentProcessor processor : processors) {
            if (processor.supports(method)) {
                return processor.validate(paymentData);
            }
        }
        return false;
    }

    private Payment createPayment(String method, Map<String, String> paymentData, boolean valid) {
        String status = valid
                ? PaymentStatus.SUCCESS.getValue()
                : PaymentStatus.REJECTED.getValue();
        return new Payment(
                UUID.randomUUID().toString(),
                method,
                status,
                paymentData
        );
    }

    private void updateOrderStatus(Order order, boolean valid) {
        if (valid) {
            order.setStatus(OrderStatus.SUCCESS.getValue());
        } else {
            order.setStatus(OrderStatus.FAILED.getValue());
        }
    }

    private void savePayment(Payment payment, Order order) {
        paymentRepository.save(payment);
        paymentOrderMap.put(payment.getId(), order);
    }

    @Override
    public void setStatus(Payment payment, String status) {
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