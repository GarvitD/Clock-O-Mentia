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

import com.example.clock_o_mentia.Patient.Activities.ViewDoctor;
import com.example.clock_o_mentia.Patient.Models.DoctorModel;
import com.example.clock_o_mentia.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class NearbyDoctorsAdapter extends RecyclerView.Adapter<NearbyDoctorsAdapter.NearbyViewHolder> {

    Context context;
    ArrayList<DoctorModel> list;

    public NearbyDoctorsAdapter(Context context, ArrayList<DoctorModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NearbyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.nearby_doctors_item,parent,false);
        return new NearbyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyViewHolder holder, int position) {
        holder.imageview.setImageResource(R.drawable.default_profile_img);
        holder.textname.setText(list.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewDoctor.class);
                holder.getAdapterPosition();
                intent.putExtra("doctor_age",list.get(position).getAge());
                intent.putExtra("doctor_name",list.get(position).getName());
                intent.putExtra("doctor_gender",list.get(position).getGender());
                intent.putExtra("doctor_number",list.get(position).getPhoneNum());
                intent.putExtra("doctor_email",list.get(position).getEmail());
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
            imageview=itemView.findViewById(R.id.imageview1);
            textname=itemView.findViewById(R.id.textview);
            textdist=itemView.findViewById(R.id.distance);
            txt = itemView.findViewById(R.id.textView2);
        }
    }
}
