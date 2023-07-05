package com.application.myapplication.Service;

import static android.app.Service.START_STICKY;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
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

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;

public class MqttService extends Service {
    private static final String CHANNEL_ID = "mqtt_channel";
    private static final int NOTIFICATION_ID = 1;
    private MqttAndroidClient mqttAndroidClient;
    private static final String TAG = "MqttService";
    private MqttAndroidClient mqttClient;
    private final IBinder binder = new LocalBinder();
    private MqttConnectOptions mqttConnectOptions;
    Context context;
    public class LocalBinder extends Binder {
        MqttService getService() {
            return MqttService.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
//        startForeground(NOTIFICATION_ID, createNotification());
        initializeMqttClient();
        connectToMQTTBroker();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        connectAndSubscribe();
        return START_STICKY;
    }

    private void initializeMqttClient() {
        // Initialize your MQTT client
        String serverUri = "tcp://35.239.121.30:1883";
        String clientId = MqttClient.generateClientId();
//        mqttClient = new MqttAndroidClient(context, serverUri, clientId);
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId, Ack.AUTO_ACK);

    }

    public void connectToMQTTBroker() {
        String username = "duy";
        String password = "duy";
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
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d("MQTT", "Connect Failed");
            }
        });
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
