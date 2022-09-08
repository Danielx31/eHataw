package com.danielx31.ehataw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.danielx31.ehataw.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    private FirebaseAuth auth;
    ActivityMainBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText fullName =  findViewById(R.id.fullNameRegister);
        final EditText email = findViewById(R.id.edittext_email1);
        final EditText password =  findViewById(R.id.passwordRegister);
        final EditText conPassword = findViewById(R.id.edittext_password1);

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
                if(fullNameTxt.isEmpty() || emailTxt.isEmpty() || passwordTxt.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
                else if(passwordTxt.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password Must be 6 or More Characters", Toast.LENGTH_SHORT).show();
                }
                //Check if passwords are matching with each other
                //If not matching with each other then show toast message
                else if(!passwordTxt.equals(conPasswordTxt)){
                    Toast.makeText(RegisterActivity.this, "Password are not matching", Toast.LENGTH_SHORT).show();
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

                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("email", emailTxt);
                    hashMap.put("name",fullNameTxt);
                    hashMap.put("password",passwordTxt);

                    databaseReference.child("Users")
                            .child(fullNameTxt)
                            .setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }else{
                    Toast.makeText(RegisterActivity.this, "Error..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}