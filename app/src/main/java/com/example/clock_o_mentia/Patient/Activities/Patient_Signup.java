package com.example.clock_o_mentia.Patient.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.clock_o_mentia.databinding.ActivityPatientSignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class Patient_Signup extends AppCompatActivity {

    private ActivityPatientSignupBinding binding;
    private FirebaseFirestore db;
    private CollectionReference patients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        patients = db.collection("auth_patient");

        binding.patientSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = binding.patientPass.getText().toString();
                String pass_cnf = binding.patientPassCnf.getText().toString();

                if(!(password.equalsIgnoreCase(pass_cnf))) {
                    binding.patientPassCnf.requestFocus();
                    binding.patientPassCnf.setError("Passwords Do not Match!");
                } else {
                    try {
                        checkEmailValid(binding.patientEmail.getText().toString());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        binding.toPatientLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Patient_Signup.this, Patient_Login.class);
                startActivity(intent);
            }
        });
    }

    private void checkEmailValid(String email) throws ExecutionException, InterruptedException {
        if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userNameExists(binding.patientUserSignup.getText().toString());
        } else {
            binding.patientEmail.requestFocus();
            binding.patientEmail.setError("Email ID is not valid!");
        }
    }

    private void signupPatient() {

        String userName = binding.patientUserSignup.getText().toString();
        String password = binding.patientPass.getText().toString();

        HashMap<String,Object> patient = new HashMap<>();
        patient.put("userName",userName);
        patient.put("password",password);

        patients
                .add(patient)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(Patient_Signup.this, "success", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Patient_Signup.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void userNameExists(String userName) throws ExecutionException, InterruptedException {
        Query userNameQuery = patients.whereEqualTo("userName",userName);
        userNameQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!(task.getResult().isEmpty())) {
                        binding.patientUserSignup.requestFocus();
                        binding.patientUserSignup.setError("User Name Already Exists! Login");
                    } else {
                        signupPatient();
                    }
                }
            }
        });
    }
}