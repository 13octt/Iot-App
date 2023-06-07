package com.application.myapplication.Fragment.Home;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.application.myapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.eclipse.paho.android.service.MqttAndroidClient;

public class HomeFragment extends Fragment {
    private TextView bedRoom, livingRoom, diningRoom;
    LinearLayout linearLayout;
    private int selectTabNumber = 1;
    private int flagLamp = 0, flagTV = 0, flagAC = 0, flagFan = 0, flagLight = 0;
    Button btnLamp, btnTV, btnAC, btnFan, btnLight;
    private MqttAndroidClient mqttAndroidClient;
    private static final String TOPIC_TEMPERATURE = "TEMP";
    private static final String TOPIC_HUMIDITY = "HUMI";
    private static final String TOPIC_GAS = "GAS";
    private TextView tvTemp, tvHumidity;
    CardView temp, humidity;
    BottomSheetDialog bottomSheetDialog;
    private DatabaseReference mDatabase;
    private GraphView mGraphView;
    private LineGraphSeries<DataPoint> mTemperatureSeries;
    private LineGraphSeries<DataPoint> mHumiditySeries;



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

        mqttAndroidClient = new MqttAndroidClient(getContext(), serverUri, clientId);

        // Connect to Broker function
//        connectToMQTTBroker();
//        mqttAndroidClient.setCallback(new MqttCallback() {
//            @Override
//            public void connectionLost(Throwable cause) {
//                Log.d(TAG, "Connection lost: " + cause.getMessage());
//                Toast.makeText(getContext(), "Connection lost", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//                String payload = new String(message.getPayload());
//                Log.d("MQTT", "Received message on topic: " + topic + " , " + payload);
//                if (topic.equals("TEMP")) {
//                    tvTemp = getView().findViewById(R.id.temperature_value_text_view);
//                    tvTemp.setText(payload);
//                } else if (topic.equals("HUMI")) {
//                    tvHumidity = getView().findViewById(R.id.humidity_value_text_view);
//                    tvHumidity.setText(payload);
//                } else if (topic.equals("GAS")) {
////                    tvGas = getView().findViewById(R.id.gas_value_text_view);
////                    tvGas.setText(payload);
//                }
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//            }
//        });

        // Monitor Device Function
//        monitorDevices();

        switchFragment(new BedRoomFragment());

        bedRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(1);
            }
        });
        livingRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(2);
            }
        });
        diningRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(3);
            }
        });

    }

    private void selectTab(int tabNumber) {
        TextView selectedTextView = null;
        TextView nonSelectedTextView1 = null;
        TextView nonSelectedTextView2 = null;

        if(tabNumber == 1){
            selectedTextView = bedRoom;
            nonSelectedTextView1 = livingRoom;
            nonSelectedTextView2 = diningRoom;
            switchFragment(new BedRoomFragment());
        } else if (tabNumber == 2) {
            nonSelectedTextView1 = bedRoom;
            selectedTextView = livingRoom;
            nonSelectedTextView2 = diningRoom;
            switchFragment(new LivingRoomFragment());
        } else if (tabNumber == 3) {
            nonSelectedTextView1 = bedRoom;
            nonSelectedTextView2 = livingRoom;
            selectedTextView = diningRoom;
            switchFragment(new DiningRoomFragment());
        }

        float slideTo = (tabNumber - selectTabNumber) * selectedTextView.getWidth();
        TranslateAnimation translateAnimation = new TranslateAnimation(0, slideTo, 0, 0);
        translateAnimation.setDuration(100);
        if(selectTabNumber == 1){
            bedRoom.startAnimation(translateAnimation);
        } else if (selectTabNumber == 2) {
            livingRoom.startAnimation(translateAnimation);

        } else if (selectTabNumber == 3) {
            diningRoom.startAnimation(translateAnimation);

        }

        TextView finalSelectedTextView = selectedTextView;
        TextView finalNonSelectedTextView1 = nonSelectedTextView1;
        TextView finalNonSelectedTextView2 = nonSelectedTextView2;

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                linearLayout.setBackgroundResource(R.drawable.border_light_white);
                finalSelectedTextView.setBackgroundResource(R.drawable.border_cardview);
                finalSelectedTextView.setTypeface(null, Typeface.BOLD);
                finalSelectedTextView.setTextColor(Color.BLACK);

                finalNonSelectedTextView1.setBackgroundResource(R.drawable.border_light_white_0);
                finalNonSelectedTextView1.setTypeface(null, Typeface.NORMAL);


                finalNonSelectedTextView2.setBackgroundResource(R.drawable.border_light_white_0);
                finalNonSelectedTextView2.setTypeface(null, Typeface.NORMAL);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        selectTabNumber = tabNumber;
    }

    private void initUI() {
//        btnLamp = getView().findViewById(R.id.btn_lamp);
//        btnTV = getView().findViewById(R.id.btn_tv);
//        btnAC = getView().findViewById(R.id.btn_ac);
//        btnFan = getView().findViewById(R.id.btn_fan);
//        btnLight = getView().findViewById(R.id.btn_light);
//        temp = getView().findViewById(R.id.card_view_temp);
//        humidity = getView().findViewById(R.id.card_view_humidity);

        bedRoom = getView().findViewById(R.id.bed_room_tab);
        livingRoom = getView().findViewById(R.id.living_room_tab);
        diningRoom = getView().findViewById(R.id.dining_room_tab);

//        linearLayout = getView().findViewById(R.id.linear_main_light);


    }

//    public void connectToMQTTBroker() {
//        try {
//            String username = "thanhduy";
//            String password = "thanhduy";
//
//            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
//            mqttConnectOptions.setAutomaticReconnect(true);
//            mqttConnectOptions.setCleanSession(true);
//            mqttConnectOptions.setUserName(username);
//            mqttConnectOptions.setPassword(password.toCharArray());
//            IMqttToken token = mqttAndroidClient.connect(mqttConnectOptions);
//            token.setActionCallback(new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    Log.d("MQTT", "Connect to Broker Successfully");
//
//                    subscribeToTopic(TOPIC_TEMPERATURE);
//                    subscribeToTopic(TOPIC_HUMIDITY);
//                    subscribeToTopic(TOPIC_GAS);
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    // Connection failed
//                    Log.d("MQTT", "Connect Failed");
//                }
//            });
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void subscribeToTopic(String topic) {
//        int qos = 1;
//        try {
//            IMqttToken subToken = mqttAndroidClient.subscribe(topic, qos);
//            subToken.setActionCallback(new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    // The message was published
//                    Log.d("MQTT", "Subscribed to topic: " + topic + ", qos: " + qos);
//
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    Log.d("MQTT", "Failed to subscribe.");
//                }
//            });
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void publishMessage(String topic, String payload, int qos) {
//        try {
//            MqttMessage message = new MqttMessage(payload.getBytes());
//            message.setQos(qos);
//            mqttAndroidClient.publish(topic, message);
//            Log.d("MQTT", "Topic: " + topic + ", Message: " + message);
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void monitorDevices() {
//        // Monitor Lamp
//        btnLamp.setOnClickListener(view1 -> {
//            flagLamp++;
//            if (flagLamp % 2 == 0) {
//                Log.d("btn lamp", "off");
//                // Xử lí sự kiện khi tắt nút
//                publishMessage("LAMP", "0", 1);
//            } else {
//                Log.d("btn lamp", "on");
//                // Xử lí sự kiện khi mở nút
//                publishMessage("LAMP", "1", 1);
//            }
//        });
//
//        // Monitor TV
//        btnTV.setOnClickListener(view1 -> {
//            flagTV++;
//            if (flagTV % 2 == 0) {
//                Log.d("btn tv", "off");
//                publishMessage("TV", "0", 1);
//            } else {
//                Log.d("btn tv", "on");
//                publishMessage("TV", "1", 1);
//            }
//        });
//
//        // Monitor Air Conditioner
//        btnAC.setOnClickListener(view1 -> {
//            flagAC++;
//            if (flagAC % 2 == 0) {
//                Log.d("btn AC", "off");
//                publishMessage("AC", "0", 1);
//            } else {
//                Log.d("btn AC", "on");
//                publishMessage("AC", "1", 1);
//            }
//        });
//
//        // Monitor Fan
//        btnFan.setOnClickListener(view1 -> {
//            flagFan++;
//            if (flagFan % 2 == 0) {
//                Log.d("btn FAN", "off");
//                publishMessage("FAN", "0", 1);
//            } else {
//                Log.d("btn FAN", "on");
//                publishMessage("FAN", "1", 1);
//            }
//        });
//
//        // Monitor Light
//        btnLight.setOnClickListener(view1 -> {
//            flagLight++;
//            if (flagLight % 2 == 0) {
//                Log.d("btn LIGHT", "off");
//                publishMessage("LIGHT", "0", 1);
//            } else {
//                Log.d("btn tv", "on");
//                publishMessage("LIGHT", "1", 1);
//            }
//        });
//    }
//
//    private void showDialog(View view) {
//        bottomSheetDialog = new BottomSheetDialog(getContext());
//        bottomSheetDialog.setContentView(view);
//        bottomSheetDialog.show();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//    }
//
//
//    private void callTemp() {
//
//    }
//
//    private void callHumidity() {
//
//    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_home, fragment);
        fragmentTransaction.commit();
    }


}