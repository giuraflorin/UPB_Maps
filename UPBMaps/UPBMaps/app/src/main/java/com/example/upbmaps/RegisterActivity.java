package com.example.upbmaps;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {
    private EditText fullNameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private RadioGroup statusRadioGroup;
    private Spinner facultySpinner;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        fullNameEditText = findViewById(R.id.editText_register_full_name);
        emailEditText = findViewById(R.id.editText_register_email);
        phoneEditText = findViewById(R.id.editText_register_phone);
        passwordEditText = findViewById(R.id.editText_register_password);
        confirmPasswordEditText = findViewById(R.id.editText_register_confirm_password);
        statusRadioGroup = findViewById(R.id.radio_group_register_status);
        facultySpinner = findViewById(R.id.spinner_faculty);
        registerButton = findViewById(R.id.button_register);
        progressBar = findViewById(R.id.processBar);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String fullName = fullNameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String selectedStatus = ((RadioButton) findViewById(statusRadioGroup.getCheckedRadioButtonId())).getText().toString();
        final String selectedFaculty = facultySpinner.getSelectedItem().toString();

        if (selectedStatus.equals("Administration")) {
            // Check if the password contains the required word
            if (!password.contains("profesorupb")) {
                Toast.makeText(RegisterActivity.this, "You can't be a Professor/Administrative Staff", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (TextUtils.isEmpty(selectedFaculty) || selectedFaculty.equals("Select Faculty")) {
            Toast.makeText(RegisterActivity.this, "Please select a faculty", Toast.LENGTH_SHORT).show();
            return;
        }


        // Validate inputs
        if (TextUtils.isEmpty(fullName)) {
            fullNameEditText.setError("Please enter your full name");
            fullNameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Please enter your email");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            emailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneEditText.setError("Please enter your phone number");
            phoneEditText.requestFocus();
            return;
        }

        if (!phone.matches("^(07|03|02)[2-9]\\d{7}$")) {
            phoneEditText.setError("Please enter a valid phone number");
            phoneEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Please enter a password");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password should be at least 6 characters long");
            passwordEditText.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            confirmPasswordEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User registration successful
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                currentUser.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Email verification sent
                                                    Toast.makeText(RegisterActivity.this, "Email verification sent to " + email,
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Failed to send email verification
                                                    Toast.makeText(RegisterActivity.this, "Failed to send email verification",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                String userId = currentUser.getUid();

                                // Save additional user details to Firebase Database
                                User user = new User(userId, fullName, email, selectedStatus, selectedFaculty, phone);
                                mDatabase.child("users").child(userId).setValue(user);

                                // Update user's display name
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(fullName)
                                        .build();
                                currentUser.updateProfile(profileUpdates);

                                // Redirect to appropriate activity based on status
                                if (selectedStatus.equals("Student")) {
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                } else if (selectedStatus.equals("Professor")) {
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                }

                                finish();
                            }
                        } else {
                            // User registration failed
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(RegisterActivity.this, "Email address is already in use",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        // Hide progress bar
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}