package com.example.trackie.ui.locations;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackie.R;
import com.example.trackie.Utils;
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
        System.out.println("Name: " + floorplanData.getName());

        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.P_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Utils.CURRENT_LOCATION_KEY, floorplanData.getName());
                editor.apply();
            }
        });
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout container;
        TextView locationNameTextview;
        TextView locationDescriptionTextview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationNameTextview = itemView.findViewById(R.id.location_name_textview);
            locationDescriptionTextview = itemView.findViewById(R.id.location_description_textview);

            container = itemView.findViewById(R.id.location_recycler_layout_container);
        }
    }

    @Override
    public int getItemCount() {
        return locationsList.size();
    }
}
