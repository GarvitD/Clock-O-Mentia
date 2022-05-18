package com.example.clock_o_mentia.Patient.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock_o_mentia.Patient.Models.AppointmentModel;
import com.example.clock_o_mentia.R;

import java.util.ArrayList;

public class MyAptsAdapter extends RecyclerView.Adapter<MyAptsAdapter.MyAptsViewHolder> {
    Context context;
    ArrayList<AppointmentModel> list;
    OnAptClick onAptClick;

    public MyAptsAdapter(Context context, ArrayList<AppointmentModel> list, OnAptClick onAptClick) {
        this.context = context;
        this.list = list;
        this.onAptClick = onAptClick;
    }

    @NonNull
    @Override
    public MyAptsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyAptsViewHolder(LayoutInflater.from(context).inflate(R.layout.pat_appointment_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyAptsViewHolder holder, int position) {
        holder.aptDateTime.setText(list.get(holder.getBindingAdapterPosition()).getDateTime());
        holder.doctorName.setText(list.get(holder.getBindingAdapterPosition()).getDoctorName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyAptsViewHolder extends RecyclerView.ViewHolder {
        TextView doctorName;
        TextView aptDateTime;

        public MyAptsViewHolder(@NonNull View itemView) {
            super(itemView);

            doctorName = itemView.findViewById(R.id.name);
            aptDateTime = itemView.findViewById(R.id.dateTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAptClick.showAppointment(list.get(getAbsoluteAdapterPosition()),getAbsoluteAdapterPosition());
                }
            });
        }
    }

    public interface OnAptClick {
        void showAppointment(AppointmentModel appointmentModel, int position);
    }
}
