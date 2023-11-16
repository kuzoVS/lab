package com.example.lab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lab.models.Experiment;

import java.util.List;

public class ExperimentAdapter extends ArrayAdapter<Experiment> {
    private Context context;
    private int resource;

    public ExperimentAdapter(Context context, int resource, List<Experiment> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        Experiment experiment = getItem(position);

        if (experiment != null) {
            TextView noteTextView = convertView.findViewById(R.id.experimentNoteTextView);
            TextView statusTextView = convertView.findViewById(R.id.experimentStatusTextView);
            TextView timeTextView = convertView.findViewById(R.id.experimentTimeTextView);

            noteTextView.setText("Note: " + experiment.getNote());
            statusTextView.setText("Status: " + experiment.getStatus());
            timeTextView.setText(experiment.getTimeInProcess());
        }

        return convertView;
    }
}