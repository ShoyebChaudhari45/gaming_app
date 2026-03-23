package com.example.gameapp.models.response;

import com.google.gson.annotations.SerializedName;

public class SupportResponse {

    @SerializedName("status")
    private boolean status;

    @SerializedName("data")
    private SupportData data;

    public boolean isStatus() {
        return status;
    }

    public SupportData getData() {
        return data;
    }

    public static class SupportData {

        @SerializedName("id")
        private int id;

        @SerializedName("contact_no")
        private String contactNo;

        @SerializedName("whatsapp_no")
        private String whatsappNo;

        @SerializedName("email_id")
        private String emailId;

        @SerializedName("telegram_link")
        private String telegramLink;

        @SerializedName("proof_link")
        private String proofLink;

        // ================= SAFE CHECKS =================

        private boolean isValid(String value) {
            return value != null
                    && !value.trim().isEmpty()
                    && !"null".equalsIgnoreCase(value.trim());
        }

        public boolean hasValidContact() {
            return isValid(contactNo);
        }

        public boolean hasValidWhatsapp() {
            return isValid(whatsappNo);
        }

        public boolean hasValidEmail() {
            return isValid(emailId);
        }

        public boolean hasValidTelegram() {
            return isValid(telegramLink);
        }

        public boolean hasValidProof() {
            return isValid(proofLink);
        }

        // ================= GETTERS =================

        public String getContactNo() {
            return contactNo;
        }

        public String getWhatsappNo() {
            return whatsappNo;
        }

        public String getEmailId() {
            return emailId;
        }

        public String getTelegramLink() {
            return telegramLink;
        }

        public String getProofLink() {
            return proofLink;
        }
    }
}