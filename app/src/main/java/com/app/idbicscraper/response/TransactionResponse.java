package com.app.idbicscraper.response;

import java.util.List;

public class TransactionResponse {
    private List<ResultItem> Result;

    public List<ResultItem> getResult() {
        return Result;
    }

    public void setResult(List<ResultItem> result) {
        this.Result = result;
    }

    public static class ResultItem {
        private String CreatedDate;
        private String Amount;
        private String UPIId;
        private String RefNumber;
        private String Description;
        private String AccountBalance;
        private String BankName;
        private String BankLoginId;
        private String DeviceInfo;

        public String getCreatedDate() {
            return CreatedDate;
        }

        public void setCreatedDate(String createdDate) {
            this.CreatedDate = createdDate;
        }

        public String getAmount() {
            return Amount;
        }

        public void setAmount(String amount) {
            this.Amount = amount;
        }

        public String getUPIId() {
            return UPIId;
        }

        public void setUPIId(String UPIId) {
            this.UPIId = UPIId;
        }

        public String getRefNumber() {
            return RefNumber;
        }

        public void setRefNumber(String refNumber) {
            this.RefNumber = refNumber;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            this.Description = description;
        }

        public String getAccountBalance() {
            return AccountBalance;
        }

        public void setAccountBalance(String accountBalance) {
            this.AccountBalance = accountBalance;
        }

        public String getBankName() {
            return BankName;
        }

        public void setBankName(String bankName) {
            this.BankName = bankName;
        }

        public String getBankLoginId() {
            return BankLoginId;
        }

        public void setBankLoginId(String bankLoginId) {
            this.BankLoginId = bankLoginId;
        }

        public String getDeviceInfo() {
            return DeviceInfo;
        }

        public void setDeviceInfo(String deviceInfo) {
            this.DeviceInfo = deviceInfo;
        }
    }
}
