package com.example.upbmaps;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private TextView textViewGreeting;
    private TextView textViewEmail;
    private BottomNavigationView bottomNavigationView;

    private EditText editTextNewEmail;
    private TextView textViewFaculty;
    private TextView textViewStatus;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textViewFaculty = findViewById(R.id.textViewFaculty);
        textViewGreeting = findViewById(R.id.textViewGreeting);
        textViewEmail = findViewById(R.id.textViewEmail);
        editTextNewEmail = findViewById(R.id.editTextNewEmail);
        textViewStatus = findViewById(R.id.textViewStatus);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            textViewGreeting.setText("Hello, " + displayName + "!");
            textViewEmail.setText("Email: " + email);

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            userRef.child("faculty").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String faculty = dataSnapshot.getValue(String.class);
                        textViewFaculty.setText("Faculty: " + faculty);
                    } else {
                        textViewFaculty.setText("Faculty: Not specified");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
            DatabaseReference userRef2 = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            userRef.child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String status = dataSnapshot.getValue(String.class);
                        textViewStatus.setText("Role: " + status);
                    } else {
                        textViewStatus.setText("Role: Not specified");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }

        Button buttonChangePassword = findViewById(R.id.buttonChangePassword);
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(currentUser.getEmail())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(HomeActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(HomeActivity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        Button buttonChangeEmail = findViewById(R.id.buttonChangeEmail);
        buttonChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String newEmail = editTextNewEmail.getText().toString().trim();
                    currentUser.updateEmail(newEmail)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    currentUser.sendEmailVerification()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(HomeActivity.this, "Email update requested. Please check your new email for verification instructions.", Toast.LENGTH_LONG).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(HomeActivity.this, "Failed to send email verification", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    textViewEmail.setText("Email: " + newEmail);
                                    Toast.makeText(HomeActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(HomeActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        Button buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent homeIntent = new Intent(HomeActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    return true;
                } else if (itemId == R.id.nav_maps) {
                    Intent homeIntent = new Intent(HomeActivity.this, MapsActivity.class);
                    startActivity(homeIntent);
                    return true;
                } else if (itemId == R.id.nav_class) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                        usersRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String status = dataSnapshot.child("status").getValue(String.class);
                                    if (status != null) {
                                        if (status.equals("Student")) {
                                            Intent classesIntent = new Intent(HomeActivity.this, ClassesActivity.class);
                                            startActivity(classesIntent);
                                        } else if (status.equals("Visitor")) {
                                            Intent visitorIntent = new Intent(HomeActivity.this, VisitorActivity.class);
                                            startActivity(visitorIntent);
                                        } else if (status.equals("Administration")) {
                                            Intent professorIntent = new Intent(HomeActivity.this, ProfessorActivity.class);
                                            startActivity(professorIntent);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle error
                            }
                        });
                    }
                    return true;
                }
                else if (itemId == R.id.nav_heat_map) {
                    Intent homeIntent = new Intent(HomeActivity.this, HeatMapActivity.class);
                    startActivity(homeIntent);
                    return true;
                }
                return false;
            }
        });
    }

}
