package com.example.clock_o_mentia.Patient.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.clock_o_mentia.Patient.Activities.ViewDoctor;
import com.example.clock_o_mentia.Doctor.Models.DoctorModel;
import com.example.clock_o_mentia.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

public class NearbyDoctorsAdapter extends RecyclerView.Adapter<NearbyDoctorsAdapter.NearbyViewHolder> {

    Context context;
    ArrayList<DoctorModel> list;
    com.google.android.gms.maps.model.LatLng patient;

    public NearbyDoctorsAdapter(Context context, ArrayList<DoctorModel> list, LatLng patient) {
        this.context = context;
        this.list = list;
        this.patient = patient;
    }

    @NonNull
    @Override
    public NearbyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.nearby_doctors_item,parent,false);
        return new NearbyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyViewHolder holder, int position) {
        DoctorModel doctorModel = list.get(holder.getBindingAdapterPosition());

        RequestOptions requestOptions = new RequestOptions()
                .centerCrop()
                        .fitCenter()
                                .placeholder(R.drawable.default_profile_img)
                                        .error(R.drawable.default_profile_img);
        Glide.with(context).load(doctorModel.getProfilePhoto_link()).apply(requestOptions).into(holder.imageview);
        holder.textname.setText(doctorModel.getName());
        holder.txt.setText(doctorModel.getPhoneNum());
        holder.textdist.setText(""+String.format("%.2f",(SphericalUtil.computeDistanceBetween(patient,new LatLng(doctorModel.getLatitude(),doctorModel.getLongitude())))/(double) 1000.0) + " Km");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewDoctor.class);
                intent.putExtra("doctor_age",list.get(holder.getBindingAdapterPosition()).getAge());
                intent.putExtra("doctor_name",list.get(holder.getBindingAdapterPosition()).getName());
                intent.putExtra("doctor_gender",list.get(holder.getBindingAdapterPosition()).getGender());
                intent.putExtra("doctor_number",list.get(holder.getBindingAdapterPosition()).getPhoneNum());
                intent.putExtra("doctor_email",list.get(holder.getBindingAdapterPosition()).getEmail());
                intent.putExtra("doctor_ceti_link",list.get(holder.getBindingAdapterPosition()).getCerificate_link());
                intent.putExtra("doctor_profile_link",list.get(holder.getBindingAdapterPosition()).getProfilePhoto_link());
                intent.putExtra("doctor_location",new LatLng(list.get(holder.getBindingAdapterPosition()).getLatitude(),list.get(holder.getBindingAdapterPosition()).getLongitude()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NearbyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;
        TextView textname;
        TextView textdist;
        TextView txt;
        public NearbyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageview=itemView.findViewById(R.id.doctor_profile_item);
            textname=itemView.findViewById(R.id.doctor_item_name);
            textdist=itemView.findViewById(R.id.doctor_item_distance);
            txt = itemView.findViewById(R.id.doctor_item_phoneNum);
        }
    }
}
