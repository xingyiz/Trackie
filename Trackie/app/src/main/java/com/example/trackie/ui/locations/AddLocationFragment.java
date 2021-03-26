package com.example.trackie.ui.locations;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.trackie.R;
import com.example.trackie.database.FloorplanHelper;
import com.example.trackie.database.OnCompleteCallback;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddLocationFragment extends Fragment {
    private EditText locationNameEditText;
    private Button uploadFloorplanButton;
    private ImageView uploadFloorplanImageView;
    private Button confirmFloorplanButton;
    private MaterialCheckBox checkBox;

    private Bitmap floorplanBitmap = null;

    private Uri filePath;

    private LocationsViewModel locationsViewModel;

    public static final int GET_FROM_GALLERY = 3;
    private static final int READ_EXTERNAL_STORAGE_REQUEST = 2;

    public AddLocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddLocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddLocationFragment newInstance(String param1, String param2) {
        AddLocationFragment fragment = new AddLocationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        locationsViewModel = new ViewModelProvider(getActivity()).get(LocationsViewModel.class);
        container.removeAllViews();
        return inflater.inflate(R.layout.fragment_add_location, container, false);
    }

    // TODO: cannot add name that already exist in database
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locationNameEditText = (EditText) view.findViewById(R.id.location_name_edittext);
        uploadFloorplanButton = (Button) view.findViewById(R.id.upload_floorplan_button);
        uploadFloorplanImageView = (ImageView) view.findViewById(R.id.upload_floorplan_imageview);
        confirmFloorplanButton = (Button) view.findViewById(R.id.confirm_floorplan_button);
        checkBox = view.findViewById(R.id.upload_floorplan_checkbox);

        uploadFloorplanImageView.setVisibility(View.INVISIBLE);
        uploadFloorplanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST);
                } else {
                    startActivityForResult(new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                }
            }
        });

        confirmFloorplanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Date currentTime = Calendar.getInstance().getTime();
                // SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                // String outputDateString = dateFormat.format(currentTime);
                // Toast.makeText(getActivity(), "Time is: " + outputDateString, Toast.LENGTH_SHORT).show();
                // set upload to database code here
                int darkmode = checkBox.isChecked() ? 1 : 0;
                FloorplanHelper.UploadFloorplan uploadFloorplan = new FloorplanHelper.UploadFloorplan(
                        locationNameEditText.getText().toString(), filePath, darkmode);
                uploadFloorplan.execute(new OnCompleteCallback() {
                    @Override
                    public void onSuccess() {
                        locationsViewModel.loadLocations();
                        Toast.makeText(getContext(), "Upload Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(getContext(), "Upload Failure", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {

                    }
                });

                getActivity().onBackPressed();

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            filePath = data.getData();
            try {
                floorplanBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                uploadFloorplanImageView.setImageBitmap(floorplanBitmap);
                uploadFloorplanImageView.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            } else {
                Toast.makeText(getActivity(), "Access Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}