package com.example.codecatchersapp;
import android.Manifest;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.codecatchersapp.databinding.ActivityMapDisplayBinding;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * MapActivity is an implementation of the OnMapReadyCallback interface.
 * It is responsible for displaying a Google Map with markers on it.
 */

public class MapDisplayActivity extends FragmentActivity implements OnMapReadyCallback {

    /**
     * The GoogleMap object used to display the map.
     */

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private ActivityMapDisplayBinding binding;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng mCurrentLocation;

    /**
     * Called when the activity is starting.
     * Connects to the map_layout.xml and sets it as the content view.
     * Gets the intent and gets the SupportMapFragment for the map.
     * Accesses user current location if permission is granted and initializes camera in that location.
     * Registers this activity as the callback for when the map is ready.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     *                           then this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect to  map_layout.xml
        setContentView(R.layout.activity_map_display);
        Intent intent = getIntent();

        binding = ActivityMapDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapView mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Get the FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Request permission to access the user's location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        // If permission is granted, get the current location
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Set the current location variable and move the camera
                            mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 15));
                        }
                    }
                });
    }

    /**
     * Called when the map is ready to be used.
     * @param googleMap The GoogleMap object used to display the map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}