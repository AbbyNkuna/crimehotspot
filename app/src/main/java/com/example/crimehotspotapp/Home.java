package com.example.crimehotspotapp;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crimehotspotapp.Common.common;
import com.example.crimehotspotapp.Interface.ItemClickListener;
import com.example.crimehotspotapp.Model.Report;
import com.example.crimehotspotapp.ViewHolder.ReportViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.crimehotspotapp.databinding.ActivityHomeBinding;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import io.paperdb.Paper;

public class Home extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, LocationListener {

    Marker marker;
    TextView Firstname;
    ArrayList<String> lat = new ArrayList<>();
    ArrayList<String> log = new ArrayList<>();
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    Location currentLocation;
    private GoogleMap mMap;
    RecyclerView recyclerView;

    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Report, ReportViewHolder> adapter;

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Report");
    FusedLocationProviderClient fusedLocationProviderClient;
    final int REQUEST_CODE = 101;
    SupportMapFragment supportMapFragment;
    private LocationManager locationManager;
    private TextView locationTextView;
CardView fab;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(Home.this);
        View headerview = navigationView.getHeaderView(0);
        Firstname = headerview.findViewById(R.id.Name);
        Paper.init(Home.this);
        Firstname.setText(Paper.book().read("Name").toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Home.this);

        fetchLastLocation();
        instertvalues();
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, com.example.crimehotspotapp.Report.class));
            }
        });
    }

    private void instertvalues() {
        recyclerView = findViewById(R.id.RecyclerItems);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FirebaseRecyclerAdapter<Report, ReportViewHolder>(Report.class, R.layout.reportlayout, ReportViewHolder.class, reference.orderByChild("id").equalTo(Paper.book().read("UserID").toString())) {
            @Override
            protected void populateViewHolder(ReportViewHolder viewHolder, Report model, int position) {
                lat.add(model.getLat());
                log.add(model.getLog());

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(Home.this);
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Home.this);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int Position, Boolean isLongClick) {

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Home.this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Paper.init(Home.this);
                    Paper.book().write("LastLocation", currentLocation.getLatitude() + "," + currentLocation.getLongitude());
                    supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(Home.this);
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.seller, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_Profile) {


        } else if (id == R.id.nav_List) {
            startActivity(new Intent(Home.this, List.class));
        } else if (id == R.id.nav_Search) {
            startActivity(new Intent(Home.this, Search.class));
        } else if (id == R.id.nav_Logout) {
            Paper.book().destroy();
            Intent i = new Intent(Home.this, SignIn.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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


        for (int i = 0; i < lat.size(); i++) {
            LatLng location = new LatLng(Double.parseDouble(lat.get(i)), Double.parseDouble(log.get(i)));
            mMap.addCircle(new CircleOptions().center(location).radius(50).fillColor(android.R.color.holo_red_light).strokeColor(android.R.color.holo_red_light).strokeWidth(0));
        }


        if (marker != null) {  // marker name is declared as a gloval variable.
            marker.remove();
        }
        LatLng LatLog = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        //  marker = new MarkerOptions().position(LatLog).title("I Am Here .");
        mMap.animateCamera(CameraUpdateFactory.newLatLng(LatLog));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLog, 18));
        marker = mMap.addMarker(new MarkerOptions().position(LatLog).title("I Am Here .").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        //

    }

    @Override
    public void onLocationChanged(Location location) {
        // Called when the location has changed
        fetchLastLocation();
        // locationTextView.setText("Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude());
        //here we update the location on the map

        LatLng myActualLocation = new LatLng(location.getLatitude(), location.getLongitude());
        Intent serviceIntent = new Intent(Home.this, MyBackgroundService.class);
        startService(serviceIntent);

        if (marker != null) {  // marker name is declared as a gloval variable.
            marker.remove();
        }

        marker = mMap.addMarker(new MarkerOptions().position(myActualLocation).title("I Am Here .").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(myActualLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myActualLocation, 18));

        currentLocation = location;

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Report");
        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Report report = snapshot.getValue(Report.class);

                float location = DistanceCalculator.calculateDistance(currentLocation.getLatitude(), currentLocation.getLongitude(),
                        Double.parseDouble(report.getLat()), Double.parseDouble(report.getLog()));
                 if (location > 800 && location < 10000) {
                    NotificationHelper.createNotificationChannel(Home.this);
                    NotificationHelper.showNotification(Home.this,"They has been a "+report.getCrime() + " in " + report.getCity() + "Just 1 km from your Location");
            }
                if (location >400 && location < 700) {

                    NotificationHelper.createNotificationChannel(Home.this);
                    NotificationHelper.showNotification(Home.this,"They has been a "+report.getCrime() + " in " + report.getCity() + "Just Less Than 600 Meters  from your Location");


                }
                if (location < 200) {
                    NotificationHelper.createNotificationChannel(Home.this);
                    NotificationHelper.showNotification(Home.this,"They has been a "+report.getCrime() + " in " + report.getCity() + " Please be  Vigilant");

                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Report report = snapshot.getValue(Report.class);

                float location = DistanceCalculator.calculateDistance(currentLocation.getLatitude(), currentLocation.getLongitude(),
                        Double.parseDouble(report.getLat()), Double.parseDouble(report.getLog()));
                if (location > 800 && location < 10000) {

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Home.this)
                            .setSmallIcon(R.drawable.ic_baseline_security_24)
                            .setContentTitle("Be On Alert")
                            .setContentText("They has been a "+report.getCrime() + " in " + report.getCity() + "Just 1 km from your Location");


                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Home.this);

                    if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    notificationManager.notify(1, builder.build());


                }
                if (location >400 && location < 700) {

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Home.this)
                            .setSmallIcon(R.drawable.ic_baseline_security_24)
                            .setContentTitle("Be On Alert")
                            .setContentText("They has been a "+report.getCrime() + " in " + report.getCity() + "Just Less Than 600 Meters  from your Location");


                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Home.this);

                    if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    notificationManager.notify(1, builder.build());


                }
                if (location < 200) {

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Home.this)
                            .setSmallIcon(R.drawable.ic_baseline_security_24)
                            .setContentTitle("Be On Alert")
                            .setContentText("They has been a "+report.getCrime() + " in " + report.getCity() + " Please be  Vigilant");


                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Home.this);

                    if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    notificationManager.notify(1, builder.build());


                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Report report = snapshot.getValue(Report.class);

                float location = DistanceCalculator.calculateDistance(currentLocation.getLatitude(), currentLocation.getLongitude(),
                        Double.parseDouble(report.getLat()), Double.parseDouble(report.getLog()));
                if (location > 800 && location < 10000) {

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Home.this)
                            .setSmallIcon(R.drawable.ic_baseline_security_24)
                            .setContentTitle("Be On Alert")
                            .setContentText("They has been a "+report.getCrime() + " in " + report.getCity() + "Just 1 km from your Location");


                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Home.this);

                    if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    notificationManager.notify(1, builder.build());


                }
                if (location >400 && location < 700) {

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Home.this)
                            .setSmallIcon(R.drawable.ic_baseline_security_24)
                            .setContentTitle("Be On Alert")
                            .setContentText("They has been a "+report.getCrime() + " in " + report.getCity() + "Just Less Than 600 Meters  from your Location");


                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Home.this);

                    if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    notificationManager.notify(1, builder.build());


                }
                if (location < 200) {

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Home.this)
                            .setSmallIcon(R.drawable.ic_baseline_security_24)
                            .setContentTitle("Be On Alert")
                            .setContentText("They has been a "+report.getCrime() + " in " + report.getCity() + " Please be  Vigilant");


                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Home.this);

                    if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    notificationManager.notify(1, builder.build());


                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Report report = snapshot.getValue(Report.class);

                float location = DistanceCalculator.calculateDistance(currentLocation.getLatitude(), currentLocation.getLongitude(),
                        Double.parseDouble(report.getLat()), Double.parseDouble(report.getLog()));
                if (location > 800 && location < 10000) {

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Home.this)
                            .setSmallIcon(R.drawable.ic_baseline_security_24)
                            .setContentTitle("Be On Alert")
                            .setContentText("They has been a "+report.getCrime() + " in " + report.getCity() + "Just 1 km from your Location");


                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Home.this);

                    if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    notificationManager.notify(1, builder.build());


                }
                if (location >400 && location < 700) {

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Home.this)
                            .setSmallIcon(R.drawable.ic_baseline_security_24)
                            .setContentTitle("Be On Alert")
                            .setContentText("They has been a "+report.getCrime() + " in " + report.getCity() + "Just Less Than 600 Meters  from your Location");


                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Home.this);

                    if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    notificationManager.notify(1, builder.build());


                }
                if (location < 200) {

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Home.this)
                            .setSmallIcon(R.drawable.ic_baseline_security_24)
                            .setContentTitle("Be On Alert")
                            .setContentText("They has been a "+report.getCrime() + " in " + report.getCity() + " Please be  Vigilant");


                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Home.this);

                    if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    notificationManager.notify(1, builder.build());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Check if permissions have been granted
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Request location updates
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            }else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates when the activity is paused
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume location updates when the activity is resumed
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    // Other methods from LocationListener interface
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
}