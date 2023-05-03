package com.application.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.application.myapplication.Fragment.CameraFragment;
import com.application.myapplication.Fragment.HomeFragment;
import com.application.myapplication.Fragment.ProfileFragment;
import com.application.myapplication.User.SplashActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

//    private MqttAndroidClient mqttAndroidClient;
    private static final String TOPIC_TEMPERATURE = "TEMP";
    private static final String TOPIC_HUMIDITY = "HUMI";
    private static final String TOPIC_GAS = "GAS";
    private ImageButton logOut;
    private MqttAndroidClient mqttAndroidClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        String serverUri = "tcp://35.222.45.221:1883";
////        String serverUri = "mqtts://pb1f10e4.ala.us-east-1.emqxsl.com:8883";
//
//        String clientId = "SMART";
//
//        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
//        connectToMQTTBroker();

        // Load the store fragment by default
        replaceFragment(new HomeFragment());
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.action_camera:
                    replaceFragment(new CameraFragment());
                    break;
                case R.id.action_profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }
            return true;
        });
        logOut = findViewById(R.id.btn_log_out);
        logOut.setOnClickListener(view -> {
            Intent logOut = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(logOut);
            finish();
        });
    }

    private void replaceFragment(Fragment fragment) {
        // load fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.relative_bottom_nav, fragment);
        fragmentTransaction.commit();
    }

    public void connectToMQTTBroker(){
        try {
            String username = "thanhduy";
            String password = "thanhduy";

//            String username = "test";
//            String password = "test";

            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setUserName(username);
            mqttConnectOptions.setPassword(password.toCharArray());
            IMqttToken token = mqttAndroidClient.connect(mqttConnectOptions);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // Connection success
                    Log.d("MQTT", "Connect Successfully");
//                    Toast.makeText(getApplicationContext(), "Connect Successfully", Toast.LENGTH_SHORT).show();

//                    subscribeToTopic(TOPIC_TEMPERATURE);
//                    subscribeToTopic(TOPIC_HUMIDITY);
//                    subscribeToTopic(TOPIC_GAS);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Connection failed
                    Log.d("MQTT", "Connect Failed");
//                    Toast.makeText(getApplicationContext(), "Connect Failed", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}