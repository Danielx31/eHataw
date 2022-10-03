package com.danielx31.ehataw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.danielx31.ehataw.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    private FirebaseAuth auth;
    ActivityMainBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private EditText fullName, email, password, conPassword;
    private static final String TAG = "RegisterActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullName =  findViewById(R.id.fullNameRegister);
        email = findViewById(R.id.edittext_email1);
        password =  findViewById(R.id.passwordRegister);
        conPassword = findViewById(R.id.edittext_password1);

        final Button registerBtn = findViewById(R.id.button_Register);
        final TextView loginNowBtn =  findViewById(R.id.button_Login);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get Data from Edit Text into String Variables
                final String fullNameTxt =  fullName.getText().toString();
                final String emailTxt = email.getText().toString();
                final String passwordTxt = password.getText().toString();
                final String conPasswordTxt = conPassword.getText().toString();

                //Check if user fill all the fields before sending data to firebase
                if(TextUtils.isEmpty(emailTxt)){
                    Toast.makeText(RegisterActivity.this, "Please enter your Email", Toast.LENGTH_SHORT).show();
                    email.setError("Email is required");
                    email.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()){
                    Toast.makeText(RegisterActivity.this, "Please re-enter your Email", Toast.LENGTH_SHORT).show();
                    email.setError("Valid Email is required");
                    email.requestFocus();
                }
                else if(TextUtils.isEmpty(fullNameTxt)){
                    Toast.makeText(RegisterActivity.this, "Please Enter your fullname", Toast.LENGTH_SHORT).show();
                    fullName.setError("Full Name is required");
                    fullName.requestFocus();
                }
                else if(TextUtils.isEmpty(passwordTxt)){
                    Toast.makeText(RegisterActivity.this, "Please enter your Password", Toast.LENGTH_SHORT).show();
                    password.setError("Password is required");
                    password.requestFocus();
                }
                else if(passwordTxt.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password should be at least 6 digits", Toast.LENGTH_SHORT).show();
                    password.setError("Password too weak");
                    password.requestFocus();
                }
                else if(TextUtils.isEmpty(conPasswordTxt)){
                    Toast.makeText(RegisterActivity.this, "Please enter your Confirm Password", Toast.LENGTH_SHORT).show();
                    conPassword.setError("Password Confirmation is required");
                    conPassword.requestFocus();
                }
                //Check if passwords are matching with each other
                //If not matching with each other then show toast message
                else if(!passwordTxt.equals(conPasswordTxt)){
                    Toast.makeText(RegisterActivity.this, "Password are not matching", Toast.LENGTH_SHORT).show();
                    conPassword.setError("Password Confirmation is required");
                    conPassword.requestFocus();
                    password.clearComposingText();
                    conPassword.clearComposingText();
                }
                else{
                    registerUser(fullNameTxt, emailTxt, passwordTxt);

                }

            }


        });


        loginNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void registerUser(String fullNameTxt, String emailTxt, String passwordTxt){

        auth.createUserWithEmailAndPassword(emailTxt, passwordTxt).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    //Update Display Name of User
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(fullNameTxt).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    //Enter User Data into the Firebase Realtime Database
                    ReadWriteUserDetails writeUserDatails = new ReadWriteUserDetails(fullNameTxt);

                    //Extracting User reference from database for "Register Users"
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDatails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()) {

                                //send verification email
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(RegisterActivity.this, "Registered Successfully. Please verify your email", Toast.LENGTH_SHORT).show();

                             //Open User Profile after successful registration
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    //To prevent user from returning back to register activity on pressing back button after registration
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                    startActivity(intent);
                                    finish(); //to close Register activity
                            }else{
                                Toast.makeText(RegisterActivity.this, "Registered Failed. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



//

                }else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        password.setError("Your Password is too weak. Kindly use a mix of alphabetical, numbers and special characters");
                        password.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        password.setError("Your email is invalid or already use. Kindly re-enter.");
                        password.requestFocus();
                    }catch (FirebaseAuthUserCollisionException e){
                        password.setError("User is already registered with this email. Use another email.");
                        password.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
//                    Toast.makeText(RegisterActivity.this, "Error..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}