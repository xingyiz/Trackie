package com.example.trackie.ui.testmode;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.trackie.R;
import com.example.trackie.database.FirestoreHelper;
import com.example.trackie.database.OnCompleteCallback;
import com.example.trackie.database.TestRatingData;

public class RatingDialogFragment extends DialogFragment {
    private TextView errorCountTextview;
    private TextView testTimeTakenTextview;
    private EditText ratingEditText;
    private String ratingTextProcessed;
    private Button submitRatingButton;
    private Button continueTestingButton;
    private RatingBar ratingBar;

    private int errorCount;
    private String testTimeTaken;

    public RatingDialogFragment(int numberOfErrors, String testTimeTaken) {
        super();
        this.errorCount = numberOfErrors;
        this.testTimeTaken = testTimeTaken;
        this.setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.test_rating_dialog, container, false);

        String indicated_time_taken = getString(R.string.time_taken);
        testTimeTakenTextview = v.findViewById(R.id.testing_time_taken_textview);
        testTimeTakenTextview.setText(indicated_time_taken + " " + testTimeTaken);

        String indicated_errors_text = getString(R.string.indicated_error_points);
        errorCountTextview = v.findViewById(R.id.indicated_error_points_count_textview);
        errorCountTextview.setText(errorCount + " " + indicated_errors_text);

        ratingEditText = v.findViewById(R.id.ratingText);
        submitRatingButton = v.findViewById(R.id.submit_rating_button);
        continueTestingButton = v.findViewById(R.id.continue_testing_button);
        ratingBar = v.findViewById(R.id.ratingBar);

        continueTestingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        submitRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(ratingEditText.getText().toString())) {
                    ratingTextProcessed = "NIL";
                } else {
                    ratingTextProcessed = ratingEditText.getText().toString();
                }

                FirestoreHelper.UploadRating uploadRating = new FirestoreHelper.UploadRating(getContext(),
                        new TestRatingData(ratingTextProcessed, ratingBar.getRating()));
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
