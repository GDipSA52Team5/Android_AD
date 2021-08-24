package com.team5.HawkeRise.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.team5.HawkeRise.R;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Button forgetPassword;
    private EditText editTextPasswordEmail;
    private TextView backToLogin;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_forget_password);


        forgetPassword = (Button) findViewById(R.id.buttonRequestPassword);
        forgetPassword.setOnClickListener(this);

        editTextPasswordEmail = (EditText) findViewById(R.id.editTextResetPasswordEmail);

        backToLogin = (TextView) findViewById(R.id.textViewBackToLogin);

        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRequestPassword:
                resetPassword();
                break;

            case R.id.textViewBackToLogin:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    private void resetPassword(){
        String email = editTextPasswordEmail.getText().toString().trim();

        if(email.isEmpty()){
            editTextPasswordEmail.setError("Email is required!");
            editTextPasswordEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextPasswordEmail.setError("Please provide a valid email!");
            editTextPasswordEmail.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgetPasswordActivity.this, "Check your Email to reset your password!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, "Email address not found, please sign up for an account!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}