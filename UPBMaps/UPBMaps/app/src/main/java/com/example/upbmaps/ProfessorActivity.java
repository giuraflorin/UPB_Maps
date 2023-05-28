package com.example.upbmaps;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ProfessorActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private LinearLayout linearClasses;
    private TextView textViewNoClasses;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private static final int PICK_PDF_REQUEST = 1;
    private Uri pdfUri;
    private String pdfUrl;
    private ClassInfo classInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        linearClasses = findViewById(R.id.linearClasses);
        textViewNoClasses = findViewById(R.id.textViewNoClasses);

        Button buttonAddClass = findViewById(R.id.buttonAddClass);
        buttonAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddClassPopup();
            }
        });

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        databaseReference.child("classes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                linearClasses.removeAllViews();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ClassInfo classInfo = snapshot.getValue(ClassInfo.class);
                        addClassView(classInfo);
                    }
                }

                updateClassesViewVisibility();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfessorActivity.this, "Failed to retrieve classes", Toast.LENGTH_SHORT).show();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_class);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent homeIntent = new Intent(ProfessorActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    return true;
                } else if (itemId == R.id.nav_maps) {
                    Intent homeIntent = new Intent(ProfessorActivity.this, MapsActivity.class);
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
                                        Intent professorIntent = new Intent(ProfessorActivity.this, ProfessorActivity.class);
                                        startActivity(professorIntent);
                                    } else if (status != null && status.equals("Student")) {
                                        Intent classesIntent = new Intent(ProfessorActivity.this, ClassesActivity.class);
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
                } else if (itemId == R.id.nav_heat_map) {
                    Intent homeIntent = new Intent(ProfessorActivity.this, HeatMapActivity.class);
                    startActivity(homeIntent);
                    return true;
                }
                return false;
            }
        });


    }

    private void showAddClassPopup() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_add_class, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextSubject = dialogView.findViewById(R.id.editTextSubject);
        final EditText editTextClass = dialogView.findViewById(R.id.editTextClass);
        final EditText editTextPlaces = dialogView.findViewById(R.id.editTextPlaces);
        final EditText editTextHourFrom = dialogView.findViewById(R.id.editTextHourFrom);
        final EditText editTextHourTo = dialogView.findViewById(R.id.editTextHourTo);
        final EditText editTextDate = dialogView.findViewById(R.id.editTextDate);

        editTextHourFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(editTextHourFrom);
            }
        });

        editTextHourTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(editTextHourTo);
            }
        });

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(editTextDate);
            }
        });

        final Button buttonAddPDF = dialogView.findViewById(R.id.buttonAddPDF);
        buttonAddPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDF();
            }
        });

        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String subject = editTextSubject.getText().toString().trim();
                String className = editTextClass.getText().toString().trim();
                String places = editTextPlaces.getText().toString().trim();
                String hourFrom = editTextHourFrom.getText().toString().trim();
                String hourTo = editTextHourTo.getText().toString().trim();
                String date = editTextDate.getText().toString().trim();

                if (subject.isEmpty() || className.isEmpty() || hourFrom.isEmpty() || hourTo.isEmpty() || date.isEmpty() || places.isEmpty()) {
                    Toast.makeText(ProfessorActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    String classId = FirebaseDatabase.getInstance().getReference().child("classes").push().getKey();

                    classInfo = new ClassInfo(subject, className,places, hourFrom, hourTo, date, pdfUrl);
                    classInfo.setClassId(classId);

                    DatabaseReference classRef = FirebaseDatabase.getInstance().getReference().child("classes").child(classId);
                    classRef.setValue(classInfo)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ProfessorActivity.this, "Class added successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ProfessorActivity.this, "Failed to add class", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }


    private void showTimePicker(final EditText editText) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(ProfessorActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                String time = timeFormat.format(calendar.getTime());
                editText.setText(time);
            }
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void showDatePicker(final EditText editText) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(ProfessorActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String date = dateFormat.format(calendar.getTime());
                editText.setText(date);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void checkDuplicateClass(final String subject, final String className,final String places, final String hourFrom, final String hourTo, final String date) {
        databaseReference.child("classes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isDuplicate = false;

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ClassInfo classInfo = snapshot.getValue(ClassInfo.class);
                        if (classInfo != null && classInfo.getClassName().equals(className)
                                && classInfo.getHourFrom().equals(hourFrom) && classInfo.getHourTo().equals(hourTo)
                                && classInfo.getDate().equals(date)) {
                            isDuplicate = true;
                            break;
                        }
                    }
                }

                if (isDuplicate) {
                    Toast.makeText(ProfessorActivity.this, "A class with the same details already exists", Toast.LENGTH_SHORT).show();
                } else {
                    String classId = FirebaseDatabase.getInstance().getReference().child("classes").push().getKey();

                    classInfo = new ClassInfo(subject, className, places, hourFrom, hourTo, date, pdfUrl);
                    classInfo.setClassId(classId);

                    DatabaseReference classRef = FirebaseDatabase.getInstance().getReference().child("classes").child(classId);
                    classRef.setValue(classInfo)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ProfessorActivity.this, "Class added successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ProfessorActivity.this, "Failed to add class", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfessorActivity.this, "Failed to retrieve classes", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void addClassView(ClassInfo classInfo) {
        View classView = getLayoutInflater().inflate(R.layout.item_class, null);

        TextView textViewSubject = classView.findViewById(R.id.textViewSubject);
        TextView textViewClass = classView.findViewById(R.id.textViewClass);
        TextView textViewPlaces = classView.findViewById(R.id.textViewPlaces);
        TextView textViewHour = classView.findViewById(R.id.textViewHour);
        TextView textViewDate = classView.findViewById(R.id.textViewDate);

        textViewSubject.setTypeface(null, Typeface.BOLD);
        textViewClass.setTypeface(null, Typeface.BOLD);
        textViewPlaces.setTypeface(null, Typeface.BOLD);
        textViewHour.setTypeface(null, Typeface.BOLD);
        textViewDate.setTypeface(null, Typeface.BOLD);

        textViewSubject.setText("SUBJECT : " + classInfo.getSubject());
        textViewClass.setText("CLASS AND PREFIX NUMBER : " + classInfo.getClassName());
        textViewPlaces.setText("Number of places : " + classInfo.getClassPlaces());
        textViewHour.setText("FROM : " + classInfo.getHourFrom() + " TO : " + classInfo.getHourTo());
        textViewDate.setText("DATE : " + classInfo.getDate());

        linearClasses.addView(classView);
        classView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClassDetailsPopup(classInfo);
            }
        });
    }

    private void updateClassesViewVisibility() {
        if (linearClasses.getChildCount() > 0) {
            linearClasses.setVisibility(View.VISIBLE);
            textViewNoClasses.setVisibility(View.GONE);
        } else {
            linearClasses.setVisibility(View.GONE);
            textViewNoClasses.setVisibility(View.VISIBLE);
        }
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pdfUri = data.getData();
            uploadPDF();
        }
    }
    private void uploadPDF() {
        if (pdfUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("pdfs").child(System.currentTimeMillis() + ".pdf");

            storageReference.putFile(pdfUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ProfessorActivity.this, "PDF uploaded successfully", Toast.LENGTH_SHORT).show();

                            Task<Uri> downloadUriTask = taskSnapshot.getStorage().getDownloadUrl();
                            downloadUriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    String pdfUrl = downloadUri.toString();
                                    classInfo.setPdfUrl(pdfUrl);
                                    saveClassToDatabase();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfessorActivity.this, "Failed to upload PDF", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(ProfessorActivity.this, "No PDF selected", Toast.LENGTH_SHORT).show();
            saveClassToDatabase();
        }
    }

    private void savePDFUrlToDatabase(String pdfUrl) {
        classInfo.setPdfUrl(pdfUrl);

        DatabaseReference classRef = databaseReference.child("classes").child(classInfo.getClassId());
        classRef.setValue(classInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfessorActivity.this, "Class updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfessorActivity.this, "Failed to update class", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void selectPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
    }
    private void showClassDetailsPopup(ClassInfo classInfo) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_class_details, null);
        dialogBuilder.setView(dialogView);


        TextView textViewSubject = dialogView.findViewById(R.id.textViewSubject);
        TextView textViewClass = dialogView.findViewById(R.id.textViewClass);
        TextView textViewPlaces = dialogView.findViewById(R.id.textViewPlaces);
        TextView textViewHour = dialogView.findViewById(R.id.textViewHour);
        TextView textViewDate = dialogView.findViewById(R.id.textViewDate);
        Button buttonDownloadPDF = dialogView.findViewById(R.id.buttonDownloadPdf);

        textViewSubject.setText("Subject: " + classInfo.getSubject());
        textViewClass.setText("Class: " + classInfo.getClassName());
        textViewPlaces.setText("Number of places : " + classInfo.getClassPlaces());
        textViewHour.setText("Hour: " + classInfo.getHourFrom() + " - " + classInfo.getHourTo());
        textViewDate.setText("Date: " + classInfo.getDate());

        buttonDownloadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPDF(classInfo.getPdfUrl());
            }
        });


        Button buttonDeleteClass = dialogView.findViewById(R.id.buttonDeleteClass);
        buttonDeleteClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

                String classId = classInfo.getClassId();

                String classPath = "classes/" + classId;

                databaseRef.child(classPath).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Clasa a fost ștearsă cu succes", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "A apărut o eroare la ștergerea clasei", Toast.LENGTH_SHORT).show();
                            }
                        });
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
    private void saveClassToDatabase() {
        DatabaseReference classRef = databaseReference.child("classes").child(classInfo.getClassId());
        classRef.setValue(classInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfessorActivity.this, "Class updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfessorActivity.this, "Failed to update class", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}