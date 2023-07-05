package com.application.myapplication.Service;

import static android.app.Service.START_STICKY;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.application.myapplication.Fragment.Home.BedRoomFragment;
import com.application.myapplication.R;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import info.mqtt.android.service.MqttAndroidClient;

public class MqttService extends Service {
    private static final String CHANNEL_ID = "mqtt_channel";
    private static final int NOTIFICATION_ID = 1;
    private MqttAndroidClient mqttAndroidClient;
    String serverUri = "tcp://35.222.45.221:1883";
    String clientId = "SMART_HOME_IOT";
    String username = "thanhduy";
    String password = "thanhduy";
    private MqttConnectOptions mqttConnectOptions;
    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(NOTIFICATION_ID, createNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = getApplicationContext();
        try {
            connectToMqttBroker(context);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Disconnect MQTT connection and perform any necessary cleanup
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    private void connectToMqttBroker() {
//        String brokerUrl = "tcp://mqtt.example.com:1883"; // Replace with your MQTT broker URL
//
//        try {
//            IMqttClient mqttClient = new MqttClient(brokerUrl, MqttClient.generateClientId());
//            MqttConnectOptions connectOptions = new MqttConnectOptions();
//            connectOptions.setCleanSession(true);
//
//            mqttClient.connect(connectOptions);
//
//            // Subscribe to MQTT topics or perform other operations after successful connection
//
//            mqttClient.setCallback(new MqttCallback() {
//                @Override
//                public void connectionLost(Throwable cause) {
//                    // Handle connection lost
//                }
//
//                @Override
//                public void messageArrived(String topic, MqttMessage message) throws Exception {
//                    // Handle incoming MQTT messages
//                }
//
//                @Override
//                public void deliveryComplete(IMqttDeliveryToken token) {
//                    // Handle message delivery completion
//                }
//            });
//        } catch (MqttException e) {
//            e.printStackTrace();
//            // Handle MQTT connection exception
//        }
//    }

    public void connectToMqttBroker(Context context) throws MqttException {
        mqttAndroidClient = new MqttAndroidClient(this, serverUri, clientId, null);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                // Connection established
                Log.d("MQTT", "Connected to MQTT Broker");
            }

            @Override
            public void connectionLost(Throwable cause) {
                // Connection lost
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // New message received
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Message delivered
            }
        });

        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(true);
        connectOptions.setAutomaticReconnect(true);
        connectOptions.setUserName(username);
        connectOptions.setPassword(password.toCharArray());

        if(mqttAndroidClient != null){
            mqttAndroidClient.connect(connectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // Connection successful
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Connection failed
                }
            });
        } else {
            return;
        }

    }

    private Notification createNotification() {
        createNotificationChannel();

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MQTT Service")
                .setContentText("Maintaining MQTT connection")
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "MQTT Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }


}
