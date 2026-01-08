package com.example.gameapp.models.response;

import java.util.List;



import java.util.List;

public class PriceResponse {

    public boolean success;

    // IMPORTANT: data is List<String>, NOT List<Price>
    public List<String> data;
}
