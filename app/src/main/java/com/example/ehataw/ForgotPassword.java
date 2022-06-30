package com.example.ehataw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPassword extends AppCompatActivity {

    private Button forgetBtn, loginBtn;
    private EditText emailTxt;
    private String email;

    private FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        forgetBtn = findViewById(R.id.linkSendBtn);
        loginBtn = findViewById(R.id.logInForgotPassBtn);
        emailTxt = findViewById(R.id.emailForgotPass);

        forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData() {
        email = emailTxt.getText().toString();

        if(email.isEmpty()){
            emailTxt.setError("Required");
        }else{
            forgetPass();
        }
    }

    private void forgetPass() {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this, "Check your Email", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotPassword.this, Login.class));
                    finish();
                }else{
                    Toast.makeText(ForgotPassword.this, "Error "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}