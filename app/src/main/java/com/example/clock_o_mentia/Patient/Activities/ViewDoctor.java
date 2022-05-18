package com.example.clock_o_mentia.Patient.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.clock_o_mentia.Doctor.Models.DoctorModel;
import com.example.clock_o_mentia.R;
import com.example.clock_o_mentia.databinding.ActivityViewDoctorBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewDoctor extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityViewDoctorBinding binding;
    private DoctorModel doctor;

    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient client;
    private LatLng doctorLocation;
    private String email;
    private String doctorCertiLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setViews();

        binding.bookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewDoctor.this,BookAppointment.class);
                intent.putExtra("doctorEmail",email);
                intent.putExtra("doctorName",binding.doctorName.getText().toString());
                startActivity(intent);
            }
        });

        binding.doctorCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCertificate();
            }
        });
    }

    private void showCertificate() {
        final ImagePopup imagePopup = new ImagePopup(this);

        imagePopup.setWindowHeight(800);
        imagePopup.setWindowWidth(800);
        imagePopup.setBackground(getDrawable(R.drawable.upload_image_placeholder));
        imagePopup.setFullScreen(true);
        imagePopup.setHideCloseIcon(true);
        imagePopup.setImageOnClickClose(true);
        imagePopup.initiatePopupWithGlide(doctorCertiLink);

        imagePopup.viewPopup();
    }

    private void setViews() {

        Intent intent = getIntent();
        binding.doctorAge.setText(intent.getIntExtra("doctor_age",0) + " Yrs");
        binding.doctorName.setText(intent.getStringExtra("doctor_name"));
        binding.doctorGender.setText(intent.getStringExtra("doctor_gender"));
        binding.doctorNumber.setText(intent.getStringExtra("doctor_number"));

        email = intent.getStringExtra("doctor_email");
        binding.email.setText(email);

        doctorLocation = intent.getParcelableExtra("doctor_location");
        doctorCertiLink = intent.getStringExtra("doctor_ceti_link");

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.default_profile_img)
                .centerCrop()
                .fitCenter()
                .error(R.drawable.default_profile_img);
        Glide.with(this).load(intent.getStringExtra("doctor_profile_link")).apply(requestOptions).into(binding.dcotrorProfilePhoto);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MarkerOptions markerOptions = new MarkerOptions().title("Doctor's Location").position(doctorLocation);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(doctorLocation,17));
        googleMap.addMarker(markerOptions);
    }
}