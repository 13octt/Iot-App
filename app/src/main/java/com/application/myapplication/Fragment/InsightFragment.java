package com.application.myapplication.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.application.myapplication.R;

import java.util.Arrays;
import java.util.List;

public class InsightFragment extends Fragment {

    private LinearLayout container;
    private Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_insight, container, false);
        spinner = view.findViewById(R.id.spinner);


        String[] items = {"Temperature Chart", "Humidity Chart"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner, items);
        spinner.setAdapter(adapter);

//        spinner.setOnClickListener(v -> {
//            // Tạo PopupWindow
//            PopupWindow popupWindow = new PopupWindow(getContext());
//
//            // Tạo danh sách phần tử
//            List<String> items1 = Arrays.asList("Item 1", "Item 2", "Item 3");
//
//            // Tạo ArrayAdapter
//            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(), R.layout.item_spinner, items1);
//
//            // Tạo ListView và thiết lập Adapter cho nó
//            ListView listView = new ListView(getContext());
//            listView.setAdapter(adapter1);
//
//            // Thiết lập PopupWindow với ListView
//            popupWindow.setContentView(listView);
//            popupWindow.setWidth(spinner.getWidth());
//            popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//            popupWindow.setFocusable(true);
//
//            // Hiển thị PopupWindow phía dưới Spinner
//            popupWindow.showAsDropDown(spinner);
//        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);

                if (selectedItem.equals("Tempearature Chart")) {
                    Log.d("SPINNER", "item temp");
//                    Fragment fragment = new Fragment1();
//                    FragmentTransaction transaction = requireFragmentManager().beginTransaction();
//                    transaction.replace(R.id.fragment_container, fragment);
//                    transaction.commit();
                } else if (selectedItem.equals("Humidity Chart")) {
                    Log.d("SPINNER", "item humi");

//                    Fragment fragment = new Fragment2();
//                    FragmentTransaction transaction = requireFragmentManager().beginTransaction();
//                    transaction.replace(R.id.fragment_container, fragment);
//                    transaction.commit();
                }

                // Xử lý logic khi người dùng chọn một phần tử trong Spinner và chuyển sang Fragment khác
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Xử lý logic khi không có phần tử nào được chọn
            }
        });


        return view;
    }

}