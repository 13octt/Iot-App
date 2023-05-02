package com.application.myapplication.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.application.myapplication.R;

public class HomeFragment extends Fragment {

    LinearLayout roomLinearLayout;
    private int flag = 0;
    Button btnLamp;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();

        btnLamp.setOnClickListener(view1 -> {
            flag++;
            if (flag % 2 == 0) {
                Log.d("btn", "off");
                // Xử lí sự kiện khi tắt nút
            } else {
                Log.d("btn", "on");
                // Xử lí sự kiện khi mở nút

            }
        });

    }

    private void initUI() {
        btnLamp = getView().findViewById(R.id.btn_lamp);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void onClickFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.linear_iot_room, fragment);

    }

}