package com.application.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.application.myapplication.Fragment.SettingFragment;
import com.application.myapplication.BottomSheet.TempBottomSheet;
import com.application.myapplication.Fragment.CameraFragment;
import com.application.myapplication.Fragment.HomeFragment;
import com.application.myapplication.Fragment.InsightFragment;
import com.application.myapplication.User.SplashActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private ImageButton logOut, noti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    replaceFragment(new InsightFragment());
                    break;
                case R.id.action_setting:
                    replaceFragment(new SettingFragment());
            }
            return true;
        });
        logOut = findViewById(R.id.btn_log_out);
        logOut.setOnClickListener(view -> {
            Intent logOut = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(logOut);
            finish();
        });

        noti = findViewById(R.id.notification_img_btn);
        noti.setOnClickListener(view -> {
            Intent a = new Intent(MainActivity.this, TempBottomSheet.class);
            startActivity(a);
        });

    }

    private void replaceFragment(Fragment fragment) {
        // load fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.relative_bottom_nav, fragment);
        fragmentTransaction.commit();
    }
}