package com.application.myapplication.Fragment;

import static android.service.controls.ControlsProviderService.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.application.myapplication.R;
import com.application.myapplication.User.SignInActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class HomeFragment extends Fragment {

    LinearLayout roomLinearLayout;
    private int flagLamp = 0, flagTV = 0, flagAC = 0, flagFan = 0, flagLight = 0;
    Button btnLamp, btnTV, btnAC, btnFan, btnLight;
    private MqttAndroidClient mqttAndroidClient;
    private static final String TOPIC_TEMPERATURE = "TEMP";
    private static final String TOPIC_HUMIDITY = "HUMI";
    private static final String TOPIC_GAS = "GAS";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();

        String serverUri = "tcp://35.222.45.221:1883";
        String clientId = "SMART_HOME";

//        String serverUri = "mqtts://bea9028a.ala.us-east-1.emqxsl.com:8883";
//        String clientId = "abc";

        mqttAndroidClient = new MqttAndroidClient(getContext(), serverUri, clientId);
        connectToMQTTBroker();
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "Connection lost: " + cause.getMessage());
                Toast.makeText(getContext(), "Connection lost", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String payload = new String(message.getPayload());
                Log.d("MQTT", "Received message on topic: " + topic + " , " + payload);
                if (topic.equals("TEMP")) {
                    // xử lý dữ liệu nhiệt độ
//                    tvTemp = findViewById(R.id.temperature_value_text_view);
//                    tvTemp.setText(payload);
                } else if (topic.equals("HUMI")) {
                    // xử lý dữ liệu độ ẩm
//                    tvHumidity = findViewById(R.id.humidity_value_text_view);
//                    tvHumidity.setText(payload);
                } else if(topic.equals("GAS")){
//                    tvGas = findViewById(R.id.gas_value_text_view);
//                    tvGas.setText(payload);
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });


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

        btnTV.setOnClickListener(view1 -> {
            flagTV++;
            if(flagTV % 2 == 0){
                Log.d("btn tv", "off");
                publishMessage("TV", "0", 1);
            } else {
                Log.d("btn tv", "on");
                publishMessage("TV", "1", 1);
            }
        });

        btnAC.setOnClickListener(view1 -> {
            flagAC++;
            if(flagAC % 2 == 0){
                Log.d("btn AC", "off");
                publishMessage("AC", "0", 1);
            } else {
                Log.d("btn AC", "on");
                publishMessage("AC", "1", 1);
            }
        });

        btnFan.setOnClickListener(view1 -> {
            flagFan++;
            if(flagFan % 2 == 0){
                Log.d("btn FAN", "off");
                publishMessage("FAN", "0", 1);
            } else {
                Log.d("btn FAN", "on");
                publishMessage("FAN", "1", 1);
            }
        });

        btnLight.setOnClickListener(view1 -> {
            flagLight++;
            if(flagLight % 2 == 0){
                Log.d("btn LIGHT", "off");
                publishMessage("LIGHT", "0", 1);
            } else {
                Log.d("btn tv", "on");
                publishMessage("LIGHT", "1", 1);
            }
        });


    }

    private void initUI() {
        btnLamp = getView().findViewById(R.id.btn_lamp);
        btnTV = getView().findViewById(R.id.btn_tv);
        btnAC = getView().findViewById(R.id.btn_ac);
        btnFan = getView().findViewById(R.id.btn_fan);
        btnLight =getView().findViewById(R.id.btn_light);
    }

    public void connectToMQTTBroker(){
        try {
            String username = "thanhduy";
            String password = "thanhduy";

//            String username = "duy";
//            String password = "duy";

            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setAutomaticReconnect(true);     //
            mqttConnectOptions.setCleanSession(true);       //
            mqttConnectOptions.setUserName(username);
            mqttConnectOptions.setPassword(password.toCharArray());
            IMqttToken token = mqttAndroidClient.connect(mqttConnectOptions);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // Connection success
                    Log.d("MQTT", "Connect Successfully");
//                    Toast.makeText(getContext(), "Connect Successfully", Toast.LENGTH_SHORT).show();

                    subscribeToTopic(TOPIC_TEMPERATURE);
                    subscribeToTopic(TOPIC_HUMIDITY);
                    subscribeToTopic(TOPIC_GAS);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Connection failed
                    Log.d("MQTT", "Connect Failed");
                    Toast.makeText(getContext(), "Connect Failed", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }



    public void subscribeToTopic(String topic){
//        String topic = "SENSORS";
        int qos = 1;
        try {
            IMqttToken subToken = mqttAndroidClient.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                    Log.d("MQTT", "Subscribed to topic: " + topic + ", qos: " + qos);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("MQTT", "Failed to subscribe.");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(String topic, String payload, int qos){
        try {
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(qos);
            mqttAndroidClient.publish(topic, message);
            Log.d("MQTT", "Topic: " + topic + ", Message: " + message);


        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void onClickFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.linear_iot_room, fragment);

    }

}