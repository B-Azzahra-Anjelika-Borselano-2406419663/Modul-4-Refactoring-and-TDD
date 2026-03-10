package id.ac.ui.cs.advprog.eshop.service.processor;

import java.util.Map;

public interface PaymentProcessor {
    boolean supports(String method);
    void validate(Map<String, String> paymentData);
}