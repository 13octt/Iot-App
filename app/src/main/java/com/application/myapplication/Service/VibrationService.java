package com.application.myapplication.Service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.application.myapplication.Api.ApiRetrofit;
import com.application.myapplication.Api.ApiService;
import com.application.myapplication.Call.DataReceived;
import com.application.myapplication.Call.Gauge;
import com.application.myapplication.MainActivity;
import com.application.myapplication.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VibrationService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "AlertChannel";
    private Context context;
    boolean flag = false;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification notification = createNotification();
        flag = false;
        showSafeNotification();
        startForeground(NOTIFICATION_ID, notification);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                checkWeatherConditions();
            }
        };
        timer.schedule(timerTask, 0, 5000);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Create the notification and set its properties
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Alert")
                .setSound(null)
                .setContentText("Vibration Service Running")
                .setSmallIcon(R.drawable.ic_launcher_foreground);

//        builder.build();
        return builder.build();
    }

    private void checkWeatherConditions() {
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
                        if (temp > 32 && temp <= 38 && humidityVal > 60) {
                            showDangerNotification();
                            vibrateDevice();
                        } else if (temp > 38) {
                            showDangerNotification();
                            vibrateDevice();
                        } else showSafeNotification();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Gauge>> call, @NonNull Throwable t) {
            }
        });
    }

    private void showDangerNotification() {
        createNotification();
        Intent main = new Intent(context, MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, main, PendingIntent.FLAG_MUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Warning")
                .setContentText("High temperature and humidity")
                .setSound(null)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void showSafeNotification() {
        createNotification();
        Intent main = new Intent(context, MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, main, PendingIntent.FLAG_MUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Notification")
                .setContentText("Safe temperature and humidity")
                .setSound(null)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // Hỗ trợ các phiên bản Android cũ hơn
                vibrator.vibrate(1000);
            }
        }
    }
}
