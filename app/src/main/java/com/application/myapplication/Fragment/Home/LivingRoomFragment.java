package com.application.myapplication.Fragment.Home;

import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.myapplication.R;


public class LivingRoomFragment extends Fragment {
    private SwitchCompat buttonLedLivingRoom;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_living_room, container, false);

        return view;
    }
}