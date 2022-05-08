package com.example.clock_o_mentia.Patient.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.clock_o_mentia.Patient.Models.DoctorModel;
import com.example.clock_o_mentia.databinding.ActivityViewDoctorBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class ViewDoctor extends AppCompatActivity {

    private ActivityViewDoctorBinding binding;
    private DoctorModel doctor;

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    LatLng patient;

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
                startActivity(intent);
            }
        });
    }

    private void setViews() {
        Intent intent = getIntent();
        binding.doctorAge.setText(intent.getStringExtra("doctor_age"));
        binding.doctorName.setText(intent.getStringExtra("doctor_name"));
        binding.doctorGender.setText(intent.getStringExtra("doctor_gender"));
        binding.doctorNumber.setText(intent.getStringExtra("doctor_number"));
        binding.email.setText(intent.getStringExtra("doctor_email"));


    }
}