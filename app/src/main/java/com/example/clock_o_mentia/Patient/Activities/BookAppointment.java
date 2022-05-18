package com.example.clock_o_mentia.Patient.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.clock_o_mentia.Patient.Models.AppointmentModel;
import com.example.clock_o_mentia.databinding.ActivityBookAppointmentBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class BookAppointment extends AppCompatActivity {

    private ActivityBookAppointmentBinding binding;
    private CollectionReference appointments;
    private StorageReference reports;
    private String doctorEmail;
    private String reportUrl;
    private String doctorName;

    private static final int GET_IMG_REQID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        doctorEmail = intent.getStringExtra("doctorEmail");
        doctorName = intent.getStringExtra("doctorName");

        reports = FirebaseStorage.getInstance().getReference("patient_reports");

        binding.openDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

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

        binding.uploadReportImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bookAppointmentBtn.setClickable(false);
                getImage();
            }
        });


        binding.bookAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,String> map = new HashMap<>();
                map.put("random","rand");
                FirebaseFirestore.getInstance().collection("appointments").document(doctorEmail.trim()).set(map);
                appointments = FirebaseFirestore.getInstance().collection("appointments").document(doctorEmail.trim())
                        .collection("apts");

                String name = binding.appointmentName.getText().toString();
                String description = binding.appointmentDesc.getText().toString();
                String date = binding.appointmentDate.getText().toString();
                String time = binding.appointmentTime.getText().toString();

                String dateTime = date + " " + time;

                SharedPreferences sharedPreferences = getSharedPreferences("patientEmail",MODE_PRIVATE);

                AppointmentModel appointment = new AppointmentModel(name,dateTime,0,description,reportUrl,sharedPreferences.getString("email",null),doctorName,doctorEmail);
                appointments.add(appointment)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(BookAppointment.this, "Your Appointment has been booked!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(BookAppointment.this, NearbyDoctors.class);
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

    private void getImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        resultLauncher.launch(intent);

    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        Uri uri = result.getData().getData();
                        binding.reportFileName.setText((new File(uri.getPath())).getName());
                        Toast.makeText(BookAppointment.this, "Please wait till the upload completes", Toast.LENGTH_SHORT).show();
                        uploadReport(uri);
                    }
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GET_IMG_REQID &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {

        }
    }

    private String getFileExtension(Uri imageUri){

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));

    }

    private void uploadReport(Uri uri) {

        if(uri != null) {
            StorageReference report = reports.child(System.currentTimeMillis()+"."+getFileExtension(uri));
            report.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(BookAppointment.this, "File Successfully Uploaded!", Toast.LENGTH_SHORT).show();
                            report.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    reportUrl = uri.toString();
                                    binding.bookAppointmentBtn.setClickable(true);
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(BookAppointment.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(BookAppointment.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            int progress = (int) ((100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount());
                            String percentage = progress + " %";
                            binding.uploadProgressBar.setProgress(progress);
                            binding.reportUploadProgress.setText(percentage);
                        }
                    });
        }
    }

    private void openDatePicker() {

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR,i);
                calendar.set(Calendar.MONTH,i1);
                calendar.set(Calendar.DAY_OF_MONTH,i2);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                binding.appointmentDate.setText(simpleDateFormat.format(calendar.getTime()));
                openTimePicker();
            }
        };
        new DatePickerDialog(BookAppointment.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void openTimePicker() {

    Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(Calendar.HOUR_OF_DAY,i);
                calendar.set(Calendar.MINUTE,i1);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm aa");
                binding.appointmentTime.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        new TimePickerDialog(BookAppointment.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
    }
}