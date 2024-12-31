package com.example.tiktube.frontend.dialogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.tiktube.R;
import com.example.tiktube.backend.callbacks.DataFetchCallback;
import com.example.tiktube.backend.controllers.InteractionController;
import com.example.tiktube.backend.controllers.LoginController;
import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.backend.controllers.VideoController;
import com.example.tiktube.backend.models.Interaction;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.backend.utils.UidGenerator;
import com.example.tiktube.frontend.adapters.CommentsAdapter;
import com.example.tiktube.frontend.pages.VideoPageActivity;

import java.util.ArrayList;
import java.util.List;

public class CommentsDialogFragment extends DialogFragment {

    private RecyclerView commentsRecyclerView;
    private EditText commentInput;
    private ImageView sendButton;
    private List<Interaction> commentsList = new ArrayList<>();
    private CommentsAdapter adapter;

    private UserController userController;

    private LoginController loginController;

    private InteractionController interactionController;

    private VideoController videoController;

    private Video video;

    private boolean isLiked = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            video = getArguments().getParcelable("video");
        }
    }

    public static CommentsDialogFragment newInstance(Video video) {
        CommentsDialogFragment fragment = new CommentsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("video", video); // Video must implement Parcelable
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set the dialog width to match the screen width
        if (getDialog() != null && getDialog().getWindow() != null) {
            int screenWidth = ViewGroup.LayoutParams.MATCH_PARENT;
            int screenHeight = getResources().getDisplayMetrics().heightPixels / 2; // Half screen height
            getDialog().getWindow().setLayout(screenWidth, screenHeight);
            getDialog().getWindow().setGravity(Gravity.BOTTOM); // Align to bottom
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comment_dialog, container, false);

        loginController = new LoginController();
        userController = new UserController();
        videoController = new VideoController();
        interactionController = new InteractionController();
        commentsRecyclerView = view.findViewById(R.id.comments_recycler_view);
        commentInput = view.findViewById(R.id.comment_input);
        sendButton = view.findViewById(R.id.send_button);



        fetchComments();

        userInteractVideo();

        return view;
    }

    private void fetchComments() {
        interactionController.getAllInteractionsByVideoUID(video.getUid(), new DataFetchCallback<Interaction>() {
            @Override
            public void onSuccess(List<Interaction> data) {
                // Initialize RecyclerView
                commentsList.clear(); // Clear the existing comments
                commentsList.addAll(data);
                if (adapter == null) {
                    adapter = new CommentsAdapter(commentsList);
                    commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    commentsRecyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Comment Dialog", "Error fetching comments: " + e.getMessage());
            }
        });
    }

    private void userInteractVideo() {
        sendButton.setOnClickListener(v -> {
            String newComment = commentInput.getText().toString().trim();
            if (!newComment.isEmpty()) {
                Interaction newInteraction = new Interaction(
                        UidGenerator.generateUID(),
                        loginController.getUserUID(),
                        video.getUid(),
                        newComment,
                        "Just now"
                );

                userController.userInteraction(newInteraction, video, newInteraction.getUid(), new DataFetchCallback<String>() {
                    @Override
                    public void onSuccess(List<String> data) {
                        commentInput.setText(""); // Clear the input field
                        fetchComments(); // Fetch updated comments

                        // Update comment count in the video object
                        video.getInteractions().add(data.get(0));

                        // Notify the adapter about the change (if the adapter is passed via callback)
                        if (getActivity() instanceof VideoPageActivity) {
                            ((VideoPageActivity) getActivity()).updateVideo(video);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Comment Dialog", "Error sending comment: " + e.getMessage());
                    }
                });
            }
        });
    }


}
