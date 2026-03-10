package id.ac.ui.cs.advprog.eshop.service.processor;

import id.ac.ui.cs.advprog.eshop.enums.PaymentMethod;

import java.util.Map;

public class VoucherProcessor implements PaymentProcessor {
    private static final String VOUCHER_PREFIX = "ESHOP";
    private static final int VOUCHER_LENGTH = 16;
    private static final int DIGIT_COUNT = 8;

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
        if (!voucherCode.startsWith(VOUCHER_PREFIX)) {
            throw new IllegalArgumentException("Voucher must start with ESHOP");
        }
        if (voucherCode.length() != VOUCHER_LENGTH) {
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
        if (sum != DIGIT_COUNT) {
            throw new IllegalArgumentException("Voucher must contain 8 numerical characters");
        }
    }
}