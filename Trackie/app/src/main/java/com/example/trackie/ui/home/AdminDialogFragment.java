package com.example.trackie.ui.home;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.trackie.R;
import com.example.trackie.ui.Prefs;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class AdminDialogFragment extends DialogFragment {

    private Button adminSubmit;
    private TextInputEditText adminPIN;
    private int count = 3;
    private OnAdminModeEnabledCallback callback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enter_password_dialogfragment, container, false);
        adminSubmit = view.findViewById(R.id.admin_submit);
        adminPIN = view.findViewById(R.id.admin_pin);

        adminSubmit.setOnClickListener(v -> {
            if (adminPIN.getText().toString().equals("1234")) {
                Prefs.setAdminMode(getContext(), true);
                Toast.makeText(getContext(), "Admin Mode enabled.", Toast.LENGTH_SHORT).show();
                NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);
                TextView currentPrivs = headerView.findViewById(R.id.nav_header_currentPrivs);
                currentPrivs.setText("Current Privileges: Admin");
                callback.onAdminModeEnabled();
                dismiss();
            } else {
                --count;
                Toast.makeText(getContext(), "Wrong Pin Entered. \n " + count + " tries left.", Toast.LENGTH_LONG).show();
                if (count == 0) {
                    dismiss();
                }
            }
        });
        return view;
    }

    public void setOnAdminModeEnabledCallback(OnAdminModeEnabledCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    protected interface OnAdminModeEnabledCallback {
        void onAdminModeEnabled();
    }
}
