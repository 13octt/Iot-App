package com.application.myapplication.Adapter;

import com.application.myapplication.DeviceData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("/data/{device_id}")
    Call<List<DeviceData>> getDataByID(@Path("device_id") String device_id);
}
