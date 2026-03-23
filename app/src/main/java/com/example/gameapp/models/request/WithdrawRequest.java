package com.example.gameapp.models.request;

public class WithdrawRequest {

    private final int price;
    private final String payment_mode;

    public WithdrawRequest(int price, String payment_mode) {
        this.price = price;
        this.payment_mode = payment_mode;
    }

    public int getPrice() {
        return price;
    }

    public String getPayment_mode() {
        return payment_mode;
    }
}
