package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentRepositoryTest {

    private PaymentRepository paymentRepository;
    private Payment payment;
    private Map<String, String> paymentData;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();

        paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");

        payment = new Payment(
                "payment-1",
                PaymentMethod.VOUCHER.getValue(),
                PaymentStatus.REJECTED.getValue(),
                paymentData
        );
    }

    @Test
    void testSaveCreate() {
        Payment savedPayment = paymentRepository.save(payment);

        assertEquals(payment, savedPayment);
        assertEquals(payment, paymentRepository.findById("payment-1"));
    }

    @Test
    void testSaveUpdate() {
        paymentRepository.save(payment);

        payment.setStatus(PaymentStatus.SUCCESS.getValue());
        paymentRepository.save(payment);

        Payment updatedPayment = paymentRepository.findById("payment-1");

        assertEquals(PaymentStatus.SUCCESS.getValue(), updatedPayment.getStatus());
    }

    @Test
    void testFindByIdIfIdFound() {
        paymentRepository.save(payment);

        Payment findResult = paymentRepository.findById("payment-1");

        assertNotNull(findResult);
        assertEquals("payment-1", findResult.getId());
        assertEquals(PaymentMethod.VOUCHER.getValue(), findResult.getMethod());
    }

    @Test
    void testFindByIdIfIdNotFound() {
        Payment findResult = paymentRepository.findById("unknown-id");

        assertNull(findResult);
    }
}