package com.example.disp_final;
import android.Manifest;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.disp_final.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private FusedLocationProviderClient fusedLocationClient;
    double latitude = 0;
    double longitude = 0;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getLastLocation();
            handler.postDelayed(this, 5000);
            //Toast.makeText(getApplicationContext(), "nanay", Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        handler.post(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }


    ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // El permiso fue concedido
                } else {
                    // El permiso fue denegado
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


    }
    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Task<Location> locationResult = fusedLocationClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    LatLng farm0 = new LatLng(-11.986287, -77.067032);
                    LatLng farm1 = new LatLng(-12.018283, -76.952426);
                    LatLng farm2 = new LatLng(37.666596, -100.043393);
                    LatLng farm3 = new LatLng(-10.5, -76.5);

                    LatLng[] farms = new LatLng[4];
                    farms[0] = farm0;
                    farms[1] = farm1;
                    farms[2] = farm2;
                    farms[3] = farm3;

                    mMap.addMarker(new MarkerOptions().position(farm0).title("Farmacia1"));
                    mMap.addMarker(new MarkerOptions().position(farm1).title("Farmacia2"));
                    mMap.addMarker(new MarkerOptions().position(farm2).title("Farmacia3"));
                    mMap.addMarker(new MarkerOptions().position(farm3).title("Farmacia4"));
                    String[] farmNames = {"F1", "F2", "F3", "F4"};



                    if (task.isSuccessful()) {
                        // La ubicación se obtuvo correctamente
                        Location location = task.getResult();
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            // Agrega un marcador en tu ubicación y mueve la cámara
                            LatLng myLocation = new LatLng(latitude, longitude);
                            mMap.addMarker(new MarkerOptions().position(myLocation).title("Mi ubicación").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                            obtener_la_menor_distancia(myLocation, farms,farmNames);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "nanay", Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }
    }

    private void obtener_la_menor_distancia(LatLng myLocation, LatLng[] farms, String[] farmNames) {
        double minDistance = Double.MAX_VALUE;
        String closestFarm = "";
        for (int i = 0; i < farms.length; i++) {
            double distance = distanceBetween(myLocation, farms[i]);
            if (distance < minDistance) {
                minDistance = distance;
                closestFarm = farmNames[i];
            }
        }
        String message = "La farmacia más cercana es: " + closestFarm;
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private double distanceBetween(LatLng point1, LatLng point2) {
        double earthRadius = 6371; // Radio de la Tierra en km
        double lat1 = Math.toRadians(point1.latitude);
        double lat2 = Math.toRadians(point2.latitude);
        double deltaLat = Math.toRadians(point2.latitude - point1.latitude);
        double deltaLng = Math.toRadians(point2.longitude - point1.longitude);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLastLocation();


        //double distance = distanceBetween(farm4, me);

    }
}