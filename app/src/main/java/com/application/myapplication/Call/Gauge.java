package com.application.myapplication.Call;

import com.application.myapplication.Call.DataReceived;

public class Gauge {
    private DataReceived data_received;

    public Gauge(DataReceived dataReceived) {
        this.data_received = dataReceived;
    }

    public DataReceived getDataReceived() {
        return data_received;
    }

    // For DashBoard
    private String device_name;
    private String last_connection_time;
    private String last_disconnection_time;
    private String create_time;
    private String time_status;
    private boolean enable;

    public DataReceived getData_received() {
        return data_received;
    }
    public String getDevice_name() {
        return device_name;
    }
    public String getLast_connection_time() {
        return last_connection_time;
    }
    public String getLast_disconnection_time() {
        return last_disconnection_time;
    }
    public String getCreate_time() {
        return create_time;
    }
    public boolean isEnable() {
        return enable;
    }
    public String getTime_status() {
        return time_status;
    }

}
