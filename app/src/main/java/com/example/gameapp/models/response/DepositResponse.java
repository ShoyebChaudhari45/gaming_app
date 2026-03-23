package com.example.gameapp.models.response;

public class DepositResponse {
    private int status_code;
    private String message;
    private Data data;

    public int getStatus_code() {
        return status_code;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        private int employee_id;
        private String type;
        private String amount;  // ⭐ STRING - Backend sends "500" not 500
        private String payment_proof;
        private String status;
        private String qr_code;

        public int getEmployee_id() {
            return employee_id;
        }

        public String getType() {
            return type;
        }

        // ⭐ Return as INT for easy use
        public int getAmount() {
            try {
                return Integer.parseInt(amount);
            } catch (Exception e) {
                return 0;
            }
        }

        // ⭐ Get original string if needed
        public String getAmountString() {
            return amount;
        }

        public String getPayment_proof() {
            return payment_proof;
        }

        public String getStatus() {
            return status;
        }

        public String getQr_code() {
            return qr_code != null ? qr_code : "";
        }
    }
}