package com.example.clock_o_mentia.Doctor.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock_o_mentia.Patient.Models.AppointmentModel;
import com.example.clock_o_mentia.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AppoitmentsAdapter extends FirestoreRecyclerAdapter<AppointmentModel,AppoitmentsAdapter.AppointmentViewHolder> {

    public AppoitmentsAdapter(@NonNull FirestoreRecyclerOptions<AppointmentModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position, @NonNull AppointmentModel model) {
        holder.dateTime.setText(model.getDateTime());
        holder.name.setText(model.getName());
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppointmentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item,parent,false));
    }

    public class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView dateTime;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            dateTime = itemView.findViewById(R.id.dateTime);
        }
    }
}
