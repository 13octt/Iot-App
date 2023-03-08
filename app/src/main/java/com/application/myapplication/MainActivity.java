package com.application.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    TextView livingRoom, outDoor, bedRoom, bathRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        livingRoom = findViewById(R.id.iot_living_room);
//        livingRoom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replaceRoomFragment(new LivingRoomFragment());
//            }
//        });
//        outDoor = findViewById(R.id.iot_out_door);
//        outDoor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replaceRoomFragment(new OutdoorFragment());
//            }
//        });
//        bedRoom = findViewById(R.id.iot_bed_room);
//        bedRoom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replaceRoomFragment(new BedRoomFragment());
//            }
//        });
//        bathRoom = findViewById(R.id.iot_bath_room);
//        bathRoom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replaceRoomFragment(new BathRoomFragment());
//            }
//        });

        // Load the store fragment by default
        replaceFragment(new HomeFragment());
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
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

    }

//    private void onClickTextView(TextView textView ){
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (1){
//                    case R.id.iot_living_room:
//                        replaceRoomFragment(new LivingRoomFragment());
//                        break;
//                    case R.id.iot_out_door:
//                        replaceRoomFragment(new OutdoorFragment());
//                        break;
//                    case R.id.iot_bed_room:
//                        replaceRoomFragment(new BedRoomFragment());
//                        break;
//                    case R.id.iot_bath_room:
//                        replaceRoomFragment(new BathRoomFragment());
//                }
//            }
//        });
//    }

//    private void replaceRoomFragment(Fragment fragment){
//        // load room fragment
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.linear_iot_room, fragment);
//    }

    private void replaceFragment(Fragment fragment) {
        // load fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.relative_bottom_nav, fragment);
        fragmentTransaction.commit();
    }
}