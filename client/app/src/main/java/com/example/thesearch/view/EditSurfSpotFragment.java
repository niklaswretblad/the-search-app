package com.example.thesearch.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.thesearch.R;
import com.example.thesearch.model.Resource;
import com.example.thesearch.model.Spot;
import com.example.thesearch.model.SpotResponse;
import com.example.thesearch.repository.UserRepository;
import com.example.thesearch.viewmodel.SpotViewModel;
import com.example.thesearch.viewmodel.ViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EditSurfSpotFragment extends BottomSheetDialogFragment {
    private static final String TAG = "EditSurfSpotFragment";
    private static final String ARG_SURF_SPOT = "surf_spot";
    private EditText nameEditText, descriptionEditText;
    private RatingBar difficultyRatingBar, qualityRatingBar;
    private Button saveButton, deleteButton;
    private Spot spot;
    private SpotViewModel spotViewModel;

    public static EditSurfSpotFragment newInstance(Spot spot) {
        EditSurfSpotFragment fragment = new EditSurfSpotFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SURF_SPOT, spot);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_surf_spot, container, false);

        // Initialize ViewModel
        UserRepository userRepository = new UserRepository();
        ViewModelFactory factory = new ViewModelFactory(userRepository);
        spotViewModel = new ViewModelProvider(requireActivity(), factory).get(SpotViewModel.class);

        nameEditText = view.findViewById(R.id.edit_text_name);
        descriptionEditText = view.findViewById(R.id.edit_text_description);
        difficultyRatingBar = view.findViewById(R.id.rating_bar_difficulty);
        qualityRatingBar = view.findViewById(R.id.rating_bar_quality);
        saveButton = view.findViewById(R.id.button_save);
        deleteButton = view.findViewById(R.id.button_delete);  // New delete button

        if (getArguments() != null) {
            spot = (Spot) getArguments().getSerializable(ARG_SURF_SPOT);
            if (spot != null) {
                nameEditText.setText(spot.getName());
                descriptionEditText.setText(spot.getDescription());
                difficultyRatingBar.setRating(spot.getDifficultyRating());
                qualityRatingBar.setRating(spot.getQualityRating());
            }
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spot != null) {
                    spot.setName(nameEditText.getText().toString());
                    spot.setDescription(descriptionEditText.getText().toString());
                    spot.setDifficultyRating((int) difficultyRatingBar.getRating());
                    spot.setQualityRating((int) qualityRatingBar.getRating());

                    spotViewModel.updateSpot(spot);
                }
            }
        });

        // Handle delete button click
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spot != null) {
                    spotViewModel.removeSpot(spot.getId());
                }
            }
        });

        // Observe the result of the spot creation
        spotViewModel.getSpotEditStatus().observe(getViewLifecycleOwner(), new Observer<Resource<SpotResponse>>() {
            @Override
            public void onChanged(Resource<SpotResponse> resource) {
                if (resource != null && resource.status == Resource.Status.SUCCESS) {
                    // Spot created successfully, update UI
                    Toast.makeText(getContext(), "Spot edited successfully!", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else if (resource != null && resource.status == Resource.Status.ERROR) {
                    // Handle failure
                    Log.d(TAG, String.format("Failed to edit spot."));
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Observe the result of the spot creation
        spotViewModel.getSpotRemovalStatus().observe(getViewLifecycleOwner(), new Observer<Resource<SpotResponse>>() {
            @Override
            public void onChanged(Resource<SpotResponse> resource) {
                if (resource != null && resource.status == Resource.Status.SUCCESS) {
                    // Spot created successfully, update UI
                    Toast.makeText(getContext(), "Spot removed successfully!", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else if (resource != null && resource.status == Resource.Status.ERROR) {
                    // Handle failure
                    Log.d(TAG, String.format("Failed to remove spot."));
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        android.app.Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }
}
