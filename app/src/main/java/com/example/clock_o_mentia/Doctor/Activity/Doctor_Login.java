package com.example.clock_o_mentia.Doctor.Activity;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.clock_o_mentia.Patient.Activities.Patient_Login;
import com.example.clock_o_mentia.R;
import com.example.clock_o_mentia.databinding.ActivityDoctorLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class Doctor_Login extends AppCompatActivity {

    private ActivityDoctorLoginBinding binding;
    private FirebaseFirestore db;
    private CollectionReference doctors;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private CollectionReference firestoreRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        doctors = db.collection("auth_doctor");
        firestoreRef = FirebaseFirestore.getInstance().collection("doctors_info");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        binding.googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        binding.doctorLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.doctorLoginEmail.getText().toString();
                checkEmail(email);
            }
        });

        binding.toPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Doctor_Login.this, Patient_Login.class);
                startActivity(intent);
                finish();
            }
        });

        binding.toDoctorRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Doctor_Login.this, Doctor_Signup.class);
                startActivity(intent);
            }
        });
    }

    private void checkEmail(String email) {
        Query userNameQuery = doctors.whereEqualTo("email",email);
        userNameQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().isEmpty()) {
                        binding.doctorLoginEmail.requestFocus();
                        binding.doctorLoginEmail.setError("User Not Found! Signup");
                    } else {
                        checkpassword((String) task.getResult().getDocuments().get(0).getData().get("password"));
                    }
                }
            }
        });
    }

    private void checkpassword(String password) {
        if(password.equalsIgnoreCase(binding.doctorLoginPass.getText().toString())) {
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        } else {
            binding.doctorLoginPass.requestFocus();
            binding.doctorLoginPass.setError("Password is Incorrect!");
        }
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult.launch(signInIntent);
    }

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                handleSignInResult(task);
            }
        }
    });

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
//            Toast.makeText(Doctor_Login.this, "failed", Toast.LENGTH_SHORT).show();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(Doctor_Login.this, "failed", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Doctor_Login.this, "Sign In Success", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "signInWithCredential:success");
                            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            Log.i("emailId",email);

                            Query query = FirebaseFirestore.getInstance().collection("doctors_info").whereEqualTo("email",email).limit(1);
                            query.get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    Intent intent;
                                                    if(queryDocumentSnapshots.getDocuments().size() != 0) {
                                                        intent = new Intent(Doctor_Login.this, DoctorMainActivity.class);
                                                        SharedPreferences sharedPreferences = getSharedPreferences("DoctorEmail",MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putString("email",email);
                                                        editor.apply();
                                                    } else {
                                                        intent = new Intent(Doctor_Login.this, DoctorProfileSetup.class);
                                                    }
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(Doctor_Login.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                        } else {
                            Toast.makeText(Doctor_Login.this, "failed", Toast.LENGTH_SHORT).show();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                        // ...
                    }
                });
    }
}