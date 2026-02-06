package com.example.gameapp.models.request;

public class DepositRequest {

    private int amount;  // ⭐ Backend expects "amount" not "price"

    public DepositRequest(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}