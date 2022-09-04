package com.danielx31.ehataw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    private TextInputEditText emailTextInputEditText;
    private TextInputEditText passwordTextInputEditText;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private Button loginButton;
    private TextView registerTextView;
    private TextView forgotPasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailTextInputEditText = findViewById(R.id.textinputedittext_email);
        passwordTextInputEditText = findViewById(R.id.textinputedittext_password);
        emailTextInputLayout = findViewById(R.id.textinputlayout_email);
        passwordTextInputLayout = findViewById(R.id.textinputlayout_password);
        loginButton = findViewById(R.id.button_login);
        registerTextView = findViewById(R.id.button_register);
        forgotPasswordTextView = findViewById(R.id.button_forgetpass);
        auth = FirebaseAuth.getInstance();

        emailTextInputLayout.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.arbutus_regular));
        passwordTextInputLayout.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.arbutus_regular));

        startActivity(new Intent(getApplicationContext(), HomeActivity.class));

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailTxt = emailTextInputEditText.getText().toString();
                final String passwordTxt = passwordTextInputEditText.getText().toString();

                if(TextUtils.isEmpty(emailTxt)){
                    emailTextInputEditText.setError("Email cannot be empty");
                    emailTextInputEditText.requestFocus();
                }else if(TextUtils.isEmpty(passwordTxt)){
                    passwordTextInputEditText.setError("Password cannot be empty");
                    passwordTextInputEditText.requestFocus();
                }
                else{
                    logIn(emailTxt, passwordTxt);
                }
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //open RegisterActivity activity
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
            }
        });
    }

    private void logIn(String emailTxt, String password){
        auth.signInWithEmailAndPassword(emailTxt, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "logged In", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "Log In Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}