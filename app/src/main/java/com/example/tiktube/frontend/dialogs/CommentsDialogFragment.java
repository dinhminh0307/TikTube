package com.example.tiktube.frontend.dialogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.tiktube.R;
import com.example.tiktube.backend.models.Interaction;
import com.example.tiktube.frontend.adapters.CommentsAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommentsDialogFragment extends DialogFragment {

    private RecyclerView commentsRecyclerView;
    private EditText commentInput;
    private ImageView sendButton;
    private List<Interaction> commentsList = new ArrayList<>();
    private CommentsAdapter adapter;

    public static CommentsDialogFragment newInstance(String videoId) {
        CommentsDialogFragment fragment = new CommentsDialogFragment();
        Bundle args = new Bundle();
        args.putString("videoId", videoId);
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

        commentsRecyclerView = view.findViewById(R.id.comments_recycler_view);
        commentInput = view.findViewById(R.id.comment_input);
        sendButton = view.findViewById(R.id.send_button);

        commentsList.add(new Interaction("User", "ok", "DM", true, "Just now"));
        commentsList.add(new Interaction("User", "ok", "DM", true, "Just now"));
        commentsList.add(new Interaction("User", "ok", "DM", true, "Just now"));

        // Initialize RecyclerView
        adapter = new CommentsAdapter(commentsList);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsRecyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(v -> {
            String newComment = commentInput.getText().toString().trim();
            if (!newComment.isEmpty()) {
                commentsList.add(new Interaction("User", "ok", newComment, true, "Just now"));
                adapter.notifyItemInserted(commentsList.size() - 1);
                commentsRecyclerView.scrollToPosition(commentsList.size() - 1); // Scroll to the bottom
                commentInput.setText("");
            }
        });

        return view;
    }

}
