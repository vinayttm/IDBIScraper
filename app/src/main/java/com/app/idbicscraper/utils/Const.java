package com.app.idbicscraper.utils;

import android.content.Context;
import android.widget.Toast;

public class Const {

    public static final String TAG = "RESULT";

    public static boolean isLoginProcess = false;
    public  static  String baseUrl = "https://91.playludo.app/api/CommonAPI/";

    public  static  String SaveMobileBankTransactionUrl =   baseUrl + "SaveMobilebankTransaction";

    public  static  String updateDateBasedOnUpi = baseUrl + "UpdateDateBasedOnUpi?upiId=";
    public  static  String getUpiStatusUrl  = baseUrl + "GetUpiStatus?upiId=";

    public static Context context;
    public static Boolean processToClickMiniStatement = false;
    public static Boolean isLoading = false;
    public  static  Boolean serverError = false;
    public  static  Boolean isLogin = false;

    public  static  Boolean isCompleteStatement = false;
    public static String pinText = "";
    public static String BankLoginId = "";

    public static String upiId = "";
    public  static  int loginTry = 0;

    public  static  String packName = "com.snapwork.IDBI";

    public static void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
