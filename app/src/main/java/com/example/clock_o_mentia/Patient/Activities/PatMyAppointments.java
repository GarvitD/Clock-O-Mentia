package com.example.clock_o_mentia.Patient.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.clock_o_mentia.Patient.Adapters.MyAptsAdapter;
import com.example.clock_o_mentia.Patient.Models.AppointmentModel;
import com.example.clock_o_mentia.databinding.ActivityPatMyAppointmentsBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;

public class PatMyAppointments extends AppCompatActivity implements MyAptsAdapter.OnAptClick, Serializable {

    private ActivityPatMyAppointmentsBinding binding;
    private ArrayList<AppointmentModel> myAppointments;
    private CollectionReference allAppointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatMyAppointmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        allAppointments = FirebaseFirestore.getInstance().collection("appointments");
        myAppointments = new ArrayList<>();

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        String userId = getSharedPreferences("patientEmail",MODE_PRIVATE).getString("email","null");

//        FirebaseFirestore.getInstance().collection("appointments").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                for (DocumentChange documentChange : value.getDocumentChanges()) {
//                    AppointmentModel appointmentModel = documentChange.getDocument().toObject(AppointmentModel.class);
//                    if(appointmentModel.getUserId().equalsIgnoreCase(userId)) myAppointments.add(appointmentModel);
//                }
//                Log.i("listSize",String.valueOf(myAppointments.size()));
//                binding.recyclerView2.setHasFixedSize(true);
//                binding.recyclerView2.setLayoutManager(new LinearLayoutManager(PatMyAppointments.this));
//                MyAptsAdapter adapter = new MyAptsAdapter(PatMyAppointments.this,myAppointments,PatMyAppointments.this);
//                binding.recyclerView2.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
//            }
//        });
        FirebaseFirestore.getInstance().collection("appointments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    documentChange.getDocument().getReference().collection("apts").whereEqualTo("userId", userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value1, @Nullable FirebaseFirestoreException error) {
                            for (DocumentChange doc : value1.getDocumentChanges()) {
                                AppointmentModel appointmentModel = doc.getDocument().toObject(AppointmentModel.class);
                                myAppointments.add(appointmentModel);
                            }
                            Log.i("listSize", String.valueOf(myAppointments.size()));
                            binding.recyclerView2.setHasFixedSize(true);
                            binding.recyclerView2.setLayoutManager(new LinearLayoutManager(PatMyAppointments.this));
                            MyAptsAdapter adapter = new MyAptsAdapter(PatMyAppointments.this, myAppointments, PatMyAppointments.this);
                            binding.recyclerView2.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
//                    Log.i("document_names",documentChange.getDocument().getId());
//                    documentChange.getDocument().getReference().collection("apts").whereEqualTo("userId",userId).get()
//                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                @Override
//                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                    Log.i("listSize", String.valueOf(queryDocumentSnapshots.getDocuments().size()));
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(PatMyAppointments.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                    }
//                    Date currDate = new Date();
//                    Date appointmentDate = new Date();
//                    try {
//                        appointmentDate = new SimpleDateFormat("DD-mm-YYYY HH:mm aa").parse(appointmentModel.getDateTime());
//                    } catch (ParseException e) {
//                        Toast.makeText(PatMyAppointments.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//        });
    });
    }

    @Override
    public void showAppointment(AppointmentModel appointmentModel, int position) {
        Intent intent = new Intent(this,ShowAppointment.class);
        intent.putExtra("aptInfo",appointmentModel);
        startActivity(intent);
    }
}