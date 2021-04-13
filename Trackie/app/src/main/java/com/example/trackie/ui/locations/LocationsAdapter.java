package com.example.trackie.ui.locations;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackie.R;
import com.example.trackie.database.FloorplanData;
import com.example.trackie.database.FloorplanHelper;
import com.example.trackie.database.OnCompleteCallback;
import com.example.trackie.ui.Prefs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationsAdapter extends RecyclerView.Adapter {
    private Context context;
    private Activity activity;
    private static NavController navController;
    private List<FloorplanData> locationsList;
    private List<String> locationsString = new ArrayList<>();

    private LocationsViewModel locationsViewModel;

    public LocationsAdapter(Context context, Activity activity, List<FloorplanData> locationsList) {
        this.context = context;
        this.activity = activity;
        navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        for (FloorplanData data : locationsList) {
            if (!locationsString.contains(data.getName())) {
                locationsString.add(data.getName());
            }
        }
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
        String floorplan = locationsString.get(position);
        viewHolder.locationNameTextview.setText(floorplan);

        try {
            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",
                    Locale.getDefault());
            String text = sfd.format(locationsList.get(position).getTimestamp().toDate());
            viewHolder.locationDescriptionTextview.setText("Created : " + text);
        } catch (NullPointerException e) {
            Toast.makeText(context, "Floorplan has no date.", Toast.LENGTH_SHORT).show();
        }

        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Set Location: " + floorplan + " ?")
                        .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Prefs.setCurrentLocation(context, floorplan);
                                Toast.makeText(context, "Location set: " + floorplan, Toast.LENGTH_SHORT).show();
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

                TextView dialogText = (TextView) dialog.findViewById(android.R.id.message);
                Button button1 = (Button) dialog.getWindow().findViewById(android.R.id.button1);
                Button button2 = (Button) dialog.getWindow().findViewById(android.R.id.button2);

                Typeface face = ResourcesCompat.getFont(context, R.font.opensans_reg);
                Typeface bface = ResourcesCompat.getFont(context, R.font.opensans_semibold);
                dialogText.setTypeface(face);
                button1.setTypeface(bface);
                button2.setTypeface(bface);
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
                                FloorplanHelper.RemoveFloorplan removeFloorplan = new FloorplanHelper.RemoveFloorplan(floorplan);
                                removeFloorplan.execute(new OnCompleteCallback() {
                                    @Override
                                    public void onSuccess() {
                                        locationsViewModel.loadLocations();
                                        Toast.makeText(context, "Remove Success: " + floorplan + " !", Toast.LENGTH_LONG).show();
                                        locationsList.remove(position);
                                        LocationsAdapter.this.notifyItemRemoved(position);
                                    }

                                    @Override
                                    public void onFailure() {
                                        Toast.makeText(context, "Remove Failure: " + floorplan + " !", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onError() {
                                        Toast.makeText(context, "Remove Error: " + floorplan + " !", Toast.LENGTH_LONG).show();
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

        viewHolder.overflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, viewHolder.overflowButton);
                //inflating menu from xml resource
                popup.inflate(R.menu.locations_overflow_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.locmenu_edit:

                                return true;
                            case R.id.locmenu_delete:

                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        public View buttonViewOption;
        ConstraintLayout container;
        TextView locationNameTextview;
        TextView locationDescriptionTextview;
        ImageView overflowButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationNameTextview = itemView.findViewById(R.id.location_name_textview);
            locationDescriptionTextview = itemView.findViewById(R.id.location_description_textview);
            overflowButton = itemView.findViewById(R.id.location_options_button);
            container = itemView.findViewById(R.id.location_recycler_layout_container);
        }
    }

    @Override
    public int getItemCount() {
        if (locationsString == null) return -1;
        return locationsString.size();
    }
}
