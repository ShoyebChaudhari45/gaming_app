package com.example.gameapp.models.response;

import java.util.List;

public class WalletStatementResponse {

    private int status_code;
    private String message;
    private List<Data> data;

    public int getStatus_code() {
        return status_code;
    }

    public String getMessage() {
        return message;
    }

    public List<Data> getData() {
        return data;
    }

    public static class Data {
        private String type;
        private String amount;
        private String status;
        private String date;

        public String getType() {
            return type;
        }

        public String getAmount() {
            return amount;
        }

        public String getStatus() {
            return status;
        }

        public String getDate() {
            return date;
        }
    }
}
