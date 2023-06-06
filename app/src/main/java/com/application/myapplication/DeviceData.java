package com.application.myapplication;

public class DeviceData {
    String _id;
    String device_id;
    float temperature;
    float humidity;
    float light;
    String receive_time;

    public DeviceData(String _id, String device_id, float temperature, float humidity, float light, String receive_time) {
        this._id = _id;
        this.device_id = device_id;
        this.temperature = temperature;
        this.humidity = humidity;
        this.light = light;
        this.receive_time = receive_time;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getLight() {
        return light;
    }

    public void setLight(float light) {
        this.light = light;
    }

    public String getReceive_time() {
        return receive_time;
    }

    public void setReceive_time(String receive_time) {
        this.receive_time = receive_time;
    }
}
