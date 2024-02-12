package com.app.idbicscraper.localstorage;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String PREFERENCE_NAME = "MyPrefs";

    private static SharedPreferencesManager instance;
    private SharedPreferences sharedPreferences;

    private SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context);
        }
        return instance;
    }

    public void saveStringValue(String key,String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getStringValue(String key) {
        return sharedPreferences.getString(key, null);
    }
    public void removeValue(String key) {
         sharedPreferences.edit().remove(key).apply();
    }
    public void clearAllValues() {
        sharedPreferences.edit().clear().apply();
    }

}
