package com.example.clock_o_mentia.Doctor.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.clock_o_mentia.Doctor.Adapters.AppoitmentsAdapter;
import com.example.clock_o_mentia.Doctor.Models.DoctorModel;
import com.example.clock_o_mentia.Patient.Models.AppointmentModel;
import com.example.clock_o_mentia.databinding.ActivityDoctorMainBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.HashMap;

import timber.log.Timber;

public class DoctorMainActivity extends AppCompatActivity implements AppoitmentsAdapter.OnItemClick, Serializable {

    private ActivityDoctorMainBinding binding;

    private CollectionReference appointments;

    private String email;
    private String currDocId;
    private AppoitmentsAdapter appoitmentsAdapter;
    private AppointmentModel appointmentInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Timber.tag("emaiilId2").i(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        SharedPreferences sharedPreferences = getSharedPreferences("DoctorEmail",MODE_PRIVATE);
        email = sharedPreferences.getString("email",null);
        Log.i("doctorEmail",email);
        appointments = FirebaseFirestore.getInstance().collection("appointments").document(email.trim()).collection("apts");

        checkIfDoctorVerified(email);

        binding.goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DoctorMainActivity.this,Doctor_Login.class));
            }
        });

        binding.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currDocId!=null) {
                    HashMap<String, Object> mp = new HashMap<String, Object>();
                    mp.put("status",-1);
                    appointments.document(currDocId).update(mp);
                    updatePending();
                }
            }
        });

        binding.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currDocId!=null) {
                    HashMap<String, Object> mp = new HashMap<String, Object>();
                    mp.put("status",1);
                    appointments.document(currDocId).update(mp);
                    updatePending();
                }
            }
        });

        binding.refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePending();
            }
        });

        binding.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAppointment();
            }
        });
    }

    private void viewAppointment() {
        Intent intent = new Intent(this,ViewAppointment.class);
        intent.putExtra("aptInfo",appointmentInfo);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpRecylerView();
    }

    private void checkIfDoctorVerified(String email) {
        CollectionReference doctors = FirebaseFirestore.getInstance().collection("doctors_info");
        Query query = doctors.whereEqualTo("email",email).limit(1);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DoctorModel ds = queryDocumentSnapshots.getDocuments().get(0).toObject(DoctorModel.class);
                if(ds.isVerified()) {
                    binding.doctorVerified.setVisibility(View.VISIBLE);
                    binding.drName.setText("Dr. "+ds.getName());
                    updatePending();
                    setUpRecylerView();
                } else {
                    binding.docNotVerified.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DoctorMainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        appoitmentsAdapter.stopListening();
    }

    private void setUpRecylerView() {
        SharedPreferences sharedPreferences = getSharedPreferences("DoctorEmail",MODE_PRIVATE);
        email = sharedPreferences.getString("email",null);
        appointments = FirebaseFirestore.getInstance().collection("appointments").document(email.trim()).collection("apts");

        Query query = appointments.whereEqualTo("status",1);
        FirestoreRecyclerOptions<AppointmentModel> options  = new FirestoreRecyclerOptions.Builder<AppointmentModel>()
                .setQuery(query,AppointmentModel.class)
                .build();

        appoitmentsAdapter = new AppoitmentsAdapter(options,this);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(appoitmentsAdapter);
        appoitmentsAdapter.startListening();
    }

    private void updatePending() {
        Query query = appointments.whereEqualTo("status",0).limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!(task.getResult().isEmpty())) {
                        AppointmentModel appointmentModel = task.getResult().getDocuments().get(0).toObject(AppointmentModel.class);
                        binding.dateTime.setText(appointmentModel.getDateTime());
                        binding.name.setText(appointmentModel.getName());
                        currDocId = task.getResult().getDocuments().get(0).getId();
                        appointmentInfo = appointmentModel;
                    } else {
                        binding.name.setText("XXXXXXX");
                        binding.dateTime.setText("DD-MM-YY , HH:MM-HH:MM");
                        Toast.makeText(DoctorMainActivity.this, "List is Empty", Toast.LENGTH_SHORT).show();
                        binding.info.setClickable(false);
                    }
                }
            }
        });
    }

    @Override
    public void itemClick(AppointmentModel appointmentModel, int position) {
        Intent intent = new Intent(this,ViewAppointment.class);
        intent.putExtra("aptInfo",appointmentModel);
        startActivity(intent);
    }
}