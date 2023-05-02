package com.application.myapplication.User;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.application.myapplication.MainActivity;
import com.application.myapplication.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    private LinearLayout layoutSignUp;
    private EditText email, password;
    private Button btnSignIn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initUI();
        signIn();

        // Don't have an account
        signUp();

    }

    private void initUI() {
        layoutSignUp = findViewById(R.id.layout_sign_up);
        email = findViewById(R.id.sign_in_email);
        password = findViewById(R.id.sign_in_pass);
        btnSignIn = findViewById(R.id.btn_sign_in);
    }

    private void signUp() {
        layoutSignUp.setOnClickListener(view -> {
            Intent signUp = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(signUp);
        });
    }

    private void signIn() {
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        btnSignIn.setOnClickListener(view -> {
            String email_text = email.getText().toString().trim();
            String pass_text = password.getText().toString().trim();
            if (TextUtils.isEmpty(email_text) || TextUtils.isEmpty(pass_text)) {
                Toast.makeText(SignInActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email_text, pass_text)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Intent main = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(main);
                        } else {
                            Toast.makeText(SignInActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        });
    }
}