package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.Therapist;

import java.util.List;

public class TherapistAdapter extends RecyclerView.Adapter<TherapistAdapter.TherapistViewHolder> {

    private final List<Therapist> therapistList;
    private final OnTherapistClickListener listener;

    public interface OnTherapistClickListener {
        void onClick(Therapist therapist);
    }

    public TherapistAdapter(List<Therapist> therapistList, OnTherapistClickListener listener) {
        this.therapistList = therapistList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TherapistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_therapist, parent, false);
        return new TherapistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TherapistViewHolder holder, int position) {
        Therapist therapist = therapistList.get(position);
        holder.name.setText(therapist.name);
        holder.specialization.setText(therapist.specialization);

        holder.itemView.setOnClickListener(v -> listener.onClick(therapist));
    }

    @Override
    public int getItemCount() {
        return therapistList.size();
    }

    public static class TherapistViewHolder extends RecyclerView.ViewHolder {
        TextView name, specialization;

        public TherapistViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.therapistName);
            specialization = itemView.findViewById(R.id.therapistSpecialization);
        }
    }
}
