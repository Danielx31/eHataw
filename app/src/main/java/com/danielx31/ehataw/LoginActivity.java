package com.danielx31.ehataw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    private TextInputEditText emailTextInputEditText;
    private TextInputEditText passwordTextInputEditText;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private Button loginButton;
    private TextView registerTextView;
    private TextView forgotPasswordTextView;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        emailTextInputEditText = findViewById(R.id.textinputedittext_email);
        passwordTextInputEditText = findViewById(R.id.textinputedittext_password);
        emailTextInputLayout = findViewById(R.id.textinputlayout_email);
        passwordTextInputLayout = findViewById(R.id.textinputlayout_password);
        loginButton = findViewById(R.id.button_login);
        registerTextView = findViewById(R.id.button_Login);
        forgotPasswordTextView = findViewById(R.id.button_forgetpass);
        auth = FirebaseAuth.getInstance();

        emailTextInputLayout.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.arbutus_regular));
        passwordTextInputLayout.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.arbutus_regular));

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailTxt = emailTextInputEditText.getText().toString();
                final String passwordTxt = passwordTextInputEditText.getText().toString();

                if(TextUtils.isEmpty(emailTxt)){
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    emailTextInputEditText.setError("Email is required");
                    emailTextInputEditText.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()){
                    Toast.makeText(LoginActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    emailTextInputEditText.setError("Valid email is required");
                    emailTextInputEditText.requestFocus();
                }else if(TextUtils.isEmpty(passwordTxt)){
                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    passwordTextInputEditText.setError("Password is required");
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


                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    //Check if email is verified before user can access their profile
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(getApplicationContext(), "You are logged in now", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        //open User Profile
                    }else{
                        firebaseUser.sendEmailVerification();
                        auth.signOut(); //Sign Out user
                        showAlertDialog();
                    }

                }else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        emailTextInputEditText.setError("User does not exists or is no longer valid. Please register again");
                        emailTextInputEditText.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        emailTextInputEditText.setError("Invalid credentials. Kindly, check and re-enter.");
                        emailTextInputEditText.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    private void showAlertDialog() {
        //Setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You can not login without email verification");

        //Open Email Apps if User clicks/taps Continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //To email app in new window and not within our app
                startActivity(intent);
            }
        });
        //create the AlertDialog
        AlertDialog alertDialog = builder.create();

        //show the AlertDialog
        alertDialog.show();
    }

    //Check if User is already Logged in. In such case, straightway take the user to the user's profile
    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser() != null){
            Toast.makeText(LoginActivity.this, "Already Logged In", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
            //Start the UserProfileActivity
        }else{
            Toast.makeText(LoginActivity.this, "You can login now!", Toast.LENGTH_SHORT).show();
        }
    }
}