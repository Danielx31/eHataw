package com.example.ehataw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText email = findViewById(R.id.EmailRegister);
        final EditText password = findViewById(R.id.confirmPasswordRegister);
        final Button loginBtn = findViewById(R.id.btnRegister);
        final TextView registerNowBtn = findViewById(R.id.logInNowBtn);
        final TextView forgotPasswordBtn = findViewById(R.id.forgotPassBtn);
        auth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailTxt = email.getText().toString();
                final String passwordTxt = password.getText().toString();

                if(TextUtils.isEmpty(emailTxt)){
                    email.setError("Email cannot be empty");
                    email.requestFocus();
                }else if(TextUtils.isEmpty(passwordTxt)){
                    password.setError("Password cannot be empty");
                    password.requestFocus();
                }
                else{
                    logIn(emailTxt, passwordTxt);
                }
            }
        });

        registerNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //open Register activity
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });
    }

    private void logIn(String emailTxt, String password){
        auth.signInWithEmailAndPassword(emailTxt, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Login.this, "logged In", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, Home.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(Login.this, "Log In Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}