package com.example.tiktube.backend.helpers;

import android.content.Context;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.tiktube.backend.controllers.UserController;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.callbacks.DataFetchCallback;

import java.util.List;

public class UserListHelper {

    private final Context context;
    private final UserController userController;

    public UserListHelper(Context context, UserController userController) {
        this.context = context;
        this.userController = userController;
    }

    /**
     * Adds a summary of the list (title and count).
     */
    public void addListSummary(LinearLayout parent, String title, int count) {
        TextView titleView = new TextView(context);
        titleView.setText(title + " (" + count + ")");
        titleView.setTextSize(16);
        titleView.setPadding(0, 8, 0, 4);
        parent.addView(titleView);
    }

    /**
     * Fetches names corresponding to UIDs and displays them in a table.
     */
    public void fetchAndDisplayNamesAsTable(LinearLayout parent, String title, List<String> uidList) {
        if (uidList == null || uidList.isEmpty()) {
            addListSummary(parent, title, 0); // Show zero if the list is empty
            return;
        }

        // Add title
        TextView titleView = new TextView(context);
        titleView.setText(title + " (" + uidList.size() + ")");
        titleView.setTextSize(16);
        titleView.setPadding(0, 8, 0, 4);
        parent.addView(titleView);

        // Create a table layout for displaying names
        TableLayout tableLayout = new TableLayout(context);
        tableLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tableLayout.setStretchAllColumns(true);
        parent.addView(tableLayout);

        // Fetch and populate rows dynamically
        for (int i = 0; i < uidList.size(); i++) {
            final int index = i;
            String uid = uidList.get(i);

            new Handler().postDelayed(() -> {
                userController.getUserById(uid, new DataFetchCallback<User>() {
                    @Override
                    public void onSuccess(List<User> users) {
                        if (!users.isEmpty()) {
                            String name = users.get(0).getName();

                            // Create a new table row
                            TableRow row = new TableRow(context);

                            // Create name cell
                            TextView nameView = new TextView(context);
                            nameView.setText(name);
                            nameView.setTextSize(14);
                            nameView.setPadding(16, 8, 16, 8);
                            row.addView(nameView);

                            // Add row to the table
                            tableLayout.addView(row);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure by adding a row indicating error
                        TableRow row = new TableRow(context);

                        TextView errorView = new TextView(context);
                        errorView.setText("Failed to fetch name for UID: " + uid);
                        errorView.setTextSize(14);
                        errorView.setPadding(16, 8, 16, 8);
                        row.addView(errorView);

                        tableLayout.addView(row);
                    }
                });
            }, index * 200); // Delay to fetch and display each user
        }
    }
}
