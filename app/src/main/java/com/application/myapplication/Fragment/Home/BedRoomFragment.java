package com.application.myapplication.Fragment.Home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.application.myapplication.Api.ApiRetrofit;
import com.application.myapplication.Api.ApiService;
import com.application.myapplication.Call.DataReceived;
import com.application.myapplication.Call.Gauge;
import com.application.myapplication.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
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
    private Button btnLamp, btnTV, btnAC, btnFan, btnLight;
    private int flagLamp = 0, flagTV = 0, flagAC = 0, flagFan = 0, flagLight = 0;
    String serverUri = "tcp://35.222.45.221:1883";
    String clientId = "SMART_HOME";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bed_room, container, false);
        tempValue = view.findViewById(R.id.temperature_value_bed_room);
        humidityValue = view.findViewById(R.id.humidity_value_bed_room);
        btnLamp = view.findViewById(R.id.btn_lamp);
        btnTV = view.findViewById(R.id.btn_tv);
        btnAC = view.findViewById(R.id.btn_ac);
        btnFan = view.findViewById(R.id.btn_fan);
        btnLight = view.findViewById(R.id.btn_light);

        //MQTT
//        mqttAndroidClient = new MqttAndroidClient(getContext(), serverUri, clientId);
//        connectToMQTTBroker();
//        mqttAndroidClient.setCallback(new MqttCallback() {
//            @Override
//            public void connectionLost(Throwable cause) {
//                Toast.makeText(getContext(), "Connection lost", Toast.LENGTH_SHORT).show();
//            }
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {}
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {}
//        });

//        monitorDevices();



        //Call API
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                callTempHumidityValue();
            }
        };
        timer.schedule(timerTask, 0, 5000);

        //

        return view;
    }

    private void monitorDevices() {
        // Monitor Lamp
        btnLamp.setOnClickListener(view1 -> {
            flagLamp++;
            if (flagLamp % 2 == 0) {
                Log.d("btn lamp", "off");
                // Xử lí sự kiện khi tắt nút
                publishMessage("LAMP", "0", 1);
            } else {
                Log.d("btn lamp", "on");
                // Xử lí sự kiện khi mở nút
                publishMessage("LAMP", "1", 1);
            }
        });

        // Monitor TV
        btnTV.setOnClickListener(view1 -> {
            flagTV++;
            if (flagTV % 2 == 0) {
                Log.d("btn tv", "off");
                publishMessage("TV", "0", 1);
            } else {
                Log.d("btn tv", "on");
                publishMessage("TV", "1", 1);
            }
        });

        // Monitor Air Conditioner
        btnAC.setOnClickListener(view1 -> {
            flagAC++;
            if (flagAC % 2 == 0) {
                Log.d("btn AC", "off");
                publishMessage("AC", "0", 1);
            } else {
                Log.d("btn AC", "on");
                publishMessage("AC", "1", 1);
            }
        });

        // Monitor Fan
        btnFan.setOnClickListener(view1 -> {
            flagFan++;
            if (flagFan % 2 == 0) {
                Log.d("btn FAN", "off");
                publishMessage("FAN", "0", 1);
            } else {
                Log.d("btn FAN", "on");
                publishMessage("FAN", "1", 1);
            }
        });

        // Monitor Light
        btnLight.setOnClickListener(view1 -> {
            flagLight++;
            if (flagLight % 2 == 0) {
                Log.d("btn LIGHT", "off");
                publishMessage("LIGHT", "0", 1);
            } else {
                Log.d("btn tv", "on");
                publishMessage("LIGHT", "1", 1);
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
        }
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
                        float humi = dataReceived.getHumidity();

                        String temperature = String.valueOf(temp);
                        String humidity = String.valueOf(humi);

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