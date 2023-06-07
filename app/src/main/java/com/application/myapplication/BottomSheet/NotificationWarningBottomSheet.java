package com.application.myapplication.BottomSheet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.application.myapplication.Api.ApiRetrofit;
import com.application.myapplication.Api.ApiService;
import com.application.myapplication.Call.DataReceived;
import com.application.myapplication.Call.Gauge;
import com.application.myapplication.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationWarningBottomSheet extends AppCompatActivity {
    
    TextView tempValue;
    TextView humidityValue;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_warning_bottom_sheet);
        LayoutInflater layoutInflater = LayoutInflater.from(NotificationWarningBottomSheet.this);
        View view = layoutInflater.inflate(R.layout.fragment_bed_room, null);
        tempValue = view.findViewById(R.id.temperature_value_bed_room);
        humidityValue = view.findViewById(R.id.humidity_value_bed_room);

    }
}