package com.example.clock_o_mentia.Patient.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.clock_o_mentia.MainActivity;
import com.example.clock_o_mentia.Patient.Models.AppointmentModel;
import com.example.clock_o_mentia.R;
import com.example.clock_o_mentia.databinding.ActivityBookAppointmentBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookAppointment extends AppCompatActivity {

    private ActivityBookAppointmentBinding binding;
    private CollectionReference appointments;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("DoctorEmail",MODE_PRIVATE);
        email = sharedPreferences.getString("email",null);
        appointments = FirebaseFirestore.getInstance().collection("appointments").document(email.trim()).collection("apts");

        binding.datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });


        binding.timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker();
            }
        });


        binding.bookAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.appointmentName.getText().toString();
                String description = binding.appointmentDesc.getText().toString();
                String date = "08-05-2022";
                String time = "11:15";

                String dateTime = date + " " + time;

                AppointmentModel appointment = new AppointmentModel(name,dateTime,0,description," ");
                appointments.add(appointment)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(BookAppointment.this, "Your Appointment has been booked!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(BookAppointment.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(BookAppointment.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void openTimePicker() {
    }

    private void openDatePicker() {
    }
}