package com.example.trackie.ui.testmode;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.trackie.R;
import com.example.trackie.database.FirestoreHelper;
import com.example.trackie.database.OnCompleteCallback;
import com.example.trackie.database.RatingData;

public class RatingDialogFragment extends DialogFragment {
    private EditText ratingText;
    private String ratingTextProcessed;
    private Button ratingSubmit;
    private RatingBar ratingBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.rating_dialog, container, false);

        ratingText = v.findViewById(R.id.ratingText);
        ratingSubmit = v.findViewById(R.id.ratingSubmit);
        ratingBar = v.findViewById(R.id.ratingBar);

        ratingSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(ratingText.getText().toString())) {
                    ratingTextProcessed = "NIL";
                } else {
                    ratingTextProcessed = ratingText.getText().toString();
                }

                FirestoreHelper.UploadRating uploadRating = new FirestoreHelper.UploadRating(getContext(),
                        new RatingData(ratingTextProcessed, ratingBar.getRating()));
                uploadRating.execute(new OnCompleteCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getContext(), "You rated " + ratingBar.getRating() + " stars!", Toast.LENGTH_SHORT).show();
                        dismiss();
                        getActivity().onBackPressed();
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(getContext(), "Upload Failure :(", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getContext(), "An error occurred!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        return v;
    }
}
