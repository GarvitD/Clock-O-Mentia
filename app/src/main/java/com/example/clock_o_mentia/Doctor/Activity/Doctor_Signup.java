package com.example.clock_o_mentia.Doctor.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.clock_o_mentia.databinding.ActivityDoctorSignupBinding;
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

public class Doctor_Signup extends AppCompatActivity {

    private ActivityDoctorSignupBinding binding;

    private FirebaseFirestore db;
    private CollectionReference doctors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        doctors = db.collection("auth_doctor");

        binding.doctorSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = binding.doctorPassword.getText().toString();
                String pass_cnf = binding.doctorPasswordCnf.getText().toString();

                if(!(password.equalsIgnoreCase(pass_cnf))) {
                    binding.doctorPasswordCnf.requestFocus();
                    binding.doctorPasswordCnf.setError("Passwords Do not Match!");
                } else {
                    try {
                        checkEmailValid(binding.doctorEmail.getText().toString());
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        binding.toDoctorLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Doctor_Signup.this, Doctor_Login.class);
                startActivity(intent);
            }
        });
    }

    private void checkEmailValid(String email) throws ExecutionException, InterruptedException {
        if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailExists(binding.doctorEmail.getText().toString());
        } else {
            binding.doctorEmail.requestFocus();
            binding.doctorEmail.setError("Email ID is not valid!");
        }
    }

    private void signupDoctor() {

        String email = binding.doctorEmail.getText().toString();
        String password = binding.doctorPassword.getText().toString();

        HashMap<String,Object> doctor = new HashMap<>();
        doctor.put("email",email);
        doctor.put("password",password);

        doctors
                .add(doctor)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        SharedPreferences sharedPreferences = getSharedPreferences("DoctorEmail",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email",email);
                        editor.apply();

                        Toast.makeText(Doctor_Signup.this, "success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Doctor_Signup.this, DoctorProfileSetup.class);
                        intent.putExtra("DoctorEmail",email);
                        intent.putExtra("DoctorName",binding.doctorFirstname.getText().toString().trim() + " " + binding.doctorLastname.getText().toString().trim());
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Doctor_Signup.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void emailExists(String email) throws ExecutionException, InterruptedException {
        Query userNameQuery = doctors.whereEqualTo("email",email);
        userNameQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!(task.getResult().isEmpty())) {
                        binding.doctorEmail.requestFocus();
                        binding.doctorEmail.setError("Email Already Exists! Login");
                    } else {
                        signupDoctor();
                    }
                }
            }
        });
    }
}