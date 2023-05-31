package com.example.crimehotspotapp;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.example.crimehotspotapp.Common.common;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.crimehotspotapp.databinding.ActivityReportBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.Manifest;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;
import java.util.List;
import io.paperdb.Paper;

public class Report extends FragmentActivity implements OnMapReadyCallback {

    Location currentLocation;
    private GoogleMap mMap;
    TextInputEditText Crime,City,Province;
    Marker marker;
    LatLng newlatlng;
    String Code;
    FusedLocationProviderClient fusedLocationProviderClient;
    final int REQUEST_CODE = 101;
    SupportMapFragment supportMapFragment;
    private LocationManager locationManager;
    private ActivityReportBinding binding;
    CardView Report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
City = findViewById(R.id.City);
Crime = findViewById(R.id.Crime);
Province = findViewById(R.id.Province);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Report.this);

        fetchLastLocation();
        Report = findViewById(R.id.Report);
        Report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newlatlng == null){
                    Toast.makeText(Report.this, "Please Click a location in the map", Toast.LENGTH_SHORT).show();
                return;
                }
                com.example.crimehotspotapp.Model.Report newReport = new com.example.crimehotspotapp.Model.Report();
                Paper.init(com.example.crimehotspotapp.Report.this);
                newReport.setLat(String.valueOf(newlatlng.latitude));
                newReport.setLog(String.valueOf(newlatlng.longitude));
                newReport.setCrime(Crime.getText().toString());
                newReport.setCity(City.getText().toString());
                newReport.setCode(Code);
                newReport.setProvince(Province.getText().toString());
                newReport.setID(Paper.book().read("UserID").toString());

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Report");

                reference.child(String.valueOf(System.currentTimeMillis())).setValue(newReport).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Report.this, "Crime Reported", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Report.this, Home.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Report.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(Report.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Report.this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Paper.init(Report.this);
                    Paper.book().write("LastLocation", currentLocation.getLatitude() + "," + currentLocation.getLongitude());
                    supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(Report.this);
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (common.currentLocation != null) {
            currentLocation = common.currentLocation;
        }
        if (currentLocation == null) {
            fetchLastLocation();
            return;
        }

        LatLng LatLog = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLng(LatLog));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLog,18));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                mMap.clear();
                newlatlng = latLng;
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                Geocoder geocoder = new Geocoder(Report.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        String town = address.getLocality();
                        City.setText(town);
                        Province.setText(address.getAdminArea());
                        Code = address.getPostalCode();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}