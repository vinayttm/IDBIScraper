package com.app.idbicscraper.api;
import com.app.idbicscraper.response.GetUpiStatusResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("SaveMobilebankTransaction")
    Call<Void> transactionData(@Body String encryptedData);


    @GET("UpdateDateBasedOnUpi")
    Call<Void> updateDateBasedOnUpi(@Query("upiId") String upiId);

    @GET("GetUpiStatus")
    Call<GetUpiStatusResponse> getUpiStatus(@Query("upiId") String upiId);


}