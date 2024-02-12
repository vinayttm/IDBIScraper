package com.app.idbicscraper.api;

import android.util.Log;

import com.app.idbicscraper.response.GetUpiStatusResponse;
import com.app.idbicscraper.utils.Const;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ApiCaller {
    private final OkHttpClient client = new OkHttpClient();
    boolean getUpiStatus = false;

    public void fetchData(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    System.out.println(responseData);
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }
        });
    }

    public void postData(String url, String jsonData) {
        MediaType contentType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(contentType, jsonData);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    Log.d("Response", responseData);
                } else {
                    System.out.println("Error: " + response.code() + " " + response.message());
                }
            }
        });
    }

    public boolean getUpiStatus(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        CountDownLatch latch = new CountDownLatch(1);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d("Response",response.body().toString());
                        Gson gson = new Gson();
                        GetUpiStatusResponse statusResponse = gson.fromJson(response.body().charStream(), GetUpiStatusResponse.class);
                        if (statusResponse.getResult().equals("1")) {
                            Log.d("getUpiStatus", "Active: " + response.code() + " " + response.message());
                            getUpiStatus = true;
                        } else {
                            Log.d("getUpiStatus", "Inactive: " + response.code() + " " + response.message());
                            getUpiStatus = false;
                        }
                    } catch (Exception e) {
                        Log.e("getUpiStatus", "Error parsing response: " + e.getMessage());
                        getUpiStatus = false;
                    } finally {
                        latch.countDown(); // Release the latch
                    }
                } else {
                    Log.e("getUpiStatus", "Error: " + response.code() + " " + response.message());
                    getUpiStatus = false;
                    latch.countDown(); // Release the latch in case of failure
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("getUpiStatus", "Network error: " + e.getMessage());
                getUpiStatus = false;
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return getUpiStatus;
    }


}
