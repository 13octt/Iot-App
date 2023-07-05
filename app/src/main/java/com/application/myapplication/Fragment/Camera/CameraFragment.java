package com.application.myapplication.Fragment.Camera;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContextCompat.startForegroundService;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.application.myapplication.Api.ApiRetrofit;
import com.application.myapplication.Api.ApiService;
import com.application.myapplication.Call.DataReceived;
import com.application.myapplication.Call.Gauge;
import com.application.myapplication.Call.LedState;
import com.application.myapplication.Call.Monitor;
import com.application.myapplication.R;
import com.application.myapplication.Service.MqttService;
import com.application.myapplication.Service.VibrationService;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraFragment extends Fragment {
    private MqttAndroidClient mqttAndroidClient;
    public TextView send_mess, rec_mess;
    private com.google.android.material.floatingactionbutton.FloatingActionButton button_send;
    public EditText edit_text_mqtt;
    private static final String CHANNEL_ID = "1";
    private static final String CHANNEL_NAME = "Notification";
    private static final String CHANNEL_DESCRIPTION = "Temperature and Humidity Alert";
    private static final long VIBRATION_DURATION = 1000;
    private boolean isDangerous = false;
    private Vibrator vibrator;
    String serverUri = "tcp://35.239.121.30:1883";
    String clientId = "SMART_HOME";
    Context context;
    public  String message;
    public CameraFragment() {
    }
 public View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
//            if (notificationManager != null) {
//                notificationManager.createNotificationChannel(channel);
//            }
//            Notification notification = new NotificationCompat.Builder(context, "channel_id")
//                    .setContentTitle("Foreground Service")
//                    .setContentText("Service is running...")
//                    .setSmallIcon(R.drawable.ic_notification)
//                    .build();
////            startForegroundService(20, notification);
//
//        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = requireContext();
        MqttService mqttService = new MqttService();
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_camera, container, false);
        // REFERENCE UI
        //tempValue = view.findViewById(R.id.receiver);

        //humidityValue = view.findViewById(R.id.sender);
        send_mess = view.findViewById(R.id.sender);
        edit_text_mqtt = view.findViewById(R.id.edit_text_mqtt);
        button_send = view.findViewById(R.id.btn_send_mess);
        rec_mess = view.findViewById(R.id.receiver);

        button_send.setOnClickListener(clickView -> {
            // Gọi phương thức publishMessage với các tham số cụ thể
            message = edit_text_mqtt.getText().toString();
            send_mess.setText(message);
            publishMessage("texttospeak", message, 0);
            edit_text_mqtt.setText("");
        });
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId,Ack.AUTO_ACK);

        connectToMQTTBroker(view);


        return view;
    }






    public void connectToMQTTBroker(View view) {
        try {
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

                    subscribeToTopic("speechtotext",view);
                    subscribeToTopic("photo",view);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Connection failed
                    Log.d("MQTT", "Connect Failed");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void subscribeToTopic(String topic, View view) {

        int qos = 1;
        try {
            IMqttToken subToken = mqttAndroidClient.subscribe(topic, qos);
            subToken.getResponse();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                // Xử lý khi mất kết nối
                Log.d("MQTT", "Connection lost: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // Xử lý message được gửi đến topic đã đăng ký
                String payload = new String(message.getPayload());

                if(topic == "speechtotext"){
                    rec_mess.setText(payload);
                    Log.d("text",  rec_mess.getText().toString());

                }
                else {
                    if(topic == "photo"){
                        ImageView photo = view.findViewById(R.id.receive_photo);

                    }
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void publishMessage(String topic, String payload, int qos) {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        mqttAndroidClient.publish(topic, message);
        Log.d("MQTT", "Topic: " + topic + ", Message: " + message);
    }



    @SuppressLint("MissingPermission")
    private void showNotification(String message) {
        // Tạo intent để mở Activity khi người dùng nhấp vào thông báo
//        Intent intent = new Intent(getContext(), MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        // Xây dựng thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(CHANNEL_NAME)
                .setSubText(CHANNEL_DESCRIPTION)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setSound(null)
//                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Hiển thị thông báo
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(10, builder.build());

    }


    private void vibrateDevice() {
//        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//        if (vibrator != null) {
//            // Kiểm tra xem thiết bị có hỗ trợ rung không
//            if (vibrator.hasVibrator()) {
//                // Tạo một mẫu rung với độ dài và mẫu rung tùy chọn
//                VibrationEffect vibrationEffect = null;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    vibrationEffect = VibrationEffect.createOneShot(VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE);
//                }
//
//                // Rung thiết bị với mẫu rung đã tạo
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    vibrator.vibrate(vibrationEffect);
//                }
//            }
//        }

        Intent serviceIntent = new Intent(context, VibrationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(context, serviceIntent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Kiểm tra xem ứng dụng đã thoát hoặc màn hình đã tắt
        if (isDangerous && !requireActivity().isChangingConfigurations() && !requireActivity().isFinishing()) {
            vibrateDevice(); // Rung khi thoát ứng dụng hoặc tắt màn hình
        }
    }

    private void startForeground(Context context, Intent service) {
        service = new Intent(this.context, VibrationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForeground(requireContext(),service);
            startForegroundService(requireContext(), service);
        } else {
            requireContext().startService(service);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (vibrator != null) {
            vibrator.cancel();
        }
        Log.d("MQTT", "Destroy view");
    }
}