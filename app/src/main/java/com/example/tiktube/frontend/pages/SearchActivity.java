package com.example.tiktube.frontend.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.tiktube.R;
import com.example.tiktube.backend.controllers.SearchController;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.frontend.adapters.PastSearchesAdapter;
import com.example.tiktube.frontend.adapters.SearchedUsersAdapter;
import com.example.tiktube.frontend.adapters.SearchedVideosAdapter;
import com.example.tiktube.frontend.adapters.TrendingSearchesAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchController searchController;
    User currentUser;

    TextView btnBack, txtSuggestions, btnRefresh, searchBtnVideos, searchBtnUsers;
    SearchView searchBar;
    ListView pastSearches, trendingSearches;
    ConstraintLayout searchSuggestions;
    LinearLayout searchResults;
    GridView videoResults;
    ListView userResults;
    boolean showUserRes = false;

    private List<User> searchedUsers = new ArrayList<>();

    private List<Video> searchedVideo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        currentUser = getIntent().getParcelableExtra("user");
        initComponent();

        ArrayList<String> pastSearchesList = new ArrayList<>();
        pastSearchesList.add("spider-man");
        pastSearchesList.add("winter soldier");
        pastSearchesList.add("doctor doom");
        pastSearchesList.add("batman");
        pastSearchesList.add("superman");
        pastSearchesList.add("wonder woman");
        pastSearchesList.add("darkseid");
        pastSearchesList.add("black racer");
        pastSearchesList.add("this is a very long search term that I'm using to test the UI");
        pastSearchesList.add("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");
        ArrayList<String> trendingSearchesList = new ArrayList<>();
        trendingSearchesList.add("RDJ is back!!");
        trendingSearchesList.add("Dr. Doom");
        trendingSearchesList.add("Mr. Stark");
        trendingSearchesList.add("Thanos");
        trendingSearchesList.add("Avengers");
        trendingSearchesList.add("Young Avengers");
        trendingSearchesList.add("Scarlet Witch");
        trendingSearchesList.add("WandaVision");
        trendingSearchesList.add("Moon Knight");

        searchBar.setIconifiedByDefault(false);

        PastSearchesAdapter pastSearchesAdapter = new PastSearchesAdapter(this, pastSearchesList);
        pastSearches.setAdapter(pastSearchesAdapter);
        TrendingSearchesAdapter trendingSearchesAdapter = new TrendingSearchesAdapter(this, trendingSearchesList);
        trendingSearches.setAdapter(trendingSearchesAdapter);
        SearchedVideosAdapter searchedVideosAdapter = new SearchedVideosAdapter(this, searchedVideo);
        videoResults.setAdapter(searchedVideosAdapter);
        SearchedUsersAdapter searchedUsersAdapter = new SearchedUsersAdapter(this, searchedUsers);
        userResults.setAdapter(searchedUsersAdapter);

        btnBack.setOnClickListener(v -> finish());

        pastSearches.setOnItemClickListener((parent, view, position, id) -> searchBar.setQuery(pastSearchesList.get(position), true));
        trendingSearches.setOnItemClickListener((parent, view, position, id) -> searchBar.setQuery(trendingSearchesList.get(position), true));

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    Log.d("Search Activity", "Searched keyword: " + query);

                    // Clear old results and reset the adapter
                    searchedVideo.clear();
                    searchedUsers.clear();

                    // Temporarily detach adapters
                    videoResults.setAdapter(null);
                    userResults.setAdapter(null);

                    searchController.search(query)
                            .thenAccept(res -> runOnUiThread(() -> {
                                if (res.isEmpty()) {
                                    Toast.makeText(SearchActivity.this, "No results found for: " + query, Toast.LENGTH_SHORT).show();
                                } else {
                                    // Process and add new results
                                    for (Object o : res) {
                                        if (o instanceof Video) {
                                            searchedVideo.add((Video) o);
                                            Log.d("search_results", "Searched video: " + ((Video) o).getTitle());
                                        } else if (o instanceof User) {
                                            searchedUsers.add((User) o);
                                        }
                                    }

                                    // Reattach and notify adapters after processing all results
                                    videoResults.setAdapter(searchedVideosAdapter);
                                    userResults.setAdapter(searchedUsersAdapter);
                                    searchedVideosAdapter.notifyDataSetChanged();
                                    searchedUsersAdapter.notifyDataSetChanged();
                                }
                            }))
                            .exceptionally(e -> {
                                // Handle any exceptions
                                runOnUiThread(() -> {
                                    Toast.makeText(SearchActivity.this, "Error occurred during search", Toast.LENGTH_SHORT).show();
                                });
                                e.printStackTrace();
                                return null;
                            });

                    // Update past searches
                    if (pastSearchesList.contains(query)) {
                        pastSearchesList.remove(query); // Avoid duplicate entries
                    }
                    pastSearchesList.add(0, query);
                    pastSearchesAdapter.notifyDataSetChanged();

                    searchBar.clearFocus();
                } else {
                    Toast.makeText(SearchActivity.this, "Please enter a search term", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



        searchBar.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                searchSuggestions.setVisibility(View.VISIBLE);
                searchResults.setVisibility(View.GONE);
            } else {
                searchSuggestions.setVisibility(View.GONE);
                searchResults.setVisibility(View.VISIBLE);
            }
        });

        searchBtnVideos.setOnClickListener(v -> {
            showUserRes = false;
            searchBtnVideos.setAlpha(1);
            searchBtnUsers.setAlpha(0.5F);
            videoResults.setVisibility(View.VISIBLE);
            userResults.setVisibility(View.GONE);
        });

        searchBtnUsers.setOnClickListener(v -> {
            showUserRes = true;
            searchBtnVideos.setAlpha(0.5F);
            searchBtnUsers.setAlpha(1);
            videoResults.setVisibility(View.GONE);
            userResults.setVisibility(View.VISIBLE);
        });

        videoResults.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(SearchActivity.this, UserVideoActivity.class);
            intent.putExtra("video", searchedVideo.get(position));
            intent.putExtra("user", currentUser);
            startActivity(intent);
        });
    }

    public void initComponent() {
        btnBack = findViewById(R.id.searchBtnBack);
        searchBar = findViewById(R.id.searchBar);
        pastSearches = findViewById(R.id.pastSearches);
        txtSuggestions = findViewById(R.id.txtSuggestions);
        btnRefresh = findViewById(R.id.searchBtnRefresh);
        trendingSearches = findViewById(R.id.trendingSearches);
        searchSuggestions = findViewById(R.id.searchSuggestions);
        searchResults = findViewById(R.id.searchResults);
        searchBtnVideos = findViewById(R.id.searchBtnVideos);
        searchBtnUsers = findViewById(R.id.searchBtnUsers);
        videoResults = findViewById(R.id.videoResults);
        userResults = findViewById(R.id.userResults);

        searchController = new SearchController();

        searchBar.requestFocus();
    }
}