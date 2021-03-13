package com.example.trackie.ui.locations;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackie.R;
import com.example.trackie.Utils;
import com.example.trackie.database.FloorplanData;
import com.example.trackie.ui.Prefs;

import java.util.List;

public class LocationsAdapter extends RecyclerView.Adapter {
    private Context context;
    private static NavController navController;
    private List<FloorplanData> locationsList;

    public LocationsAdapter(Context context, Activity activity, List<FloorplanData> locationsList) {
        this.context = context;
        navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
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
                Prefs.setCurrentLocation(context, floorplanData.getName());
                Toast.makeText(context, "Location set: " + floorplanData.getName(), Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.nav_home);
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
