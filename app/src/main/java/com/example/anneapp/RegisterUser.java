package com.example.anneapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private TextView banner,registerUser;
    private EditText editTextFullName,editTextAge,editTextEmail,editTextPassword;
    private ProgressBar ProgressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        TextView banner = findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser=findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextFullName=findViewById(R.id.fullName);
        editTextAge=findViewById(R.id.age);
        editTextEmail=findViewById(R.id.email);
        editTextPassword=findViewById(R.id.password);

        ProgressBar=findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.banner:
                startActivity(new Intent(this,MainActivity.class));
                break;

            case R.id.registerUser:
                registerUser();
                break;
        }

    }

    private void registerUser() {

        String email=editTextEmail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();
        String fullName=editTextFullName.getText().toString().trim();
        String age=editTextAge.getText().toString().trim();


        if (fullName.isEmpty()){
            editTextFullName.setError("Enter FullName");
            editTextFullName.requestFocus();
            return;
        }

        if (age.isEmpty()){
            editTextAge.setError("Enter age");
            editTextAge.requestFocus();
            return;
        }

        if (email.isEmpty()){
            editTextEmail.setError("Enter email");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please Provide Valid Email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            editTextPassword.setError("Enter password");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length()<6){
            editTextPassword.setError("Min password length should be 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        ProgressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            User user=new User(fullName,age,email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterUser.this, "User has been registered Successfully", Toast.LENGTH_SHORT).show();
                                        ProgressBar.setVisibility(View.GONE);

                                    }
                                    else {
                                        Toast.makeText(RegisterUser.this, "Failed to register Try Again!", Toast.LENGTH_SHORT).show();
                                        ProgressBar.setVisibility(View.GONE);
                                    }
                                }
                            });

                            
                        }else {
                            Toast.makeText(RegisterUser.this, "Failed to register", Toast.LENGTH_SHORT).show();
                            ProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}