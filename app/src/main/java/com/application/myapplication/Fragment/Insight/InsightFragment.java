package com.application.myapplication.Fragment.Insight;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.application.myapplication.R;

public class InsightFragment extends Fragment {

    private LinearLayout container;
    private Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_insight, container, false);
//        spinner = view.findViewById(R.id.spinner);


        String[] items = new String[]{"1. Temperature Chart", "2. Humidity Chart"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner, items);

        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.filled_exposed);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(), autoCompleteTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                String selectedItem = (String) adapterView.getItemAtPosition(i);
                Toast.makeText(getContext(), selectedItem.toString(), Toast.LENGTH_SHORT).show();
                Log.d("ITEM", selectedItem.toString());
                if (selectedItem.equals("1. Temperature Chart")) {
                    Fragment fragment = new TempChartFragment();
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.container_insight, fragment)
                            .commit();
                }
            }

        });


        return view;
    }

}