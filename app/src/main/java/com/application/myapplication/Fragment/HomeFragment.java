package com.application.myapplication.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.myapplication.R;

import org.w3c.dom.Text;

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
//        TextView livingRoom = getView().findViewById(R.id.iot_living_room);
//        TextView outDoor = getView().findViewById(R.id.iot_out_door);
//        TextView bedRoom = getView().findViewById(R.id.iot_bed_room);
//        TextView bathRoom = getView().findViewById(R.id.iot_bath_room);
        
//        switch (1) {
//            case R.id.iot_living_room:
//                onClickFragment(new LivingRoomFragment());
//                break;
//            case R.id.iot_out_door:
//                onClickFragment(new OutDoorFragment());
//                break;
//            case R.id.iot_bed_room:
//                onClickFragment(new BedRoomFragment());
//                break;
//            case R.id.iot_bath_room:
//                onClickFragment(new BathRoomFragment());
//                break;
//        }


//        livingRoom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onClickFragment(new LivingRoomFragment());
//            }
//        });
//        outDoor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onClickFragment(new OutDoorFragment());
//            }
//        });
//        bedRoom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onClickFragment(new BedRoomFragment());
//            }
//        });
//        bathRoom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onClickFragment(new BathRoomFragment());
//            }
//        });

       btnLamp = getView().findViewById(R.id.btn_lamp);
       btnLamp.setOnClickListener(view1 -> {
           flag++;
           if(flag % 2 == 0){
               Log.d("btn", "off" );
               // Xử lí sự kiện khi tắt nút
           }
           else {
               Log.d("btn", "on" );
               // Xử lí sự kiện khi mở nút

           }
       });

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