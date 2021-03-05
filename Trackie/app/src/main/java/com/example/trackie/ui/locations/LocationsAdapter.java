package com.example.trackie.ui.locations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackie.R;
import com.example.trackie.database.FloorplanData;
import com.example.trackie.database.MapData;

import java.util.ArrayList;
import java.util.List;

public class LocationsAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<FloorplanData> locationsList;

    public LocationsAdapter(Context context, List<FloorplanData> locationsList) {
        this.context = context;
        this.locationsList = locationsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View locationView = layoutInflater.inflate(R.layout.locations_recycler_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(locationView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        FloorplanData floorplanData = locationsList.get(position);
        viewHolder.locationNameTextview.setText(floorplanData.getName());
        System.out.println("Nameee: " + floorplanData.getName());
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView locationNameTextview;
        TextView locationDescriptionTextview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationNameTextview = itemView.findViewById(R.id.location_name_textview);
            locationDescriptionTextview = itemView.findViewById(R.id.location_description_textview);
        }
    }

    @Override
    public int getItemCount() {
        return locationsList.size();
    }
}
