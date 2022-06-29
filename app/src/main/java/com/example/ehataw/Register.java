package com.example.ehataw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    FirebaseFirestore firestore;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText fullName =  findViewById(R.id.fullNameRegister);
        final EditText email = findViewById(R.id.EmailRegister);
        final EditText password =  findViewById(R.id.passwordRegister);
        final EditText conPassword = findViewById(R.id.confirmPasswordRegister);

        final Button registerBtn = findViewById(R.id.btnRegister);
        final TextView loginNowBtn =  findViewById(R.id.logInNowBtn);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


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
                    Toast.makeText(Register.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
                else if(passwordTxt.length() < 6){
                    Toast.makeText(Register.this, "Password Must be 6 or More Characters", Toast.LENGTH_SHORT).show();
                }
                //Check if passwords are matching with each other
                //If not matching with each other then show toast message
                else if(!passwordTxt.equals(conPasswordTxt)){
                    Toast.makeText(Register.this, "Password are not matching", Toast.LENGTH_SHORT).show();
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

        auth.createUserWithEmailAndPassword(emailTxt, passwordTxt).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Map<String, Object> user = new HashMap<>();
                    user.put("Email", emailTxt);
                    user.put("Complete Name", fullNameTxt);
                    user.put("Password", passwordTxt);

                    firestore.collection("users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(Register.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Register.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    Toast.makeText(Register.this, "Error..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}