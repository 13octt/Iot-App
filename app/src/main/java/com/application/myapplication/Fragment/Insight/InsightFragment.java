package com.application.myapplication.Fragment.Insight;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.application.myapplication.R;

public class InsightFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insight, container, false);

        // Drop Down Menu
        String[] items = new String[]{"1. Temperature Chart", "2. Humidity Chart"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner, items);
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.filled_exposed);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(), autoCompleteTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                String selectedItem = (String) adapterView.getItemAtPosition(i);
                if (selectedItem.equals("1. Temperature Chart")) {
                    switchFragment(new TempChartFragment());
                } else if (selectedItem.equals("2. Humidity Chart")) {
                    switchFragment(new HumidityChartFragment());
                }
            }
        });

        Fragment tempFragment = new TempChartFragment();
        switchFragment(tempFragment);

        return view;
    }



    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_insight, fragment);
        fragmentTransaction.commit();
    }

}