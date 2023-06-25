package com.application.myapplication.Fragment.Home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.application.myapplication.Api.ApiRetrofit;
import com.application.myapplication.Api.ApiService;
import com.application.myapplication.Call.DataReceived;
import com.application.myapplication.Call.Gauge;
import com.application.myapplication.Call.Monitor;
import com.application.myapplication.Call.LedState;
import com.application.myapplication.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BedRoomFragment extends Fragment {
    private MqttAndroidClient mqttAndroidClient;
    private TextView tempValue, humidityValue;
    String serverUri = "tcp://35.222.45.221:1883";
    String clientId = "SMART_HOME";
    private SwitchCompat buttonLed;
    public BedRoomFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bed_room, container, false);
        tempValue = view.findViewById(R.id.temperature_value_bed_room);
        humidityValue = view.findViewById(R.id.humidity_value_bed_room);
        buttonLed = view.findViewById(R.id.btn_lamp);

        // PUT LED STATE
        buttonLed.setOnCheckedChangeListener((compoundButton, isChecked) -> updateLedState(isChecked));
        //GET API 2S 1L
        reCallAPI();

        return view;
    }


    private void reCallAPI(){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                callTempHumidityValue();
                getLedState();
            }
        };
        timer.schedule(timerTask, 0, 5000);
    }
    private void updateLedState(boolean ledStatus){
        ApiService apiService = ApiRetrofit.getClient().create(ApiService.class);
        LedState led = new LedState(ledStatus);
        Call<Void> call = apiService.updateLedState(led);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getContext(), "Update Led State Successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getLedState(){
        ApiService apiService = ApiRetrofit.getClient().create(ApiService.class);
        Call<Monitor> call = apiService.getLampState();
        call.enqueue(new Callback<Monitor>() {
            @Override
            public void onResponse(@NonNull Call<Monitor> call, @NonNull Response<Monitor> response) {
                if(response.isSuccessful()){
                    Monitor led = response.body();
                    if(led != null){
                        boolean isLedOn = led.isLed_state();
                        buttonLed.setChecked(isLedOn);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<Monitor> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void callTempHumidityValue() {
        ApiService apiService = ApiRetrofit.getClient().create(ApiService.class);
        Call<List<Gauge>> callTempHumidity = apiService.getDataReceived();
        callTempHumidity.enqueue(new Callback<List<Gauge>>() {
            @Override
            public void onResponse(@NonNull Call<List<Gauge>> call, @NonNull Response<List<Gauge>> response) {
                if (response.isSuccessful()) {
                    List<Gauge> list = response.body();
                    if (list != null) {
                        Gauge gauge = list.get(0);
                        DataReceived dataReceived = gauge.getDataReceived();
                        float temp = dataReceived.getTemperature();
                        float humidityVal = dataReceived.getHumidity();
                        String temperature = String.valueOf(temp);
                        String humidity = String.valueOf(humidityVal);

                        tempValue.setText(temperature);
                        humidityValue.setText(humidity);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Gauge>> call, @NonNull Throwable t) {

            }
        });
    }
    private void connectToMQTTBroker() {
        try {
            String username = "thanhduy";
            String password = "thanhduy";

            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setAutomaticReconnect(true);
            mqttConnectOptions.setCleanSession(true);
            mqttConnectOptions.setUserName(username);
            mqttConnectOptions.setPassword(password.toCharArray());
            IMqttToken token = mqttAndroidClient.connect(mqttConnectOptions);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT", "Connect to Broker Successfully");
//                    Toast.makeText(getContext(), "Connect to Broker Successfully", Toast.LENGTH_SHORT).show();
//                    monitorDevices();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Connection failed
                    Log.d("MQTT", "Connect Failed");
//                    Toast.makeText(getContext(), "Failed to Connect to MQTT Broker", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.e("API bedroom", e.getMessage());
        }
    }
    public void publishMessage(String topic, String payload, int qos) {
        try {
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(qos);
            mqttAndroidClient.publish(topic, message);
            Log.d("MQTT", "Topic: " + topic + ", Message: " + message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}