package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;
import id.ac.ui.cs.advprog.eshop.enums.PaymentStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @InjectMocks
    PaymentServiceImpl paymentService;

    @Mock
    PaymentRepository paymentRepository;

    Order order;
    Map<String, String> paymentData;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setProductId("prod-1");
        product.setProductName("Product Test");
        product.setProductQuantity(1);

        List<Product> products = new ArrayList<>();
        products.add(product);

        order = new Order("order-1", products, 1708560000L, "Zara");
        paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");
    }

    @Test
    void testAddPayment() {
        doAnswer(invocation -> invocation.getArgument(0))
                .when(paymentRepository)
                .save(any(Payment.class));

        Payment result = paymentService.addPayment(
                order,
                PaymentMethod.VOUCHER.getValue(),
                paymentData
        );

        assertEquals(PaymentMethod.VOUCHER.getValue(), result.getMethod());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testSetStatusSuccess() {
        doAnswer(invocation -> invocation.getArgument(0))
                .when(paymentRepository)
                .save(any(Payment.class));

        Payment payment = paymentService.addPayment(
                order,
                PaymentMethod.VOUCHER.getValue(),
                paymentData
        );

        paymentService.setStatus(payment, PaymentStatus.SUCCESS.getValue());
        assertEquals(OrderStatus.SUCCESS.getValue(), order.getStatus());
    }

    @Test
    void testSetStatusRejected() {
        doAnswer(invocation -> invocation.getArgument(0))
                .when(paymentRepository)
                .save(any(Payment.class));

        Payment payment = paymentService.addPayment(
                order,
                PaymentMethod.VOUCHER.getValue(),
                paymentData
        );

        paymentService.setStatus(payment, PaymentStatus.REJECTED.getValue());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }

    @Test
    void testGetPaymentIfFound() {
        Payment payment = new Payment(
                "payment-1",
                PaymentMethod.VOUCHER.getValue(),
                PaymentStatus.REJECTED.getValue(),
                paymentData
        );
        doReturn(payment).when(paymentRepository).findById("payment-1");
        Payment result = paymentService.getPayment("payment-1");
        assertEquals("payment-1", result.getId());
    }

    @Test
    void testGetPaymentIfNotFound() {
        doReturn(null).when(paymentRepository).findById("unknown");
        Payment result = paymentService.getPayment("unknown");
        assertNull(result);
    }

    @Test
    void testGetAllPayments() {
        List<Payment> payments = new ArrayList<>();
        payments.add(new Payment(
                "1",
                PaymentMethod.VOUCHER.getValue(),
                PaymentStatus.REJECTED.getValue(),
                paymentData
        ));
        payments.add(new Payment(
                "2",
                PaymentMethod.VOUCHER.getValue(),
                PaymentStatus.REJECTED.getValue(),
                paymentData
        ));
        doReturn(payments).when(paymentRepository).findAll();
        Iterable<Payment> results = paymentService.getAllPayments();

        int count = 0;
        for (Payment p : results) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    void testValidVoucherPayment() {
        Map<String,String> data = new HashMap<>();
        data.put("voucherCode","ESHOP123456zar78");

        doAnswer(invocation -> invocation.getArgument(0))
                .when(paymentRepository)
                .save(any(Payment.class));

        Payment payment = paymentService.addPayment(
                order,
                PaymentMethod.VOUCHER.getValue(),
                data
        );

        assertEquals(PaymentMethod.VOUCHER.getValue(), payment.getMethod());
        assertEquals(OrderStatus.SUCCESS.getValue(), order.getStatus());
    }

    @Test
    void testVoucherInvalidPrefix() {
        Map<String,String> data = new HashMap<>();
        data.put("voucherCode","INVALID123456789");

        Payment payment = paymentService.addPayment(
                order,
                PaymentMethod.VOUCHER.getValue(),
                data
        );

        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }

    @Test
    void testVoucherInvalidLength() {
        Map<String,String> data = new HashMap<>();
        data.put("voucherCode","ESHOP123");

        Payment payment = paymentService.addPayment(
                order,
                PaymentMethod.VOUCHER.getValue(),
                data
        );

        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }

    @Test
    void testValidCODPayment() {
        Map<String,String> data = new HashMap<>();
        data.put("address","Depok");
        data.put("deliveryFee","10000");

        doAnswer(invocation -> invocation.getArgument(0))
                .when(paymentRepository)
                .save(any(Payment.class));

        paymentService.addPayment(
                order,
                PaymentMethod.COD.getValue(),
                data
        );

        assertEquals(OrderStatus.SUCCESS.getValue(), order.getStatus());
    }

    @Test
    void testCODMissingAddressShouldRejectPayment() {
        Map<String,String> data = new HashMap<>();
        data.put("deliveryFee","10000");

        doAnswer(invocation -> invocation.getArgument(0))
                .when(paymentRepository)
                .save(any(Payment.class));

        Payment payment = paymentService.addPayment(
                order,
                PaymentMethod.COD.getValue(),
                data
        );

        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }

    @Test
    void testCODMissingDeliveryFeeShouldRejectPayment() {
        Map<String,String> data = new HashMap<>();
        data.put("address","Depok");

        doAnswer(invocation -> invocation.getArgument(0))
                .when(paymentRepository)
                .save(any(Payment.class));

        Payment payment = paymentService.addPayment(
                order,
                PaymentMethod.COD.getValue(),
                data
        );

        assertEquals(PaymentStatus.REJECTED.getValue(), payment.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }
}