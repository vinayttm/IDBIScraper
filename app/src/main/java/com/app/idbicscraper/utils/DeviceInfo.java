package com.app.idbicscraper.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class DeviceInfo {

    public static String getModelNumber() {
        return Build.MODEL;
    }

    public static String generateSecureId(Context context) {
        String androidId = getAndroidId(context);
        return androidId;
    }

    private static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
