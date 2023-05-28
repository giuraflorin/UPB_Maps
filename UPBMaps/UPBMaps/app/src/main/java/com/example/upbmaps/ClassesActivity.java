package com.example.upbmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClassesActivity extends AppCompatActivity {

    private SearchView searchView;
    private ListView listViewClasses;
    private TextView textViewNoClasses;
    private Button buttonAdd;

    private DatabaseReference classesRef;
    private ValueEventListener classesListener;

    private ClassListAdapter classListAdapter;
    private BottomNavigationView bottomNavigationView;
    private List<ClassInfo> classList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);

        searchView = findViewById(R.id.searchView);
        listViewClasses = findViewById(R.id.listViewClasses);
        textViewNoClasses = findViewById(R.id.textViewNoClasses);
        buttonAdd = findViewById(R.id.buttonAdd);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            classesRef = FirebaseDatabase.getInstance().getReference("classes");
            classList = new ArrayList<>();
            classListAdapter = new ClassListAdapter(this, classList);
            listViewClasses.setAdapter(classListAdapter);

            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (TextUtils.isEmpty(newText)) {
                        classListAdapter.getFilter().filter("");
                    } else {
                        classListAdapter.getFilter().filter(newText);
                    }
                    return true;
                }
            });

            classesListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    classList.clear();
                    for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
                        ClassInfo classInfo = classSnapshot.getValue(ClassInfo.class);
                        classList.add(classInfo);
                    }
                    classListAdapter.notifyDataSetChanged();
                    updateNoClassesVisibility();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            };
        }
        listViewClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassInfo classInfo = classList.get(position);
                showClassDetailsPopup(classInfo);
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_class);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent homeIntent = new Intent(ClassesActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    return true;
                } else if (itemId == R.id.nav_maps) {
                    Intent homeIntent = new Intent(ClassesActivity.this, MapsActivity.class);
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
                                    if (status != null && status.equals("Professor/Administrative Staff")) {
                                        Intent professorIntent = new Intent(ClassesActivity.this, ProfessorActivity.class);
                                        startActivity(professorIntent);
                                    } else if (status != null && status.equals("Student")) {
                                        Intent classesIntent = new Intent(ClassesActivity.this, ClassesActivity.class);
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
                    Intent homeIntent = new Intent(ClassesActivity.this, HeatMapActivity.class);
                    startActivity(homeIntent);
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (classesRef != null && classesListener != null) {
            classesRef.addValueEventListener(classesListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (classesRef != null && classesListener != null) {
            classesRef.removeEventListener(classesListener);
        }
    }

    private void updateNoClassesVisibility() {
        if (classList.isEmpty()) {
            textViewNoClasses.setVisibility(View.VISIBLE);
            listViewClasses.setVisibility(View.GONE);
        } else {
            textViewNoClasses.setVisibility(View.GONE);
            listViewClasses.setVisibility(View.VISIBLE);
        }
    }
    private void showClassDetailsPopup(ClassInfo classInfo) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_class_details_student, null);
        dialogBuilder.setView(dialogView);

        TextView textViewSubject = dialogView.findViewById(R.id.textViewSubject);
        TextView textViewClass = dialogView.findViewById(R.id.textViewClass);
        TextView textViewPlaces = dialogView.findViewById(R.id.textViewPlaces);
        TextView textViewHour = dialogView.findViewById(R.id.textViewHour);
        TextView textViewDate = dialogView.findViewById(R.id.textViewDate);
        Button buttonDownloadPDF = dialogView.findViewById(R.id.buttonDownloadPdf);

        textViewSubject.setText("SUBJECT: " + classInfo.getSubject());
        textViewClass.setText("CLASS: " + classInfo.getClassName());
        textViewPlaces.setText("NUMBER OF PLACES : " + classInfo.getClassPlaces());
        textViewHour.setText("HOUR: " + classInfo.getHourFrom() + " - " + classInfo.getHourTo());
        textViewDate.setText("DATE: " + classInfo.getDate());

        buttonDownloadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPDF(classInfo.getPdfUrl());
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
    private void downloadPDF(String pdfUrl) {
        if (pdfUrl != null && !pdfUrl.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(pdfUrl));
            startActivity(intent);
        } else {
            Toast.makeText(this, "No PDF available", Toast.LENGTH_SHORT).show();
        }
    }

}