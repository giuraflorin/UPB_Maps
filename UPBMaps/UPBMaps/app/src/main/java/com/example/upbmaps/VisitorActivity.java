package com.example.upbmaps;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VisitorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);

        TextView messageTextView = findViewById(R.id.textView_visitor_message);
        messageTextView.setText("You don't have classes because you are a visitor. Enjoy!");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_class);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent homeIntent = new Intent(VisitorActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    return true;
                } else if (itemId == R.id.nav_maps) {
                    Intent homeIntent = new Intent(VisitorActivity.this, MapsActivity.class);
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
                                    if (status != null && status.equals("Administration")) {
                                        Intent professorIntent = new Intent(VisitorActivity.this, ProfessorActivity.class);
                                        startActivity(professorIntent);
                                    } else if (status != null && status.equals("Student")) {
                                        Intent classesIntent = new Intent(VisitorActivity.this, ClassesActivity.class);
                                        startActivity(classesIntent);
                                    }
                                    else if (status != null && status.equals("Visitor")) {
                                        Intent classesIntent = new Intent(VisitorActivity.this, VisitorActivity.class);
                                        startActivity(classesIntent);
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
                    Intent homeIntent = new Intent(VisitorActivity.this, HeatMapActivity.class);
                    startActivity(homeIntent);
                    return true;
                }
                return false;
            }
        });
    }
}
