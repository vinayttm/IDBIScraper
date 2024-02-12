package com.app.idbicscraper;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.app.idbicscraper.client.RetrofitClient;
import com.app.idbicscraper.localstorage.SharedPreferencesManager;
import com.app.idbicscraper.response.GetUpiStatusResponse;
import com.app.idbicscraper.services.MyAccessibilityService;
import com.app.idbicscraper.utils.AccessibilityMethod;
import com.app.idbicscraper.utils.Const;
import com.app.idbicscraper.utils.MyDialog;
import com.google.gson.Gson;
import idbi.app.com.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText loginIdEditText, upiIdEditText, pinEditText;

    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Const.context = this;
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);
        setContentView(R.layout.activity_main);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        loginIdEditText = findViewById(R.id.loginId);
        upiIdEditText = findViewById(R.id.upiId);
        pinEditText = findViewById(R.id.pin);
        loginIdEditText.setText(sharedPreferencesManager.getStringValue("loginId"));
        upiIdEditText.setText(sharedPreferencesManager.getStringValue("upiId"));
        pinEditText.setText(sharedPreferencesManager.getStringValue("pinText"));
        boolean isServiceEnabled = AccessibilityMethod.isAccessibilityServiceEnabled(
                this, MyAccessibilityService.class);
        if (!isServiceEnabled) {
            MyDialog.showDialog(MainActivity.this, "Accessibility Permission Required.", "To use this app, you need to enable Accessibility Service. Go to Settings to enable it?",
                    new MyDialog.DialogClickListener() {
                        @Override
                        public void onPositiveButtonClick() {
                            openAccessibilitySettings();
                        }

                        @Override
                        public void onNegativeButtonClick() {
                        }
                    });
        }
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    checkUpiStatus(upiIdEditText.getText().toString(), loginIdEditText.getText().toString(),
                            pinEditText.getText().toString());
                }
            }
        });
    }

    private void checkUpiStatus(String upiId, String loginId, String pinText) {
        Call<GetUpiStatusResponse> call = RetrofitClient.getMyApi().getUpiStatus(upiId);
        call.enqueue(new Callback<GetUpiStatusResponse>() {
            @Override
            public void onResponse(Call<GetUpiStatusResponse> call, Response<GetUpiStatusResponse> response) {
                if (response != null && response.isSuccessful() && response.body() != null) {
                    GetUpiStatusResponse responseData = response.body();
                    Gson gson = new Gson();
                    String apiResponse = gson.toJson(responseData);
                    Log.d("apiResponse", apiResponse.toString());
                    if (responseData.getResult().equals("1")) {
                        saveToSharedPreferences(loginId, upiId, pinText);
                        openApp();
                    } else {
                        Const.showToast("Please active UPI.");
                    }
                } else {
                    Const.showToast("Please enter correct upi Id");
                }
            }

            @Override
            public void onFailure(Call<GetUpiStatusResponse> call, Throwable t) {
                Const.showToast("Please enter correct upi Id");
            }
        });
    }

    private void saveToSharedPreferences(String loginId, String upiId, String pinText) {
        sharedPreferencesManager.saveStringValue("loginId", loginId);
        sharedPreferencesManager.saveStringValue("upiId", upiId);
        sharedPreferencesManager.saveStringValue("pinText", pinText);
        String savedLoginId = sharedPreferencesManager.getStringValue("loginId");
        String savedUpiId = sharedPreferencesManager.getStringValue("upiId");
        String savedPinText = sharedPreferencesManager.getStringValue("pinText");
        Log.d("savedLoginId", savedLoginId);
        Log.d("savedUpiId", savedUpiId);
        Log.d("savedPinText", savedPinText);
        Const.BankLoginId = savedLoginId;
        Const.upiId = savedUpiId;
        Const.pinText = savedPinText;
    }

    private void openApp() {
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(Const.packName);
        if (intent != null) {
            startActivity(intent);
        } else {
            System.out.println("App with package name " + Const.packName + " not found.");
        }
    }

    private void openAccessibilitySettings() {
        Intent accessibilityIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(accessibilityIntent);
    }

    private boolean validateForm() {
        String loginId = loginIdEditText.getText().toString().trim();
        String upiId = upiIdEditText.getText().toString().trim();
        String pin = pinEditText.getText().toString().trim();
        if (loginId.isEmpty()) {
            loginIdEditText.setError("Please enter Login ID");
            return false;
        }

        if (upiId.isEmpty()) {
            upiIdEditText.setError("Please enter UPI");
            return false;
        }

        if (pin.isEmpty()) {
            pinEditText.setError("Please enter PIN");
            return false;
        }
        return true;
    }


}