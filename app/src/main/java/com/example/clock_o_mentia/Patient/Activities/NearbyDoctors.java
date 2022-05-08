package com.example.clock_o_mentia.Patient.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.example.clock_o_mentia.Patient.Models.DoctorModel;
import com.example.clock_o_mentia.Patient.Adapters.NearbyDoctorsAdapter;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;

import com.example.clock_o_mentia.R;
import com.example.clock_o_mentia.databinding.ActivityNearbyDoctorsBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NearbyDoctors extends AppCompatActivity {

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    LatLng patient;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ArrayList<DoctorModel>data;
    private NearbyDoctorsAdapter nearbyDoctorsAdapter;
    private CollectionReference firestoreRef;
    private ActivityNearbyDoctorsBinding binding;

    ArrayList<DoctorModel> doctorModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNearbyDoctorsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestoreRef = FirebaseFirestore.getInstance().collection("doctors_info");
        doctorModelList = new ArrayList<DoctorModel>();
        nearbyDoctorsAdapter = new NearbyDoctorsAdapter(NearbyDoctors.this,doctorModelList);

//                Query query = firestoreRef.orderBy("name",Query.Direction.ASCENDING);
//
//                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        List<DocumentSnapshot> ds = queryDocumentSnapshots.getDocuments();
//                        for(int i = 0 ;i<ds.size();i++){
//
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
                binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(nearbyDoctorsAdapter);
        firestoreRef.orderBy("name",Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentChange documentChange : value.getDocumentChanges()){
                    doctorModelList.add(documentChange.getDocument().toObject(DoctorModel.class));

                    nearbyDoctorsAdapter.notifyDataSetChanged();
                }
            }
        });
//        initRecyclerView();
//
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        client = LocationServices.getFusedLocationProviderClient(this);


        if (ActivityCompat.checkSelfPermission(NearbyDoctors.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            getCurrentLocation();
        }
        else {
            //permission denied
            ActivityCompat.requestPermissions(NearbyDoctors.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        nearbyDoctorsAdapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        nearbyDoctorsAdapter.stopListening();
//    }

    private void getCurrentLocation() {
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
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng latlng = new LatLng(location.getLatitude(),
                                    location.getLongitude());

                            //-----read this bharvit dhua saanp -------//

                            // LatLng sample = new LatLng(28.7041,77.1025);
                            // jesa ye ek custom banaya
                            patient = latlng;
                            MarkerOptions options = new MarkerOptions().position(latlng) // yaha pe latlng ki jagah sample daal dena
                                    .title("My location");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,17)); // yaha pe bhi sample
                            googleMap.addMarker(options);
                        }
                    });
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

//    public void initRecyclerView(){
//        Query query = firestoreRef.orderBy("name",Query.Direction.ASCENDING);
//
//        FirestoreRecyclerOptions<DoctorModel> options = new FirestoreRecyclerOptions.Builder<DoctorModel>()
//                .setQuery(query, DoctorModel.class)
//                .build();
//
//        nearbyDoctorsAdapter = new NearbyDoctorsAdapter(options);
////        Log.i("retrieved",String.valueOf(nearbyDoctorsAdapter.getItemCount()));
//
//        binding.recyclerview.setHasFixedSize(true);
//        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
//        binding.recyclerview.setAdapter(nearbyDoctorsAdapter);
//    }


}