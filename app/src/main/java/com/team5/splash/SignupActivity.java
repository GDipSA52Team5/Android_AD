package com.team5.splash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


import org.json.JSONObject;


public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private RequestQueue mQueue;

    private FirebaseAuth mAuth;

    private TextView textViewReturnLogin, registerUser;
    private EditText editTextName, editTextEmail, editTextPassword;
    private ProgressBar progressBarRegister;

    private String userEmail, dbSourceLocal, dbSourceHeroku;

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

        progressBarRegister = (ProgressBar) findViewById(R.id.progressBarRegister);

        mQueue = Volley.newRequestQueue(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

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

        if(name.isEmpty()){
            editTextName.setError("Name is required!");
            editTextName.requestFocus();
            return;
        }
        if(email.isEmpty()){
            editTextEmail.setError("Email is Required!");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide valid email!");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            editTextPassword.setError("Password should be at least 6 characters long!");
            editTextPassword.requestFocus();
        }

        progressBarRegister.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
//                            User user = new User(name, email);
//                            FirebaseDatabase.getInstance().getReference("User")
//                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if
//                                    (task.isSuccessful()){

                            progressBarRegister.setVisibility(View.GONE);

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build();

                            user.updateProfile(profileUpdates);

                            userEmail  = user.getEmail();
//                            userName = user.getDisplayName();
//                            userUID = user.getUid();
                            dbSourceHeroku = "https://gdipsa-ad-springboot.herokuapp.com/api/";
                            dbSourceLocal = "http://localhost:8080/api/";

                            String url = dbSourceHeroku + "saveUser/" + userEmail;

                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    return;
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            });
                            mQueue.add(request);

                            Toast.makeText(SignupActivity.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));

                        } else {
                            Toast.makeText(SignupActivity.this, "Failed to register! Please Try Again!", Toast.LENGTH_LONG).show();
                            progressBarRegister.setVisibility(View.GONE);
                        }

                    }

                });


    }
}