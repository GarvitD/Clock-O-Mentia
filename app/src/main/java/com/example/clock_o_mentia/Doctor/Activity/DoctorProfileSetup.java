package com.example.clock_o_mentia.Doctor.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.clock_o_mentia.Patient.Models.DoctorModel;
import com.example.clock_o_mentia.R;
import com.example.clock_o_mentia.databinding.ActivityDoctorProfileSetupBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;

public class DoctorProfileSetup extends AppCompatActivity {

    private ActivityDoctorProfileSetupBinding binding;

    private FirebaseAuth mAuth;
    private StorageReference certificates;
    private StorageReference profileImages;
    private CollectionReference firestoreRef;
    private DoctorModel doctor;

    private static final int GET_IMG_REQID = 16;
    private static final String[] genders = {"Male","Female","Others"};
    private int clicked = 0;

    private String certificate_link = " ";
    private String profile_link = " ";
    private String name;
    private String email;
    private Uri profileImageUri;
    private Uri certiImageUri;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorProfileSetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.dropdown_item,genders);
        binding.genderOptions.setAdapter(arrayAdapter);

        certificates = FirebaseStorage.getInstance().getReference("doctor_certi");
        profileImages = FirebaseStorage.getInstance().getReference("doctor_profile_images");
        firestoreRef = FirebaseFirestore.getInstance().collection("doctors_info");

        mAuth = FirebaseAuth.getInstance();
        updateUI();

        doctor = new DoctorModel();

        binding.certiImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = 1;
                bringImage();
            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked=2;
                bringImage();
            }
        });

        binding.uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        binding.doctorSaveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doctor.setName(binding.doctorName.getText().toString());
                doctor.setAge(Integer.parseInt(binding.doctorAge.getText().toString()));
                doctor.setEmail(binding.doctorEmail.getText().toString());
                doctor.setGender(binding.genderOptions.getText().toString());
                doctor.setPhoneNum(binding.doctorPhone.getText().toString().trim());
                setLatLong();

                DoctorModel doctorModel = new DoctorModel(binding.doctorName.getText().toString(),
                        Integer.parseInt(binding.doctorAge.getText().toString()),
                        binding.doctorEmail.getText().toString(),
                        binding.doctorPhone.getText().toString().trim(),
                        latitude,
                        longitude,
                        binding.genderOptions.getText().toString(),
                        certificate_link,
                        profile_link);

                firestoreRef.add(doctorModel)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(DoctorProfileSetup.this, "Success", Toast.LENGTH_SHORT).show();
                                Intent intent;
                                intent = new Intent(DoctorProfileSetup.this, DoctorMainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DoctorProfileSetup.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private String getFileExtension(Uri imageUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void uploadImage() {
        if(certiImageUri != null && profileImageUri != null) {
            StorageReference certi = certificates.child(System.currentTimeMillis()+"."+getFileExtension(certiImageUri));
            StorageReference profile_img = profileImages.child(System.currentTimeMillis()+"."+getFileExtension(profileImageUri));

            certi.putFile(certiImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            certi.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    certificate_link = uri.toString();
                                }
                            });
                            profile_img.putFile(profileImageUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot1) {
                                            profile_img.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    profile_link = uri.toString();
                                                    doctor.setProfilePhoto_link(profile_link);
                                                    doctor.setCerificate_link(certificate_link);
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DoctorProfileSetup.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                            Toast.makeText(DoctorProfileSetup.this, "Please wait for upload to get over", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DoctorProfileSetup.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            Toast.makeText(DoctorProfileSetup.this, "Please wait for upload to get over", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Chose an Image!", Toast.LENGTH_SHORT).show();
        }
    }

    private void bringImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,GET_IMG_REQID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GET_IMG_REQID &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {
            if(clicked==1) {
                certiImageUri = data.getData();
                binding.certiImage.setImageURI(certiImageUri);
            }
            else if(clicked==2) {
                profileImageUri = data.getData();
                binding.profileImage.setImageURI(profileImageUri);
            }
        }
    }

    private void updateUI() {
        name = getIntent().getStringExtra("DoctorName");
        email = getIntent().getStringExtra("DoctorEmail");

        if(name == null && email == null) {
            name = mAuth.getCurrentUser().getDisplayName();
            email = mAuth.getCurrentUser().getEmail();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("DoctorEmail",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email",email);
        editor.apply();

        binding.doctorName.setText(name);
        binding.doctorEmail.setText(email);
    }

    private void setLatLong() {
        Geocoder geocoder = new Geocoder(DoctorProfileSetup.this);
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(binding.addressInput.getText().toString(),1);
            if (addressList != null) {
                latitude = addressList.get(0).getLatitude();
                longitude = addressList.get(0).getLongitude();

                doctor.setLatitude(latitude);
                doctor.setLongitude(longitude);
            }
        } catch (IOException e) {
            Toast.makeText(DoctorProfileSetup.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}