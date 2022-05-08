package com.example.clock_o_mentia.Patient.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.clock_o_mentia.R;

import java.util.ArrayList;

public class DoPatientTest extends AppCompatActivity {

    ImageView imageview;
    Bitmap imageBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_patient_test);

        imageview = findViewById(R.id.imageView5);

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    startActivityForResult(captureImageIntent, 101);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(DoPatientTest.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    //TODO Add and alert dialog box here
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    imageview.setImageBitmap(imageBitmap);
                }

    }
}