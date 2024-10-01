package com.example.thesearch.view;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.thesearch.R;
import com.example.thesearch.model.UserManager;
import com.example.thesearch.model.Resource;
import com.example.thesearch.model.Spot;
import com.example.thesearch.model.SpotResponse;
import com.example.thesearch.repository.UserRepository;
import com.example.thesearch.viewmodel.SpotViewModel;
import com.example.thesearch.viewmodel.ViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddSurfSpotFragment extends BottomSheetDialogFragment {

    private static final String TAG = "AddSurfSpotFragment"; // Define the TAG constant

    // Fragment initialization parameters
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";

    private SpotViewModel spotViewModel;
    private EditText nameEditText, descriptionEditText;
    private RatingBar difficultyRatingBar, qualityRatingBar;
    private Button saveButton;
    private double latitude, longitude;

    public AddSurfSpotFragment() {
        // Required empty public constructor
    }

    public static AddSurfSpotFragment newInstance(Double lat, Double lon) {
        AddSurfSpotFragment fragment = new AddSurfSpotFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LATITUDE, lat);
        args.putDouble(ARG_LONGITUDE, lon);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_surf_spot, container, false);

        // Initialize ViewModel
        UserRepository userRepository = new UserRepository();
        ViewModelFactory factory = new ViewModelFactory(userRepository);
        spotViewModel = new ViewModelProvider(requireActivity(), factory).get(SpotViewModel.class);

        nameEditText = view.findViewById(R.id.edit_text_name);
        descriptionEditText = view.findViewById(R.id.edit_text_description);
        difficultyRatingBar = view.findViewById(R.id.rating_bar_difficulty);
        qualityRatingBar = view.findViewById(R.id.rating_bar_quality);
        saveButton = view.findViewById(R.id.button_save);

        if (getArguments() != null) {
            latitude = getArguments().getDouble(ARG_LATITUDE);
            longitude = getArguments().getDouble(ARG_LONGITUDE);
            Log.d(TAG, String.format("AddSurfSpotFragment::onCreateView() Lat: %f, Lon: %f", latitude, longitude));
        }

        // Handle Enter key press in the name field
        nameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_NEXT ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(nameEditText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        // Observe the result of the spot creation
        spotViewModel.getSpotCreationStatus().observe(getViewLifecycleOwner(), new Observer<Resource<SpotResponse>>() {
            @Override
            public void onChanged(Resource<SpotResponse> resource) {
                if (resource.status == Resource.Status.SUCCESS) {
                    // Spot created successfully, update UI
                    Toast.makeText(getContext(), "Spot created successfully!", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else if (resource.status == Resource.Status.ERROR) {
                    // Handle failure
                    Log.d(TAG, String.format("Spot failed to be created."));
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                int difficultyRating = (int) difficultyRatingBar.getRating();
                int qualityRating = (int) qualityRatingBar.getRating();

                Spot spot = new Spot();
                spot.setName(name);
                spot.setDescription(description);
                spot.setLatitude(latitude);
                spot.setLongitude(longitude);
                spot.setQualityRating(qualityRating);
                spot.setDifficultyRating(difficultyRating);
                spot.setCreator(UserManager.getInstance().getUserId());

                // Call createSpot in the ViewModel
                spotViewModel.createSpot(spot);
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
