package com.example.clock_o_mentia.Patient.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.clock_o_mentia.Patient.Models.AppointmentModel;
import com.example.clock_o_mentia.R;
import com.example.clock_o_mentia.VideoCallActivity;
import com.example.clock_o_mentia.databinding.ActivityShowAppointmentBinding;

public class ShowAppointment extends AppCompatActivity {

    private ActivityShowAppointmentBinding binding;
    private String reportUrl;
    private AppointmentModel appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setViews();

        binding.appointmentReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReport();
            }
        });

        binding.videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowAppointment.this, VideoCallActivity.class);
                intent.putExtra("meeting-id",appointment.getUserId()+" "+appointment.getDateTime());
                intent.putExtra("isPatient",true);
                startActivity(intent);
            }
        });
    }

    private void showReport() {
        final ImagePopup imagePopup = new ImagePopup(this);

        imagePopup.setWindowHeight(800);
        imagePopup.setWindowWidth(800);
        imagePopup.setBackgroundColor(ContextCompat.getColor(this,R.color.black));
        imagePopup.setFullScreen(true);
        imagePopup.setHideCloseIcon(true);
        imagePopup.setImageOnClickClose(true);
        imagePopup.initiatePopupWithGlide(reportUrl);

        imagePopup.viewPopup();
    }

    private void setViews() {
        Intent intent = getIntent();
        appointment = (AppointmentModel) intent.getSerializableExtra("aptInfo");

        String date = getDate(appointment.getDateTime());
        String time = getTime(appointment.getDateTime());

        binding.appointmentDate.setText(date+"\n"+time);
        binding.appointmentDescp.setText(appointment.getDetailedInfo());
        binding.appointmentName.setText(appointment.getDoctorName());

        if(appointment.getStatus() == 1){
            binding.appointmentImg.setImageResource(R.drawable.calender_ticked);
            binding.appointmentStatus.setText("Booked");
        } else {
            binding.appointmentImg.setImageResource(R.drawable.waiting_icon);
            binding.appointmentStatus.setText("Awaiting");
        }

        reportUrl = appointment.getReportUrl();
    }

    private String getTime(String dateTime) {
        int length = dateTime.length();
        return dateTime.substring(10,length);
    }

    private String getDate(String dateTime) {
        return dateTime.substring(0,10);
    }
}