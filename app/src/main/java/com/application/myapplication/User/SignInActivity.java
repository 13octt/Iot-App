package com.application.myapplication.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.application.myapplication.R;

public class SignInActivity extends AppCompatActivity {

    private LinearLayout layoutSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initUI();
        initListener();

    }

    private void initUI(){
        layoutSignUp = findViewById(R.id.layout_sign_up);
    }

    private void initListener(){
        layoutSignUp.setOnClickListener(view -> {
            Intent signUp = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(signUp);
        });
    }
}