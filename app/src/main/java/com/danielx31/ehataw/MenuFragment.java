package com.danielx31.ehataw;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuFragment extends Fragment {

    private TextView textViewWelcome;
    private String fullName, name;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_menu, container, false);

        textViewWelcome = root.findViewById(R.id.textview_name);
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if(firebaseUser == null){
            Toast.makeText(getContext(), "Something went wrong! user's details are not available at the moment", Toast.LENGTH_SHORT).show();
        }
        else{
            checkingifEmailVerified(firebaseUser);
            showUserProfile(firebaseUser);
        }

        return root;

//        return inflater.inflate(R.layout.fragment_menu, container, false);
    }


    //Users coming to Menu after successful registration
    private void checkingifEmailVerified(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        //Setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You can not login without email verification next time");

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

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //Extracting User Reference from database for "Registered Users"
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                fullName = firebaseUser.getDisplayName();
                textViewWelcome.setText("Welcome, " + fullName + "!");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);



//        name = getActivity().findViewById(R.id.textview_name);
//        name.findViewById(R.id.textview_name);
//        auth = FirebaseAuth.getInstance();
//        FirebaseUser firebaseUser = auth.getCurrentUser();
//        if(firebaseUser == null){
//            Toast.makeText(getContext(), "Something went wrong! user's details are not available at the moment", Toast.LENGTH_SHORT).show();
//            Toast.makeText(getContext(),"Nice", Toast.LENGTH_LONG).show();
//
//        }else{
//            showUserProfile(firebaseUser);
//        }
    }

//    private void showUserProfile(FirebaseUser firebaseUser) {
//        String userID = firebaseUser.getUid();
//
//        //Extracting User Reference from database for "Registered Users"
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
//        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ReadWriteUserDetails readWriteUserDatails = snapshot.getValue(ReadWriteUserDetails.class);
//                if (readWriteUserDatails != null){
//                    fullName = firebaseUser.getDisplayName();
//
//                    name.setText("Welcome, " + fullName + "!");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}
