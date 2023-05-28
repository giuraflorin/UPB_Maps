package com.example.upbmaps;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.List;

public class HeatMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final LatLng UPB_LOCATION = new LatLng(44.435756, 26.047522);

    private MapView heatmapView;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat_map);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_heat_map);

        heatmapView = findViewById(R.id.heatmapView);
        heatmapView.onCreate(savedInstanceState);
        heatmapView.getMapAsync(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent homeIntent = new Intent(HeatMapActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    return true;
                } else if (itemId == R.id.nav_maps) {
                    Intent mapsIntent = new Intent(HeatMapActivity.this, MapsActivity.class);
                    startActivity(mapsIntent);
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
                                            Intent classesIntent = new Intent(HeatMapActivity.this, ClassesActivity.class);
                                            startActivity(classesIntent);
                                        } else if (status.equals("Visitor")) {
                                            Intent visitorIntent = new Intent(HeatMapActivity.this, VisitorActivity.class);
                                            startActivity(visitorIntent);
                                        } else if (status.equals("Administration")) {
                                            Intent professorIntent = new Intent(HeatMapActivity.this, ProfessorActivity.class);
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
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UPB_LOCATION, 15f));

        List<WeightedLatLng> heatmapData = generateHeatmapData();

        int[] colors = {
                Color.rgb(102, 225, 0),   // verde
                Color.rgb(255, 0, 0)      // roșu
        };
        float[] startPoints = {0.2f, 1f};
        Gradient gradient = new Gradient(colors, startPoints);
        HeatmapTileProvider heatMapProvider = new HeatmapTileProvider.Builder()
                .weightedData(heatmapData)
                .gradient(gradient)
                .radius(50)
                .build();

        TileOverlay tileOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatMapProvider));
    }

    @Override
    protected void onResume() {
        super.onResume();
        heatmapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        heatmapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        heatmapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        heatmapView.onLowMemory();
    }

    private List<WeightedLatLng> generateHeatmapData() {
        List<WeightedLatLng> heatmapData = new ArrayList<>();
        heatmapData.add(new WeightedLatLng(new LatLng(44.43824802099029, 26.05047403884272), 3));
        heatmapData.add(new WeightedLatLng(new LatLng(44.440867550407404, 26.049811016295735), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.44241698906757, 26.049771814745572), 1));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43971220896579, 26.05136930235841), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43969688845294, 26.050891869176986), 3));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43988456444277, 26.052469007937724), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.438639763349656, 26.05059146184392), 1));
        heatmapData.add(new WeightedLatLng(new LatLng(44.4373030089377, 26.04888021238249), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.436077304670846, 26.047759049080803), 3));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43562148933902, 26.047694676073867), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.4354950861363, 26.04774832026799), 1));
        heatmapData.add(new WeightedLatLng(new LatLng(44.436199876241034, 26.0470026662014), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43621136731405, 26.046171181477106), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43614242084417, 26.045725934832745), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43628414405515, 26.045731299250146), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43637607244065, 26.045677655076126), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43760943098342, 26.047415726388458), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43872402764083, 26.05021595243177), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43885425403976, 26.05004429107491), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43858997075524, 26.05116545439543), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43916832829301, 26.050425164868077), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.439685398070374, 26.050602190653642), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43945175970862, 26.052833788336162), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.439758170486506, 26.053096644788855), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.438842763514785, 26.052705042390098), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.4398577536415, 26.053836934508357), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.4397351897366, 26.051675074248926), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.439478570718535, 26.05153023497908), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.44035949697836, 26.051347844708275), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.44076165455092, 26.051186912176167), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43988073436037, 26.049282543928143), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.440815275354204, 26.049464934013685), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.44112933905147, 26.049599044448726), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.44078846496037, 26.04855834747276), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.44084591578924, 26.048150651750216), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.44083059557373, 26.048123829663204), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.441493191236766, 26.049674146336315), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.44173831142283, 26.04977070584955), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.44222088879016, 26.05023204576056), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.4423434474852, 26.049582951254926), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43687401537064, 26.045763485730735), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43908008173716, 26.05136310388754), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43892687476761, 26.05403458378824), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.44068873065505, 26.052478902768062), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.438335999260275, 26.050540717610847), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.438055787552166, 26.049661088335025), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43778201619469, 26.049196463640616), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.437517906140094, 26.04914233260826), 2));
        heatmapData.add(new WeightedLatLng(new LatLng(44.43738907152985, 26.048957384894823), 2));


        return heatmapData;
    }
}