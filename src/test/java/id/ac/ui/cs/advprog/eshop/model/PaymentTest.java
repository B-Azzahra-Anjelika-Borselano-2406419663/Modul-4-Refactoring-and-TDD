package id.ac.ui.cs.advprog.eshop.model;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private Payment payment;
    private Map<String, String> paymentData;

    @BeforeEach
    void setUp() {
        paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");
        payment = new Payment(
                "payment-1",
                PaymentMethod.VOUCHER.getValue(),
                PaymentStatus.SUCCESS.getValue(),
                paymentData);
    }

    @Test
    void testCreatePayment() {
        assertEquals("payment-1", payment.getId());
        assertEquals(PaymentMethod.VOUCHER.getValue(), payment.getMethod());
        assertEquals(PaymentStatus.SUCCESS.getValue(), payment.getStatus());
        assertEquals(paymentData, payment.getPaymentData());
    }

    @Test
    void testChangePaymentStatus() {
        payment.setStatus(PaymentStatus.REJECTED.getValue());
        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
    }

    @Test
    void testChangePaymentMethod() {
        payment.setMethod(PaymentMethod.COD.getValue());
        assertEquals(PaymentMethod.COD.getValue(), payment.getMethod());
    }

    @Test
    void testCheckPaymentData() {
        assertTrue(payment.getPaymentData().containsKey("voucherCode"));
        assertEquals("ESHOP1234ABC5678", payment.getPaymentData().get("voucherCode"));
    }
}