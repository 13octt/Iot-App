package com.application.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

            if(currentFragment instanceof HomeFragment && item.getItemId() == R.id.action_home){
                selectedFragment = currentFragment;
            } else if (currentFragment instanceof  CameraFragment && item.getItemId() == R.id.action_camera){
                selectedFragment = currentFragment;
            } else if (currentFragment instanceof  InsightFragment && item.getItemId() == R.id.action_profile){
                selectedFragment = currentFragment;
            } else if (currentFragment instanceof SettingFragment && item.getItemId() == R.id.action_setting) {
                selectedFragment = currentFragment;
            } else {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        selectedFragment = new HomeFragment();
                        currentFragment = new HomeFragment();
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

//         Log out
        logOut.setOnClickListener(view -> {
            Intent logOut = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(logOut);
            finish();
        });
//         Warning alert
        notification.setOnClickListener(view2 -> warningAlert());
    }

    private void warningAlert(){
        View bottomSheet = getLayoutInflater().inflate(R.layout.notification_warning_bottom_sheet, null);
        alertImageView = bottomSheet.findViewById(R.id.alertImageViewBottomSheet);
        alertTextView = bottomSheet.findViewById(R.id.alertTextViewBottomSheet);
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
                        String alert = calculateAlert(temperature, humidity);
                        alertBottomSheet(alert);
                        showBottomSheet(bottomSheet);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Gauge>> call, @NonNull Throwable t) {
                Log.e("API", t.getMessage());
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void alertBottomSheet(String alert) {
        if (alert.equals("Không nguy hiểm") || alert.equals("Thoải mái")) {
            alertTextView.setText("Nhiệt độ và độ ẩm ở mức an toàn");
            alertImageView.setImageResource(R.drawable.ic_alert_green);
        } else if (alert.equals("Khá Không thoải mái") || alert.equals("Không thoải mái")) {
            alertTextView.setText("Cảnh báo nhiệt độ và độ ẩm ở mức nguy cơ nguy hiểm");
            alertImageView.setImageResource(R.drawable.ic_alert_yellow);
        } else if (alert.equals("Nguy hiểm") || alert.equals("Rất nguy hiểm ")) {
            alertTextView.setText("Cảnh báo nhiệt độ và độ ẩm ở mức nguy hiểm");
            alertImageView.setImageResource(R.drawable.ic_alert_red);
        } else {
            alertTextView.setText("Nhiệt độ và độ ẩm không xác định");
            alertImageView.setImageResource(R.drawable.ic_alert_yellow);
        }
    }
    private void showBottomSheet(View bottomSheet) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
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