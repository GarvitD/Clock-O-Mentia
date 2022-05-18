package com.example.clock_o_mentia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.clock_o_mentia.Doctor.Activity.DoctorMainActivity;
import com.example.clock_o_mentia.Patient.Activities.NearbyDoctors;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class VideoCallActivity extends JitsiMeetActivity {

    private boolean isPatient;

    @Override
    protected void onConferenceTerminated(HashMap<String, Object> extraData) {
//        super.onConferenceTerminated(extraData);
        JitsiMeetLogger.i("Conference terminated: " + extraData, new Object[0]);
        Log.i("workingFine","conference terminated");
        if(isPatient) {
            startActivity(new Intent(this, NearbyDoctors.class));
        } else {
            startActivity(new Intent(this, DoctorMainActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        Intent intent = getIntent();
        String meetingId = intent.getStringExtra("meeting-id");
        isPatient = intent.getBooleanExtra("isPatient",true);

        try {
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setRoom(meetingId)
                    .setAudioMuted(false)
                    .setVideoMuted(false)
                    .setAudioOnly(false)
                    .setWelcomePageEnabled(false)
                    .setConfigOverride("requireDisplayName", true)
                    .build();

            JitsiMeetActivity.launch(this,options);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}