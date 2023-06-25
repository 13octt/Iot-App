package com.application.myapplication.Api;

import com.application.myapplication.Call.DeviceData;
import com.application.myapplication.Call.Gauge;
import com.application.myapplication.Call.Monitor;
import com.application.myapplication.Call.LedState;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("/data/{device_id}")
    Call<List<DeviceData>> getDataByID(@Path("device_id") String device_id);

    @GET("/device")
    Call<List<Gauge>> getDataReceived();

//    @PUT("/led")
//    Call<UpdateLedResponse> updateLedState(@Body boolean ledState);

    @PUT("/led")
    Call<Void> updateLedState(@Body LedState ledState);

    @GET("/led")
    Call<Monitor> getLampState();
}
