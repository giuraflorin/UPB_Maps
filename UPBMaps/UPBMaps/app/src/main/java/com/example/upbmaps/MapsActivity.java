package com.example.upbmaps;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private EditText editTextSearch;
    private Button buttonSearch;

    private Marker automaticaVecheMarker;
    private Marker automaticaVecheMarker2;
    private Marker automaticaVecheMarker3;
    private Marker automaticaVecheMarker4;
    private Marker automaticaPrecisMarker;
    private Marker inginerieElectricaMarker;
    private Marker inginerieElectricaMarker2;
    private Marker energeticaMarker;
    private Marker energeticaMarker2;
    private Marker energeticaMarker3;
    private Marker rectoratUpbMarker;
    private Marker rectoratUpbMarkerAn;
    private Marker faimaMarker;
    private Marker transporturiMarker;
    private Marker transporturiMarker2;
    private Marker transporturiMarker3;
    private Marker transporturiMarker4;
    private Marker transporturiMarker5;
    private Marker transporturiMarker6;
    private Marker transporturiMarker7;
    private Marker aulaMarker;
    private Marker bibliotecaMarker;
    private Marker biotehnicaMarker;
    private Marker mecanicaMarker;
    private Marker roboticaMarker;
    private Marker roboticaMarker2;
    private Marker roboticaMarker3;
    private Marker filsMarker;
    private Marker secAutomatica;
    private Marker secFils;
    private Marker secRobotica;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMarkers();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_maps);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent homeIntent = new Intent(MapsActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    return true;
                } else if (itemId == R.id.nav_maps) {
                    Intent homeIntent = new Intent(MapsActivity.this, MapsActivity.class);
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
                                            Intent classesIntent = new Intent(MapsActivity.this, ClassesActivity.class);
                                            startActivity(classesIntent);
                                        } else if (status.equals("Visitor")) {
                                            Intent visitorIntent = new Intent(MapsActivity.this, VisitorActivity.class);
                                            startActivity(visitorIntent);
                                        } else if (status.equals("Administration")) {
                                            Intent professorIntent = new Intent(MapsActivity.this, ProfessorActivity.class);
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
                }else if (itemId == R.id.nav_heat_map) {
                    Intent homeIntent = new Intent(MapsActivity.this, HeatMapActivity.class);
                    startActivity(homeIntent);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        LatLng upb1 = new LatLng(44.43575607427035, 26.04752189826429);
        automaticaVecheMarker = googleMap.addMarker(new MarkerOptions().position(upb1).title("Facultatea de automatica si calculatoare(EC)"));

        LatLng upb2 = new LatLng(44.4349966843673, 26.04771678652902);
        automaticaPrecisMarker = googleMap.addMarker(new MarkerOptions().position(upb2).title("Facultatea de automatica si calculatoare(PR)"));

        LatLng upb3 = new LatLng(44.43619011305649, 26.04581229759456);
        inginerieElectricaMarker = googleMap.addMarker(new MarkerOptions().position(upb3).title("Facultatea de Inginerie Electirca(EA)"));

        LatLng upb4 = new LatLng(44.43594346056203, 26.048416474285293);
        automaticaVecheMarker2 = googleMap.addMarker(new MarkerOptions().position(upb4).title("Facultatea de automatica si calculatoare(EF)"));

        LatLng upb5 = new LatLng(44.435830252870794, 26.04807142973422);
        automaticaVecheMarker3 = googleMap.addMarker(new MarkerOptions().position(upb5).title("Facultatea de automatica si calculatoare(ED)"));

        LatLng upb6 = new LatLng(44.43620198053712, 26.046488675492693);
        inginerieElectricaMarker2 = googleMap.addMarker(new MarkerOptions().position(upb6).title("Facultatea de Inginerie Electirca(EB)"));

        LatLng upb7 = new LatLng(44.436743094970325, 26.04778330949652);
        automaticaVecheMarker4 = googleMap.addMarker(new MarkerOptions().position(upb7).title("Facultatea de automatica si calculatoare(EG)"));

        LatLng upb8 = new LatLng(44.43736625768689, 26.04855545525021);
        energeticaMarker = googleMap.addMarker(new MarkerOptions().position(upb8).title("Facultatea de Energetica(EI)"));

        LatLng upb9 = new LatLng(44.43799059023199, 26.048418662606462);
        energeticaMarker2 = googleMap.addMarker(new MarkerOptions().position(upb9).title("Facultatea de Energetica(EH)"));

        LatLng upb10 = new LatLng(44.43840808497142, 26.048424027004103);
        energeticaMarker3 = googleMap.addMarker(new MarkerOptions().position(upb10).title("Facultatea de Energetica(EL)"));

        LatLng upb11 = new LatLng(44.43850001002481, 26.051521978258446);
        rectoratUpbMarker = googleMap.addMarker(new MarkerOptions().position(upb11).title("Rectorat UPB"));

        LatLng upb12 = new LatLng(44.43876046353675, 26.050459823684104);
        rectoratUpbMarkerAn = googleMap.addMarker(new MarkerOptions().position(upb12).title("Rectorat UPB(AN)"));

        LatLng upb13 = new LatLng(44.4396739567459, 26.050588569585585);
        faimaMarker = googleMap.addMarker(new MarkerOptions().position(upb13).title("Facultatea de Antreprenoriat, Ingineria şi Managementul Afacerilor(BN)"));

        LatLng upb14 = new LatLng(44.439723180653836, 26.05270426071864);
        transporturiMarker = googleMap.addMarker(new MarkerOptions().position(upb14).title("Facultatea de Transporturi(JA)"));

        LatLng upb15 = new LatLng(44.439815103632974, 26.052607701214814);
        transporturiMarker2 = googleMap.addMarker(new MarkerOptions().position(upb15).title("Facultatea de Transporturi(JB)"));

        LatLng upb16 = new LatLng(44.43924058266027, 26.05388443262271);
        transporturiMarker3 = googleMap.addMarker(new MarkerOptions().position(upb16).title("Facultatea de Transporturi(JC)"));

        LatLng upb17 = new LatLng(44.43927888423342, 26.052822277978073);
        transporturiMarker4= googleMap.addMarker(new MarkerOptions().position(upb17).title("Facultatea de Transporturi(JE)"));

        LatLng upb18 = new LatLng(44.43874649010407, 26.052613065705685);
        transporturiMarker5= googleMap.addMarker(new MarkerOptions().position(upb18).title("Facultatea de Transporturi(JF)"));

        LatLng upb19 = new LatLng(44.43850518757118, 26.053203151670765);
        transporturiMarker6= googleMap.addMarker(new MarkerOptions().position(upb19).title("Facultatea de Transporturi(JG)"));

        LatLng upb20 = new LatLng(44.439895536113845, 26.054300175074673);
        transporturiMarker7= googleMap.addMarker(new MarkerOptions().position(upb20).title("Facultatea de Transporturi(JI)"));

        LatLng upb21 = new LatLng(44.440589838787886, 26.051184095581807);
        aulaMarker= googleMap.addMarker(new MarkerOptions().position(upb21).title("AULA"));

        LatLng upb22 = new LatLng(44.44114902636479, 26.051251150779862);
        bibliotecaMarker= googleMap.addMarker(new MarkerOptions().position(upb22).title("Biblioteca"));

        LatLng upb23 = new LatLng(44.439906296084395, 26.04560843466543);
        biotehnicaMarker= googleMap.addMarker(new MarkerOptions().position(upb23).title("Facultatea de Inginerie a Sistemelor Biotehnice(ISB)"));

        LatLng upb24 = new LatLng(44.44059862159864, 26.047911399468173);
        mecanicaMarker= googleMap.addMarker(new MarkerOptions().position(upb24).title("Facultatea de Mecanica si Mecatronica(CG)"));

        LatLng upb25 = new LatLng(44.44037999338074, 26.04904693328695);
        roboticaMarker= googleMap.addMarker(new MarkerOptions().position(upb25).title("Facultatea de Inginerie Industriala si Robotica(CF)"));

        LatLng upb26 = new LatLng(44.4411178603335, 26.049308488826963);
        roboticaMarker2= googleMap.addMarker(new MarkerOptions().position(upb26).title("Facultatea de Inginerie Industriala si Robotica(CE)"));

        LatLng upb27 = new LatLng(44.441482235647285, 26.049633838446635);
        roboticaMarker3= googleMap.addMarker(new MarkerOptions().position(upb27).title("Facultatea de Inginerie Industriala si Robotica(CB)"));

        LatLng upb28 = new LatLng(44.44229473133056, 26.049577065063907);
        filsMarker= googleMap.addMarker(new MarkerOptions().position(upb28).title("Facultatea de Inginerie in Limbi Straine(CJ)"));

        LatLng upb29 = new LatLng(44.4358629041428, 26.04731221494679);
        secAutomatica= googleMap.addMarker(new MarkerOptions().position(upb29).title("Secretariat Automatica(L-V : 11:00-17:00)"));

        LatLng upb30 = new LatLng(44.43949317545656, 26.052932319240746);
        secFils= googleMap.addMarker(new MarkerOptions().position(upb30).title("Secretariat Facultatea de Inginerie in Limbi Straine(L-V : 11:00-17:00)"));

        LatLng upb31 = new LatLng(44.44070062813462, 26.048838325496718);
        secRobotica= googleMap.addMarker(new MarkerOptions().position(upb31).title("Secretariat Facultatea de Inginerie Industriala si Robotica(L-V : 11:00-17:00)"));

        float zoomLevel = 16.0f;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(upb1, zoomLevel));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                return true;
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String markerTitle = marker.getTitle();
                LatLng markerPosition = marker.getPosition();

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + markerPosition.latitude + "," + markerPosition.longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
    }

    private void searchMarkers() {
        String searchText = editTextSearch.getText().toString().toLowerCase();

        if (automaticaVecheMarker != null && automaticaPrecisMarker != null) {
            if (automaticaVecheMarker.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(automaticaVecheMarker.getPosition()));
                automaticaVecheMarker.showInfoWindow();
            } else if (automaticaPrecisMarker.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(automaticaPrecisMarker.getPosition()));
                automaticaPrecisMarker.showInfoWindow();
            } else if (inginerieElectricaMarker.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(inginerieElectricaMarker.getPosition()));
                inginerieElectricaMarker.showInfoWindow();
            } else if (automaticaVecheMarker2.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(automaticaVecheMarker2.getPosition()));
                automaticaVecheMarker2.showInfoWindow();
            }else if (automaticaVecheMarker3.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(automaticaVecheMarker3.getPosition()));
                automaticaVecheMarker3.showInfoWindow();
            }else if (inginerieElectricaMarker2.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(inginerieElectricaMarker2.getPosition()));
                inginerieElectricaMarker2.showInfoWindow();
            }else if (automaticaVecheMarker4.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(automaticaVecheMarker4.getPosition()));
                automaticaVecheMarker4.showInfoWindow();
            }else if (energeticaMarker.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(energeticaMarker.getPosition()));
                energeticaMarker.showInfoWindow();
            }else if (energeticaMarker2.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(energeticaMarker2.getPosition()));
                energeticaMarker2.showInfoWindow();
            }else if (energeticaMarker3.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(energeticaMarker3.getPosition()));
                energeticaMarker3.showInfoWindow();
            }else if (rectoratUpbMarker.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(rectoratUpbMarker.getPosition()));
                rectoratUpbMarker.showInfoWindow();
            }else if (rectoratUpbMarkerAn.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(rectoratUpbMarkerAn.getPosition()));
                rectoratUpbMarkerAn.showInfoWindow();
            }else if (faimaMarker.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(faimaMarker.getPosition()));
                faimaMarker.showInfoWindow();
            }else if (transporturiMarker.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(transporturiMarker.getPosition()));
                transporturiMarker.showInfoWindow();
            }else if (transporturiMarker2.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(transporturiMarker2.getPosition()));
                transporturiMarker2.showInfoWindow();
            }else if (transporturiMarker3.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(transporturiMarker3.getPosition()));
                transporturiMarker3.showInfoWindow();
            }else if (transporturiMarker4.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(transporturiMarker4.getPosition()));
                transporturiMarker4.showInfoWindow();
            }else if (transporturiMarker5.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(transporturiMarker5.getPosition()));
                transporturiMarker5.showInfoWindow();
            }else if (transporturiMarker6.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(transporturiMarker6.getPosition()));
                transporturiMarker6.showInfoWindow();
            }else if (transporturiMarker7.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(transporturiMarker7.getPosition()));
                transporturiMarker7.showInfoWindow();
            }else if (aulaMarker.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(aulaMarker.getPosition()));
                aulaMarker.showInfoWindow();
            }else if (bibliotecaMarker.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(bibliotecaMarker.getPosition()));
                bibliotecaMarker.showInfoWindow();
            }else if (biotehnicaMarker.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(biotehnicaMarker.getPosition()));
                biotehnicaMarker.showInfoWindow();
            }else if (mecanicaMarker.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(mecanicaMarker.getPosition()));
                mecanicaMarker.showInfoWindow();
            }else if (roboticaMarker.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(roboticaMarker.getPosition()));
                roboticaMarker.showInfoWindow();
            }else if (roboticaMarker2.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(roboticaMarker2.getPosition()));
                roboticaMarker2.showInfoWindow();
            }else if (roboticaMarker3.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(roboticaMarker3.getPosition()));
                roboticaMarker3.showInfoWindow();
            }else if (filsMarker.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(filsMarker.getPosition()));
                filsMarker.showInfoWindow();
            }else if (secAutomatica.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(secAutomatica.getPosition()));
                secAutomatica.showInfoWindow();
            }else if (secFils.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(secFils.getPosition()));
                secFils.showInfoWindow();
            }else if (secRobotica.getTitle().toLowerCase().contains(searchText)) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(secRobotica.getPosition()));
                secRobotica.showInfoWindow();
            }
        }
    }
}

