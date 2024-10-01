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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.thesearch.R;
import com.example.thesearch.model.Comment;
import com.example.thesearch.model.CommentRequest;
import com.example.thesearch.model.CommentResponse;
import com.example.thesearch.model.Like;
import com.example.thesearch.model.Spot;
import com.example.thesearch.model.UserManager;
import com.example.thesearch.repository.UserRepository;
import com.example.thesearch.viewmodel.CommentViewModel;
import com.example.thesearch.viewmodel.SpotViewModel;
import com.example.thesearch.viewmodel.ViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewSurfSpotFragment extends BottomSheetDialogFragment {

    private static final String TAG = "ViewSurfSpotFragment";
    private static final String ARG_SPOT = "spot";
    private static final String ARG_IS_OWNED = "is_owned";

    private TextView nameTextView, descriptionTextView, difficultyRatingTextView, qualityRatingTextView, likeCountTextView, ownerEmailTextView;
    private Button editButton, postCommentButton;
    private ToggleButton likeButton;
    private EditText commentEditText;
    private LinearLayout commentsLinearLayout;
    private Spot spot;
    private SpotViewModel spotViewModel;
    private CommentViewModel commentViewModel;
    private int likeCount;
    private boolean isOwnedSpot;
    private List<Comment> currentComments;

    public static ViewSurfSpotFragment newInstance(Spot spot, boolean isOwnedSpot) {
        ViewSurfSpotFragment fragment = new ViewSurfSpotFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SPOT, spot);
        args.putBoolean(ARG_IS_OWNED, isOwnedSpot);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_surf_spot, container, false);

        UserRepository userRepository = new UserRepository();
        ViewModelFactory factory = new ViewModelFactory(userRepository);
        spotViewModel = new ViewModelProvider(requireActivity(), factory).get(SpotViewModel.class);
        commentViewModel = new ViewModelProvider(requireActivity(), factory).get(CommentViewModel.class);

        nameTextView = view.findViewById(R.id.text_view_name);
        descriptionTextView = view.findViewById(R.id.text_view_description);
        difficultyRatingTextView = view.findViewById(R.id.text_view_difficulty_rating);
        qualityRatingTextView = view.findViewById(R.id.text_view_quality_rating);
        likeCountTextView = view.findViewById(R.id.text_view_like_count);
        editButton = view.findViewById(R.id.button_edit);
        likeButton = view.findViewById(R.id.button_like);
        commentEditText = view.findViewById(R.id.edit_text_comment);
        postCommentButton = view.findViewById(R.id.button_post_comment);
        commentsLinearLayout = view.findViewById(R.id.linear_layout_comments);

        currentComments = new ArrayList<>();

        if (getArguments() != null) {
            spot = (Spot) getArguments().getSerializable(ARG_SPOT);
            isOwnedSpot = getArguments().getBoolean(ARG_IS_OWNED, true);

            if (spot != null) {
                nameTextView.setText(spot.getName());
                descriptionTextView.setText(spot.getDescription());
                difficultyRatingTextView.setText(String.valueOf(spot.getDifficultyRating()));
                qualityRatingTextView.setText(String.valueOf(spot.getQualityRating()));

                likeCount = spot.getLikes().size();
                likeCountTextView.setText(String.valueOf(likeCount) + " likes");

                String userId = UserManager.getInstance().getUserId();
                for (Like like: spot.getLikes()) {
                    if (like.getUserId().equals(userId)) {
                        likeButton.setChecked(true);
                    }
                }

                // Hide or disable editing functionality
                if (!isOwnedSpot) {
                    editButton.setVisibility(View.GONE);
                }

                loadComments(spot.getId());
            }
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spot != null && isOwnedSpot) {
                    EditSurfSpotFragment editSurfSpotDialogFragment = EditSurfSpotFragment.newInstance(spot);
                    editSurfSpotDialogFragment.show(getParentFragmentManager(), "EditSurfSpotDialogFragment");
                    dismiss();
                }
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spot != null) {
                    boolean isCurrentlyLiked = likeButton.isChecked();
                    spotViewModel.toggleLikeSpot(UserManager.getInstance().getUserId(), spot.getId(), !isCurrentlyLiked);
                }
            }
        });

        spotViewModel.getLikeToggleStatus().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLiked) {
                if ((likeButton.isChecked() && isLiked)) {
                    Toast.makeText(getContext(), "Like successful", Toast.LENGTH_SHORT).show();
                    likeCount++;
                } else if (!likeButton.isChecked() && !isLiked) {
                    Toast.makeText(getContext(), "Unlike successful", Toast.LENGTH_SHORT).show();
                    likeCount--;
                } else {
                    likeButton.setChecked(!likeButton.isChecked());
                    Toast.makeText(getContext(), "Like failed", Toast.LENGTH_SHORT).show();
                }

                likeCountTextView.setText(String.valueOf(likeCount) + " likes");
            }
        });

        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spot != null) {
                    String commentText = commentEditText.getText().toString();
                    if (!commentText.isEmpty()) {
                        CommentRequest request = new CommentRequest(commentText, UserManager.getInstance().getUserId());
                        commentViewModel.commentSpot(spot.getId(), request, new Callback<CommentResponse>() {
                            @Override
                            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    // Add comment to the view
                                    String userId = UserManager.getInstance().getUserId();
                                    String userName = UserManager.getInstance().getUser().getFirstName() + " " + UserManager.getInstance().getUser().getLastName();
                                    Comment comment = new Comment(0, userId, commentText, spot.getId(), new Date());
                                    addComment(comment);
                                    commentEditText.setText("");
                                } else {
                                    Toast.makeText(getContext(), "Failed to add comment", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<CommentResponse> call, Throwable t) {
                                Toast.makeText(getContext(), "Failed to add comment", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

        return view;
    }

    private void loadComments(int spotId) {
        String token = UserManager.getInstance().getUserId();
        commentViewModel.getSpotComments(spotId, token, new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentComments = response.body();
                    displayComments();
                } else {
                    Log.e(TAG, "Failed to load comments");
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e(TAG, "Error loading comments", t);
            }
        });
    }

    private void displayComments() {
        commentsLinearLayout.removeAllViews(); // Clear any existing comments

        for (Comment comment : currentComments) {
            View commentView = LayoutInflater.from(getContext()).inflate(R.layout.item_comment, commentsLinearLayout, false);

            TextView commentTextView = commentView.findViewById(R.id.text_view_comment);
            TextView timestampTextView = commentView.findViewById(R.id.text_view_timestamp);

            commentTextView.setText(comment.getText());
            timestampTextView.setText(comment.getFormattedTimestamp());

            commentsLinearLayout.addView(commentView);
        }
    }

    private void addComment(Comment comment) {
        currentComments.add(comment);
        displayComments();
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        android.app.Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }
}
