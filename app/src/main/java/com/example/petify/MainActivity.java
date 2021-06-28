package com.example.petify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;

    private TextView register, forgotPassword;
    private EditText editTextEmail, editTextPassword;
    private Button buttonSignIn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        register = findViewById(R.id.register_textView);
        register.setOnClickListener(this);

        buttonSignIn = findViewById(R.id.login_btn);
        buttonSignIn.setOnClickListener(this);

        editTextEmail = findViewById(R.id.email_editText);
        editTextPassword = findViewById(R.id.password_editText);
        progressBar = findViewById(R.id.progress_bar);

        forgotPassword = findViewById(R.id.forgot_password_textView);
        forgotPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_textView:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.login_btn:
                userLogIn();
                break;
            case R.id.forgot_password_textView:
                startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
                break;
        }
    }

    private void userLogIn() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("email is required");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("enter valid email");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("password is required");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("minimum length should be 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //redirect to user profile
                    progressBar.setVisibility(View.GONE);
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (firebaseUser.isEmailVerified()) {
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    } else {
                        firebaseUser.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check your email to verify email address", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Failed to sign in! Check your crendentials!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}