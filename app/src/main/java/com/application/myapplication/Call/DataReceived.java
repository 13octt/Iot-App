package com.application.myapplication.Call;

import com.google.gson.annotations.SerializedName;

public class DataReceived {
    @SerializedName("device_id")
    private final String deviceId;

    @SerializedName("temperature")
    private final int temperature;

    @SerializedName("humidity")
    private final int humidity;

    @SerializedName("light")
    private final int light;

    @SerializedName("receive_time")
    private final String receiveTime;

    public DataReceived(String deviceId, int temperature, int humidity, int light, String receiveTime) {
        this.deviceId = deviceId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.light = light;
        this.receiveTime = receiveTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getLight() {
        return light;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

}
