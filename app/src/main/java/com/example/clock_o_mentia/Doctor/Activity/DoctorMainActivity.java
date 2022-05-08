package com.example.clock_o_mentia.Doctor.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.clock_o_mentia.Doctor.Adapters.AppoitmentsAdapter;
import com.example.clock_o_mentia.Patient.Models.AppointmentModel;
import com.example.clock_o_mentia.R;
import com.example.clock_o_mentia.databinding.ActivityDoctorMainBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DoctorMainActivity extends AppCompatActivity {

    private ActivityDoctorMainBinding binding;

    private CollectionReference appointments;

    private List<AppointmentModel> confirmed;
    private List<AppointmentModel> awaiting;
    private String email;
    private String currDocId;
    private AppoitmentsAdapter appoitmentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("DoctorEmail",MODE_PRIVATE);
        email = sharedPreferences.getString("email",null);

        Log.i("emailId1",email);

        appointments = FirebaseFirestore.getInstance().collection("appointments").document(email.trim()).collection("apts");

        updatePending();

        setUpRecylerView();

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        appoitmentsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        appoitmentsAdapter.stopListening();
    }

    private void setUpRecylerView() {
        Query query = appointments.whereEqualTo("status",1);
        FirestoreRecyclerOptions<AppointmentModel> options  = new FirestoreRecyclerOptions.Builder<AppointmentModel>()
                .setQuery(query,AppointmentModel.class)
                .build();

        appoitmentsAdapter = new AppoitmentsAdapter(options);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(appoitmentsAdapter);
    }

    private void updatePending() {
        Query query = appointments.whereEqualTo("status",0).limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!(task.getResult().isEmpty())) {
                        Log.i("hello123","uploaded");
                        AppointmentModel appointmentModel = task.getResult().getDocuments().get(0).toObject(AppointmentModel.class);
                        binding.dateTime.setText(appointmentModel.getDateTime());
                        binding.name.setText(appointmentModel.getName());
                        currDocId = task.getResult().getDocuments().get(0).getId();
                    } else {
                        binding.name.setText("XXXXXXX");
                        binding.dateTime.setText("DD-MM-YY , HH:MM-HH:MM");
                        Toast.makeText(DoctorMainActivity.this, "List is Empty", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}