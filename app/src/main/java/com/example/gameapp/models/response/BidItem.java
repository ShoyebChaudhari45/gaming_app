package com.example.gameapp.models.response;

import com.google.gson.annotations.SerializedName;

public class BidItem {

    @SerializedName("type")
    private String type;

    @SerializedName("input_value")
    private String inputValue;

    @SerializedName("price")
    private int price;

    @SerializedName("total")
    private int total;

    @SerializedName("created_on")
    private String createdOn;

    @SerializedName("tap_name")
    private String tapName;

    public BidItem() {}

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getInputValue() { return inputValue; }
    public void setInputValue(String inputValue) { this.inputValue = inputValue; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public String getCreatedOn() { return createdOn; }
    public void setCreatedOn(String createdOn) { this.createdOn = createdOn; }

    public String getTapName() { return tapName; }
    public void setTapName(String tapName) { this.tapName = tapName; }
}