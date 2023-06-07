package com.application.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.application.myapplication.Api.ApiRetrofit;
import com.application.myapplication.Api.ApiService;
import com.application.myapplication.Call.DataReceived;
import com.application.myapplication.Call.Gauge;
import com.application.myapplication.Fragment.CameraFragment;
import com.application.myapplication.Fragment.Home.HomeFragment;
import com.application.myapplication.Fragment.Insight.InsightFragment;
import com.application.myapplication.Fragment.SettingFragment;
import com.application.myapplication.User.SplashActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ImageButton logOut, notification;
    private BottomSheetDialog bottomSheetDialog;
    private Fragment currentFragment;

    private ImageView alertImageView;
    private TextView alertTextView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateUI();

        currentFragment = new HomeFragment();
        replaceFragment(currentFragment);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.action_home:
                    selectedFragment = new HomeFragment();
//                    replaceFragment(selectedFragment);
                    break;
                case R.id.action_camera:
                    selectedFragment = new CameraFragment();
//                    replaceFragment(selectedFragment);
                    break;
                case R.id.action_profile:
                    selectedFragment = new InsightFragment();
//                    replaceFragment(selectedFragment);
                    break;
                case R.id.action_setting:
                    selectedFragment = new SettingFragment();
//                    replaceFragment(selectedFragment);
            }
//            return true;

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.relative_bottom_nav, selectedFragment)
                        .commit();
                currentFragment = selectedFragment;
                return true;
            }
            return false;
        });

        // Log out
        logOut.setOnClickListener(view -> {
            Intent logOut = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(logOut);
            finish();
        });

        // Notification alert
        notification.setOnClickListener(view -> {
            // Khai báo View tham chiếu tới Bottom Sheet Dialog
            View bottomSheet = getLayoutInflater().inflate(R.layout.notification_warning_bottom_sheet, null);
            alertImageView = bottomSheet.findViewById(R.id.alertImageViewBottomSheet);
            alertTextView = bottomSheet.findViewById(R.id.alertTextViewBottomSheet);

            // Call API lấy dữ liệu nhiệt độ, độ ẩm
            ApiService apiService = ApiRetrofit.getClient().create(ApiService.class);
            Call<List<Gauge>> callTemp = apiService.getDataReceived();
            callTemp.enqueue(new Callback<List<Gauge>>() {
                @Override
                public void onResponse(@NonNull Call<List<Gauge>> call, @NonNull Response<List<Gauge>> response) {
                    if (response.isSuccessful()) {
                        List<Gauge> gaugeList = response.body();
                        if (gaugeList != null) {
                            Gauge gauge = gaugeList.get(0);
                            DataReceived dataReceived = gauge.getDataReceived();
                            float temperature = dataReceived.getTemperature();
                            float humidity = dataReceived.getHumidity();

                            // Viết hàm calculateAlert để thông báo với từng trường hợp nhiệt độ và độ ẩm cụ th
                            String alert = calculateAlert(temperature, humidity);
                            // Toast.makeText(MainActivity.this, alert.toString(), Toast.LENGTH_SHORT).show();

                            // Viết hàm alertBottomSheet để gán Text và Image vào Bottom Sheet
                            alertBottomSheet(alert);
                            // Viết hàm showBottomSheet để hiển thị BottomSheet
                            showBottomSheet(bottomSheet);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Gauge>> call, @NonNull Throwable t) {
                    Log.e("API", t.getMessage());
                }
            });
        });
    }

    private void alertBottomSheet(String alert){
        if(alert.equals("Không nguy hiểm") || alert.equals("Thoải mái")){
            alertTextView.setText("Không nguy hiểm");
            alertImageView.setImageResource(R.drawable.ic_alert_green);
        } else if (alert.equals("Khá Không thoải mái") || alert.equals("Không thoải mái")) {
            alertTextView.setText("Cảnh báo nguy cơ nguy hiểm");
            alertImageView.setImageResource(R.drawable.ic_alert_yellow);
        } else if(alert.equals("Nguy hiểm") || alert.equals("Rất nguy hiểm ")){
            alertTextView.setText("Cảnh báo nguy hiểm");
            alertImageView.setImageResource(R.drawable.ic_alert_red);
        } else {
            alertTextView.setText("Không xác định");
            alertImageView.setImageResource(R.drawable.ic_alert_yellow);
        }
    }

    private void showBottomSheet(View bottomSheet){
        bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        @SuppressLint("InflateParams") View bottomSheetView = getLayoutInflater().inflate(R.layout.notification_warning_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheet);
        bottomSheetDialog.show();
    }

    private String calculateAlert(float temperature, float humidity) {
        // Sử dụng bảng chỉ số nhiệt để xác định mức cảnh báo
        if (temperature < 27 || (temperature >= 27 && temperature <= 32 && humidity < 40)) {
            return "Không nguy hiểm";
        } else if (temperature >= 27 && temperature <= 32 && humidity >= 40 && humidity <= 60) {
            return "Thoải mái";
        } else if (temperature >= 27 && temperature <= 32 && humidity > 60) {
            return "Khá không thoải mái";
        } else if (temperature > 32 && temperature <= 38 && humidity < 40) {
            return "Khá không thoải mái";
        } else if (temperature > 32 && temperature <= 38 && humidity >= 40 && humidity <= 60) {
            return "Không thoải mái";
        } else if (temperature > 32 && temperature <= 38 && humidity > 60) {
            return "Nguy hiểm";
        } else if (temperature > 38) {
            return "Rất nguy hiểm";
        } else {
            return "Không xác định";
        }
    }

    @SuppressLint("ResourceType")
    private void updateUI() {
        notification = findViewById(R.id.notification_img_btn);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        logOut = findViewById(R.id.btn_log_out);
    }

    private void replaceFragment(Fragment fragment) {
        // load fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.relative_bottom_nav, fragment);
        fragmentTransaction.commit();
    }
}