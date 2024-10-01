package com.example.thesearch.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.thesearch.LoginActivity;
import com.example.thesearch.R;
import com.example.thesearch.model.User;
import com.example.thesearch.model.UserManager;
import com.example.thesearch.repository.UserRepository;
import com.example.thesearch.viewmodel.ProfileViewModel;
import com.example.thesearch.viewmodel.SpotViewModel;
import com.example.thesearch.viewmodel.ViewModelFactory;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class ProfileFragment extends Fragment {

    private GoogleSignInClient mGoogleSignInClient;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView spotCountTextView;
    private ImageView profileImageView;
    private SpotViewModel spotViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        ViewModelFactory factory = new ViewModelFactory(new UserRepository());
        spotViewModel = new ViewModelProvider(requireActivity(), factory).get(SpotViewModel.class);

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Google Sign-In client
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build());

        // Find views
        spotCountTextView = root.findViewById(R.id.text_view_spot_count);
        nameTextView = root.findViewById(R.id.text_name);
        emailTextView = root.findViewById(R.id.text_email);
        profileImageView = root.findViewById(R.id.image_profile);
        Button logoutButton = root.findViewById(R.id.button_logout);

        // Get user information from UserManager and display it
        User user = UserManager.getInstance().getUser();
        if (user != null) {
            nameTextView.setText(user.getFirstName() + " " + user.getLastName());
            emailTextView.setText(user.getEmail());
            Glide.with(this).load(user.getPictureUrl()).into(profileImageView);
        }

        // Set up logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        // Observe the spotsLiveData to update the spot count
        spotViewModel.getSpotsLiveData().observe(getViewLifecycleOwner(), spots -> {
            if (spots != null) {
                spotCountTextView.setText("Number of spots created: " + spots.size());
            }
        });

        return root;
    }

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
            UserManager.getInstance().clearUserData(); // Clear user data on sign out
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });
    }
}
