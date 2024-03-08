package com.app.idbicscraper.utils;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.app.idbicscraper.api.ApiCaller;
import com.app.idbicscraper.client.RetrofitClient;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataFilter {

    public static void convertToJson(AccessibilityNodeInfo rootNode) {
        try {

            List<String> allText = AccessibilityMethod.getAllTextInNode(rootNode);
            Log.d("All Data", allText.toString());
            allText.removeIf(String::isEmpty);
            List<String> modifiedList = new ArrayList<>(allText);
            List<String> stringsToRemove = Arrays.asList("Current Account, ", " Current Account ,", "Total Avbl Bal", "Mini Statement", "I MASTER", "IDBICorp", "Current Account", " I MASTER", " Total Avbl Bal", "Date", "Services", "Cards", "Pay Now", "Accounts", "Home", "More", "Cardless ATM", "IMPS Payment", "BHIM UPI", "Payees", "Scan & Pay", "Home", "LOADING...", "LIFE ENTERPRISES", "Thanks for your support!", "Rate Us", " Dear Customer\n" +
                    "Please take a moment and rate us. It won't take more than a minute. Thanks for your support!", "Rate Us");
            String accountNumberPattern = "\\b\\d{16}\\b";
            Pattern pattern = Pattern.compile(accountNumberPattern);
            List<String> resultList = new ArrayList<>();
            for (String inputString : modifiedList) {
                Matcher matcher = pattern.matcher(inputString);
                String resultString = matcher.replaceAll("");
                resultList.add(resultString);
            }
            List<String> stringsToRemove2 = Arrays.asList("");
            List<String> resultList2 = new ArrayList<>();
            for (String item : resultList) {
                if (!stringsToRemove2.contains(item)) {
                    resultList2.add(item);
                }
            }

            Log.d("resultList List", resultList2.toString());
            List<String> filteredList = new ArrayList<>();
            for (String item : resultList2) {
                if (!stringsToRemove.contains(item)) {
                    filteredList.add(item);
                }
            }
            Log.d("filteredList List", filteredList.toString());
            String totalAmount = "";
            for(int i=0;i<filteredList.size();i++)
            {
                if(filteredList.get(i).contains("₹"))
                {
                    totalAmount = filteredList.get(i).replace("₹","");
                }
            }


            filteredList.remove(0);
            String modelNumber = "";
            String secureId = "";
            if (DeviceInfo.getModelNumber() != null && DeviceInfo.getModelNumber() != null && Const.context != null) {
                modelNumber = DeviceInfo.getModelNumber();
                secureId = DeviceInfo.generateSecureId(Const.context);
            }

            List<String> newFilterList = new ArrayList<>();
            for (int i = 0; i < filteredList.size(); i++) {
                if (i % 2 == 0) {
                    if (filteredList.get(i).contains("/")) {
                        String[] splitDate = filteredList.get(i).split("/");
                        String date = splitDate[0];
                        String month = splitDate[1];
                        String year = splitDate[2];
                        String yearString = year.substring(0, 4);
                        String amount = year.substring(4);
                        String finalDate = date + " " + month + " " + yearString;

                        newFilterList.add(finalDate);
                        newFilterList.add(amount);
                    }
                } else {
                    newFilterList.add(filteredList.get(i));
                }
            }
            Log.d("newFilterList ", newFilterList.toString());
            List<JSONObject> jsonObjects = new ArrayList<>();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < newFilterList.size(); ) {
                JSONObject jsonObject = new JSONObject();
                String dateAndAmount = newFilterList.get(i);
                String[] dateAndAmountList = separateDateAndAmount(dateAndAmount);
                String des1 = newFilterList.get(i + 1);
                String des2 = newFilterList.get(i + 2);
                String des = des1 + " " + des2;
                jsonObject.put("CreatedDate", convertDateFormat2(dateAndAmountList[0]));
                jsonObject.put("Amount", dateAndAmountList[1]);
                jsonObject.put("UPIId", getUPIId(des));
                jsonObject.put("RefNumber", getUtr(des) + " " +extractUTRFromDesc(des));
                jsonObject.put("Description", getUtr(des) + " " +extractUTRFromDesc(des));
                jsonObject.put("AccountBalance", totalAmount);
                jsonObject.put("BankName", "IDBI Bank-" + Const.BankLoginId);
                jsonObject.put("BankLoginId", Const.BankLoginId);
                jsonObject.put("DeviceInfo", modelNumber + "-" + secureId);
                jsonObjects.add(jsonObject);
                i = i + 3;
            }

            for (JSONObject object : jsonObjects) {
                jsonArray.put(object);
            }
            JSONObject finalJson = new JSONObject();
            Log.d("Data", jsonArray.toString());
            try {
                finalJson.put("Result", AES.encrypt(jsonArray.toString()));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
           sendTransactionData(finalJson.toString());
        } catch (Exception ignored) {

        }
    }

    public  static  String getUtr(String input) {
        String numericOnly = input.replaceAll("[^0-9]", "");
        return padWithZeroes(numericOnly, 12);

    }

    public static String padWithZeroes(String input, int length) {
        if (input.length() >= length) {
            return input.substring(0, length);
        } else {
            return "";
        }
    }
    public static String extractFirstTwelveDigits(String input) {
        String numericOnly = input.replaceAll("[^0-9]", "");
        return numericOnly.substring(0, Math.min(numericOnly.length(), 12));
    }


    public static String[] separateDateAndAmount(String input) {
        String datePattern = "\\d{2}/[A-Za-z]{3}/\\d{4}";
        String amountPattern = "\\d+\\.\\d{2}";
        String drCrPattern = "(Dr|Cr)";

        Pattern patternDate = Pattern.compile(datePattern);
        Pattern patternAmount = Pattern.compile(amountPattern);
        Pattern patternDrCr = Pattern.compile(drCrPattern);

        Matcher matcherDate = patternDate.matcher(input);
        Matcher matcherAmount = patternAmount.matcher(input);
        Matcher matcherDrCr = patternDrCr.matcher(input);
        String date = "";
        String amount = "";

        if (matcherDate.find() && matcherAmount.find()) {
            date = matcherDate.group();
            amount = matcherAmount.group();
            if (matcherDrCr.find()) {
                String drCr = matcherDrCr.group();
                if (drCr.equals("Dr")) {
                    amount = "-" + removeFirstFiveCharacters(amount);

                } else if (drCr.equals("Cr")) {
                    amount = "" + removeFirstFiveCharacters(amount);
                }
            }
        }

        return new String[]{date, amount};
    }


    public static String removeFirstFiveCharacters(String input) {
        if (input.length() > 4) {
            return input.substring(4);
        } else {
            return "";
        }
    }

    public static String extractUTRFromDesc(String description) {
        try {
            String[] split = description.split("/");
            String value = null;
            value = Arrays.stream(split).filter(x -> x.length() == 12).findFirst().orElse(null);
            if (value != null) {
                return value + " " + description;
            }
            return description;
        } catch (Exception e) {
            return description;
        }
    }

    public static String getUPIId(String description) {
        try {
            if (!description.contains("@"))
                return "";
            String[] split = description.split("/");
            String value = null;
            value = Arrays.stream(split).filter(x -> x.contains("@")).findFirst().orElse(null);
            return value != null ? value : "";
        } catch (Exception ex) {
            Log.d("Exception", ex.getMessage());
            return "";
        }
    }

    public static String convertDateFormat(String inputDate) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd MMM yyyy");
        try {
            Date date = inputDateFormat.parse(inputDate);
            SimpleDateFormat outputDateFormatPattern = new SimpleDateFormat("dd/MM/yy");
            return outputDateFormatPattern.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String convertDateFormat2(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MMM/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = null;
        try {
            Date date = inputFormat.parse(inputDate);
            formattedDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }


    private static void sendTransactionData(String data) {
        ApiCaller apiCaller = new ApiCaller();
        if (apiCaller.getUpiStatus(Const.getUpiStatusUrl + Const.upiId)) {
            Const.isLoading = true;
            apiCaller.postData(Const.SaveMobileBankTransactionUrl, data);
            updateDateBasedOnUpi();
        } else {
            Const.isLoading = false;
            Log.d("Failed to called api because of upi status off", "in Active status");
        }
    }

    private static void updateDateBasedOnUpi() {
        Log.d("updateDateBasedOnUpi", "Calling method updateDateBasedOnUpi()");
        System.out.println("Const.upiId" + Const.upiId);
        ApiCaller apiCaller = new ApiCaller();
        apiCaller.fetchData(Const.updateDateBasedOnUpi + Const.upiId);
        Const.isLoading = false;
    }
}