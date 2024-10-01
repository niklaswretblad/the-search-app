package com.example.thesearch.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.thesearch.R;
import com.example.thesearch.model.User;
import com.example.thesearch.model.UserManager;
import com.example.thesearch.viewmodel.UserProfileViewModel;

public class UserProfileFragment extends Fragment {

    private UserProfileViewModel userProfileViewModel;
    private ImageView profileImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView spotCountTextView;
    private Button followButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Initialize views
        profileImageView = view.findViewById(R.id.image_profile);
        nameTextView = view.findViewById(R.id.text_name);
        emailTextView = view.findViewById(R.id.text_email);
        spotCountTextView = view.findViewById(R.id.text_view_spot_count);
        followButton = view.findViewById(R.id.button_follow);

        // Initialize ViewModel
        userProfileViewModel = new ViewModelProvider(requireActivity()).get(UserProfileViewModel.class);
        userProfileViewModel.getUserProfile().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    // Update UI with user information
                    updateUI(user);
                }
            }
        });

        // Set up the follow button
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = userProfileViewModel.getUserProfile().getValue();
                userProfileViewModel.toggleFollowUser(UserManager.getInstance().getUserId(), user.getId());
                followButton.setEnabled(false); // Disable button to prevent multiple clicks
            }
        });

        // Observe follow status
        userProfileViewModel.getFollowStatus().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isFollowing) {
                followButton.setEnabled(true); // Re-enable the button
                if (isFollowing) {
                    followButton.setText("Unfollow");
                    Toast.makeText(getContext(), "You are now following this user", Toast.LENGTH_SHORT).show();
                } else {
                    followButton.setText("Follow");
                    Toast.makeText(getContext(), "You have unfollowed this user", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Back button functionality
        Button backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.popBackStack();  // Navigate back to the previous fragment
            }
        });

        return view;
    }

    private void updateUI(User user) {
        nameTextView.setText(user.getFirstName() + " " + user.getLastName());
        emailTextView.setText(user.getEmail());

        // Load profile image using Glide
        Glide.with(this).load(user.getPictureUrl()).into(profileImageView);

        // Update the number of spots created
        spotCountTextView.setText("Number of spots created: " + user.getSpots().size());

        // Update the follow button text based on follow status
        userProfileViewModel.checkIfFollowing(UserManager.getInstance().getUserId(), user.getId());
    }
}
