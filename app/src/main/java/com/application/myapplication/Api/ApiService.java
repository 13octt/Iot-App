package com.application.myapplication.Api;

import com.application.myapplication.Call.DeviceData;
import com.application.myapplication.Call.Gauge;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("/data/{device_id}")
    Call<List<DeviceData>> getDataByID(@Path("device_id") String device_id);

    @GET("/device")
    Call<List<Gauge>> getDataReceived();
}
