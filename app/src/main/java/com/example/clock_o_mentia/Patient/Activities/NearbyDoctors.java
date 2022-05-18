package com.example.clock_o_mentia.Patient.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.clock_o_mentia.Doctor.Models.DoctorModel;
import com.example.clock_o_mentia.Patient.Adapters.NearbyDoctorsAdapter;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.clock_o_mentia.R;
import com.example.clock_o_mentia.databinding.ActivityNearbyDoctorsBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Comparator;

public class NearbyDoctors extends AppCompatActivity {

    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient client;
    private LatLng patient;
    private LocationRequest locationRequest;
    private static final int REQUEST_CHECK_SETTING = 1;

    private NearbyDoctorsAdapter nearbyDoctorsAdapter;
    private CollectionReference firestoreRef;
    private ActivityNearbyDoctorsBinding binding;

    private ArrayList<DoctorModel> doctorModelList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNearbyDoctorsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestoreRef = FirebaseFirestore.getInstance().collection("doctors_info");

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(NearbyDoctors.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(NearbyDoctors.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }

        binding.myAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NearbyDoctors.this,PatMyAppointments.class));
            }
        });
    }

    private void setupRecyclerView() {

        doctorModelList = new ArrayList<DoctorModel>();
        nearbyDoctorsAdapter = new NearbyDoctorsAdapter(NearbyDoctors.this,doctorModelList,patient);
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(nearbyDoctorsAdapter);

        firestoreRef.orderBy("name",Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentChange documentChange : value.getDocumentChanges()){
                    DoctorModel doctor = documentChange.getDocument().toObject(DoctorModel.class);
                    doctorModelList.add(doctor);
                }
                doctorModelList.sort(new Comparator<DoctorModel>() {
                    @Override
                    public int compare(DoctorModel t1, DoctorModel t2) {
                        Double distance1 = SphericalUtil.computeDistanceBetween(patient,new LatLng(t1.getLatitude(),t1.getLongitude()));
                        Double distance2 = SphericalUtil.computeDistanceBetween(patient,new LatLng(t2.getLatitude(),t2.getLongitude()));
                        return (int) (distance1-distance2);
                    }
                });
                nearbyDoctorsAdapter.notifyDataSetChanged();
            }
        });

    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = client.getLastLocation();
        Toast.makeText(this, "We recommend enabling Wi-Fi for faster results", Toast.LENGTH_SHORT).show();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                
                if(location != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

                            patient = latlng;

                            MarkerOptions options = new MarkerOptions().position(latlng)
                                    .title("My location");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,17));
                            googleMap.addMarker(options);

                            setupRecyclerView();
                        }
                    });
                } else {
                    requestLocation();
                }
            }
        });
    }

    private void requestLocation() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(6000);
        locationRequest.setFastestInterval(1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> responseTask = LocationServices.getSettingsClient(getApplicationContext())
                . checkLocationSettings(builder.build());

        responseTask.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = responseTask.getResult(ApiException.class);
                    getCurrentLocation();
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes
                                .RESOLUTION_REQUIRED :
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(NearbyDoctors.this,REQUEST_CHECK_SETTING);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes
                                .SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CHECK_SETTING) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    getCurrentLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "Please Enable Your Location to proceed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}