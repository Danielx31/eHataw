package com.danielx31.ehataw;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class ChangePasswordActivity extends Fragment {

    private BroadcastReceiver connectionReceiver;

    private FirebaseAuth auth;
    private EditText editTextPwdCurr, editTextPwdNew, editTextPwdCurrNew;
    private TextView textViewAuthenticated;
    private Button buttonChangePwd, buttonReAuthenticated;
    private String userPwdCurr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_change_password, container, false);
        RxJavaPlugins.setErrorHandler(e -> { });

        connectionReceiver = new ConnectionReceiver();

        editTextPwdCurr = view.findViewById(R.id.editText_delete_user_current);
        editTextPwdNew = view.findViewById(R.id.editText_change_pwd_new);
        editTextPwdCurrNew = view.findViewById(R.id.editText_change_pwd_new_confirm);
        textViewAuthenticated = view.findViewById(R.id.textView_delete_user_authenticated);
        buttonChangePwd = view.findViewById(R.id.button_delete_user);
        buttonReAuthenticated = view.findViewById(R.id.button_authenticate_delete_user);

        //Disable edit text for new password, confirm new password and make change pwd button Unclickable till user is authenticated
        editTextPwdNew.setEnabled(false);
        editTextPwdCurrNew.setEnabled(false);
        buttonChangePwd.setEnabled(false);

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if(firebaseUser.equals("")){
            Toast.makeText(getActivity(), "Something went wrong! User's details not available", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), MenuFragment.class);
            startActivity(intent);
            getActivity().finish();
        }else {
            reAuthenticated(firebaseUser);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(connectionReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(connectionReceiver);
    }

    //ReAuthenticated User before changing password
    private void reAuthenticated(FirebaseUser firebaseUser) {
        buttonReAuthenticated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPwdCurr = editTextPwdCurr.getText().toString();

                if(TextUtils.isEmpty(userPwdCurr)){
                    Toast.makeText(getActivity(), "Password is needed", Toast.LENGTH_SHORT).show();
                    editTextPwdCurr.setError("Please enter your current password to authenticate");
                    editTextPwdCurr.requestFocus();
                }else{
                    //ReAutheticate user now
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurr);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //Disable editText for current password. Enable EditText for new password and confirm new password
                                editTextPwdCurr.setEnabled(false);
                                editTextPwdNew.setEnabled(true);
                                editTextPwdCurrNew.setEnabled(true);

                                //Enable Change Pwd button. Disable Authenticate button
                                buttonReAuthenticated.setEnabled(false);
                                buttonChangePwd.setEnabled(true);

                                //Set Textview to show user is authenticated/verified
                                textViewAuthenticated.setText("You are authenticated." + "You can change password now!");
                                Toast.makeText(getActivity(), "You are authenticated." + "You can change password now!", Toast.LENGTH_SHORT).show();
                                buttonChangePwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changePwd(firebaseUser);
                                    }
                                });
                            }else {
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = editTextPwdNew.getText().toString();
        String userPwdConfirmNew = editTextPwdCurrNew.getText().toString();

        if (TextUtils.isEmpty(userPwdNew)){
            Toast.makeText(getActivity(), "New Password is needed", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please enter your new password");
            editTextPwdNew.requestFocus();
        }else if(TextUtils.isEmpty(userPwdConfirmNew)){
            Toast.makeText(getActivity(), "Please Confirm your new Password is needed", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please re-enter your new password");
            editTextPwdNew.requestFocus();
        }else if(!userPwdNew.matches(userPwdConfirmNew)){
            Toast.makeText(getActivity(), "Password did not match", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please re-enter same password");
            editTextPwdNew.requestFocus();

        }else if(!userPwdConfirmNew.matches(userPwdNew)){
            Toast.makeText(getActivity(), "New Password cannot be same as old password", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please enter your new password");
            editTextPwdNew.requestFocus();
        }else{
            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getActivity(), "Password has been changed", Toast.LENGTH_SHORT).show();
                        Fragment menuFrag = new MenuFragment();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.container_fragment, menuFrag).commit();
                    }else {
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

    }



}