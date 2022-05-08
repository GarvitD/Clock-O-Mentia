package com.example.clock_o_mentia.Patient;

import com.example.clock_o_mentia.Patient.Models.DoctorModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.Comparator;

public class CustomCompare implements Comparator<DoctorModel> {
    LatLng patientAddress;
    public CustomCompare(LatLng patientAddress){this.patientAddress=patientAddress;}
    @Override
    public int compare(DoctorModel t1, DoctorModel t2) {
        Double distance1 = SphericalUtil.computeDistanceBetween(patientAddress, new LatLng(t1.getLatitude(),t1.getLongitude()));
        Double distance2 = SphericalUtil.computeDistanceBetween(patientAddress,new LatLng(t2.getLatitude(),t2.getLongitude()));
        return (int) (distance1-distance2);
    }
}
