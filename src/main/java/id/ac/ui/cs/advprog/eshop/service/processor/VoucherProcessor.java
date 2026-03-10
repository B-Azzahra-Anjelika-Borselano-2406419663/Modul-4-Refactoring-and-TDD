package id.ac.ui.cs.advprog.eshop.service.processor;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;

import java.util.Map;

public class VoucherProcessor implements PaymentProcessor {

    @Override
    public boolean supports(String method) {
        return PaymentMethod.VOUCHER.getValue().equals(method);
    }

    @Override
    public void validate(Map<String, String> paymentData) {
        String voucherCode = paymentData.get("voucherCode");

        if (voucherCode == null) {
            throw new IllegalArgumentException("Voucher code missing");
        }
        if (!voucherCode.startsWith("ESHOP")) {
            throw new IllegalArgumentException("Voucher must start with ESHOP");
        }
        if (voucherCode.length() != 16) {
            throw new IllegalArgumentException("Voucher must be 16 characters");
        }

        validate8NumericalChar(voucherCode);
    }

    public void validate8NumericalChar(String voucherCode) {
        int sum = 0;
        for (int i = 0; i < voucherCode.length(); i++) {
            char c = voucherCode.charAt(i);
            if (Character.isDigit(c)) {
                sum += 1;
            }
        }
        if (sum != 8) {
            throw new IllegalArgumentException("Voucher must contain 8 numerical characters");
        }
    }
}