package id.ac.ui.cs.advprog.eshop.service.processor;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;

import java.util.Map;

public class CODProcessor implements PaymentProcessor {
    private static final String address = "address";
    private static final String deliveryFee = "deliveryFee";

    @Override
    public boolean supports(String method) {
        return PaymentMethod.COD.getValue().equals(method);
    }

    @Override
    public boolean validate(Map<String, String> paymentData) {
        return paymentData.containsKey(address) && paymentData.containsKey(deliveryFee);
    }
}
