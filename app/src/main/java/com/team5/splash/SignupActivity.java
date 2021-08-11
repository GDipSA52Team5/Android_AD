package com.team5.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private TextView textViewReturnLogin, registerUser;
    private EditText editTextName, editTextEmail, editTextPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        registerUser = (Button) findViewById(R.id.registerBtn);
        registerUser.setOnClickListener(this);

        textViewReturnLogin = (TextView) findViewById(R.id.textRegister);

        editTextEmail = (EditText) findViewById(R.id.editTextRegisterEmailAddress);
        editTextName = (EditText) findViewById(R.id.editTextRegisterName);
        editTextPassword = (EditText) findViewById(R.id.editTextRegisterPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBarRegister);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textViewReturnLogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.registerBtn:
                registerUser();
                break;
        }

    }

    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
    }
}