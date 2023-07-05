package com.application.myapplication.Fragment.Home;

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
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BedRoomFragment extends Fragment {
    private MqttAndroidClient mqttAndroidClient;
    private TextView tempValue, humidityValue;
    private SwitchCompat buttonLed;
    private static final String CHANNEL_ID = "1";
    private static final String CHANNEL_NAME = "Notification";
    private static final String CHANNEL_DESCRIPTION = "Temperature and Humidity Alert";
    private static final long VIBRATION_DURATION = 1000;
    private boolean isDangerous = false;
    private Vibrator vibrator;
    String serverUri = "tcp://35.239.121.30:1883";
    String clientId = "SMART_HOME_IOT";
    Context context;

    public BedRoomFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            Notification notification = new NotificationCompat.Builder(context, "channel_id")
                    .setContentTitle("Foreground Service")
                    .setContentText("Service is running...")
                    .setSound(null)
                    .setSmallIcon(R.drawable.ic_notification)
                    .build();
//            startForegroundService(20, notification);

        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = requireContext();
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bed_room, container, false);
        // REFERENCE UI
        tempValue = view.findViewById(R.id.temperature_value_bed_room);
        humidityValue = view.findViewById(R.id.humidity_value_bed_room);
        buttonLed = view.findViewById(R.id.btn_lamp);
        // PUT LED STATE
        buttonLed.setOnCheckedChangeListener((compoundButton, isChecked) -> updateLedState(isChecked));
        // GET API 2S 1L
        reCallAPI();

        // NOTIFICATION
//        createNotificationChannel();

        // MQTT
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId, Ack.AUTO_ACK);
        connectToMQTTBroker();
        mqttCallBack();
//        startMqttService();
//        startForeground();
        Intent serviceIntent = new Intent(context, VibrationService.class);
//        ContextCompat.startForegroundService(context, serviceIntent);
        startForeground(context, serviceIntent);


        return view;
    }

    private void mqttCallBack() {
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "Connection lost: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) { // delete throws Exception
                String payload = new String(message.getPayload());
                Log.d("MQTT", "Received message on topic: " + topic + " , " + payload);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });

    }

    private void reCallAPI() {
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

    private void updateLedState(boolean ledStatus) {
        ApiService apiService = ApiRetrofit.getClient().create(ApiService.class);
        LedState led = new LedState(ledStatus);
        Call<Void> call = apiService.updateLedState(led);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API Bedroom", "Update Led State Successfully");
                    if (ledStatus) {
                        publishMessage("LIGHT", "1", 0);
                    } else {
                        publishMessage("LIGHT", "0", 0);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("Monitor", t.getMessage());
            }
        });
    }

    private void getLedState() {
        ApiService apiService = ApiRetrofit.getClient().create(ApiService.class);
        Call<Monitor> call = apiService.getLampState();
        call.enqueue(new Callback<Monitor>() {
            @Override
            public void onResponse(@NonNull Call<Monitor> call, @NonNull Response<Monitor> response) {
                if (response.isSuccessful()) {
                    Monitor led = response.body();
                    if (led != null) {
                        boolean isLedOn = led.isLed_state();
                        buttonLed.setChecked(isLedOn);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Monitor> call, @NonNull Throwable t) {
                Log.e("API", t.getMessage());
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
//                        calculateNotification(temp, humidityVal);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Gauge>> call, @NonNull Throwable t) {

            }
        });
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

    public void publishMessage(String topic, String payload, int qos) {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        mqttAndroidClient.publish(topic, message);
        Log.d("MQTT", "Topic: " + topic + ", Message: " + message);
    }

    private void calculateNotification(float temperature, float humidity) {
//         Sử dụng bảng chỉ số nhiệt để xác định mức cảnh báo
        if (temperature < 27 || (temperature >= 27 && temperature <= 32 && humidity < 40)) {
            showNotification("Nhiệt độ và độ ẩm ở mức an toàn");
        } else if (temperature >= 27 && temperature <= 32 && humidity >= 40 && humidity <= 60) {
            showNotification("Nhiệt độ và độ ẩm ở mức thoải mái");
        } else if (temperature >= 27 && temperature <= 32 && humidity > 60) {
            showNotification("Cảnh báo nhiệt độ và độ ẩm ở mức nguy cơ nguy hiểm");
        } else if (temperature > 32 && temperature <= 38 && humidity < 40) {
            showNotification("Cảnh báo nhiệt độ và độ ẩm ở mức nguy cơ nguy hiểm");
        } else if (temperature > 32 && temperature <= 38 && humidity >= 40 && humidity <= 60) {
            showNotification("Cảnh báo nhiệt độ và độ ẩm ở mức nguy cơ nguy hiểm");
        } else if (temperature > 32 && temperature <= 38 && humidity > 60) {
            showNotification("Cảnh báo nhiệt độ và độ ẩm ở mức nguy hiểm");
            vibrateDevice();
        } else if (temperature > 38) {
            showNotification("Cảnh báo nhiệt độ và độ ẩm ở mức rất nguy hiểm");
            vibrateDevice();
        } else {
            showNotification("Nhiệt độ và độ ẩm Không xác định");
        }
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