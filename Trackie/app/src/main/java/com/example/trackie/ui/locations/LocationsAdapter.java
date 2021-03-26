package com.example.trackie.ui.locations;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackie.R;
import com.example.trackie.Utils;
import com.example.trackie.database.FloorplanData;
import com.example.trackie.database.FloorplanHelper;
import com.example.trackie.database.OnCompleteCallback;
import com.example.trackie.ui.Prefs;

import java.util.List;

public class LocationsAdapter extends RecyclerView.Adapter {
    private Context context;
    private Activity activity;
    private static NavController navController;
    private List<FloorplanData> locationsList;

    private LocationsViewModel locationsViewModel;

    public LocationsAdapter(Context context, Activity activity, List<FloorplanData> locationsList) {
        this.context = context;
        this.activity = activity;
        navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        this.locationsList = locationsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View locationView = layoutInflater.inflate(R.layout.locations_recycler_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(locationView);
        locationsViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(LocationsViewModel.class);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        FloorplanData floorplanData = locationsList.get(position);
        viewHolder.locationNameTextview.setText(floorplanData.getName());

        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Set Location: " + floorplanData.getName() + " ?")
                        .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Prefs.setCurrentLocation(context, floorplanData.getName());
                                Toast.makeText(context, "Location set: " + floorplanData.getName(), Toast.LENGTH_SHORT).show();
                                navController.navigate(R.id.nav_home);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        viewHolder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Confirm Delete?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FloorplanHelper.RemoveFloorplan removeFloorplan = new FloorplanHelper.RemoveFloorplan(floorplanData.getName());
                                removeFloorplan.execute(new OnCompleteCallback() {
                                    @Override
                                    public void onSuccess() {
                                        locationsViewModel.loadLocations();
                                        Toast.makeText(context, "Remove Success: " + floorplanData.getName() + " !", Toast.LENGTH_LONG).show();
                                        locationsList.remove(position);
                                        LocationsAdapter.this.notifyItemRemoved(position);
                                    }

                                    @Override
                                    public void onFailure() {
                                        Toast.makeText(context, "Remove Failure: " + floorplanData.getName() + " !", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onError() {
                                        Toast.makeText(context, "Remove Error: " + floorplanData.getName() + " !", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
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
        if (locationsList == null) return -1;
        return locationsList.size();
    }
}
