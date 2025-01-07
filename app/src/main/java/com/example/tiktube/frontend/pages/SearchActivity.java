package com.example.tiktube.frontend.pages;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tiktube.R;
import com.example.tiktube.backend.controllers.SearchController;
import com.example.tiktube.backend.models.User;
import com.example.tiktube.backend.models.Video;
import com.example.tiktube.frontend.adapters.PastSearchesAdapter;
import com.example.tiktube.frontend.adapters.TrendingSearchesAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchController searchController;

    Button searchBtn;
    TextView btnBack;
    SearchView searchBar ;
    ListView pastSearches ;
    TextView txtSuggestions;
    TextView btnRefresh ;
    ListView trendingSearches;
    TextView btnImprove ;
    TextView btnLearnMore ;

    private List<User> searchedUsers = new ArrayList<>();

    private List<Video> searchedVideo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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

        btnBack.setOnClickListener(v -> finish());

        pastSearches.setOnItemClickListener((parent, view, position, id) -> searchBar.setQuery(pastSearchesList.get(position), true));
        trendingSearches.setOnItemClickListener((parent, view, position, id) -> searchBar.setQuery(trendingSearchesList.get(position), true));

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    pastSearchesList.remove(query);
                    pastSearchesList.add(0, query);
                    pastSearchesAdapter.notifyDataSetChanged();
                    searchBar.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        onSearchButtonClicked();
    }

    public void initComponent() {
        btnBack = findViewById(R.id.searchBtnBack);
        searchBar = findViewById(R.id.searchBar);
        pastSearches = findViewById(R.id.pastSearches);
        txtSuggestions = findViewById(R.id.txtSuggestions);
        btnRefresh = findViewById(R.id.searchBtnRefresh);
        trendingSearches = findViewById(R.id.trendingSearches);
       btnImprove = findViewById(R.id.searchBtnImprove);
        btnLearnMore = findViewById(R.id.searchBtnLearnMore);
        searchBtn = findViewById(R.id.searchBtn);

        searchController = new SearchController();
    }

    private void onSearchButtonClicked() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Extract the entered text from the SearchView
                String keywords = searchBar.getQuery().toString().trim();

                if (keywords.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Please enter a search term", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("Search Activity", "Searched keyword: " + keywords);

                // Call the search controller with the extracted keyword
                searchController.search(keywords)
                        .thenAccept(res -> runOnUiThread(() -> {
                            if (res.isEmpty()) {
                                Toast.makeText(SearchActivity.this, "No results found for: " + keywords, Toast.LENGTH_SHORT).show();
                            } else {
                                // Iterate over the results and log or display them
                                for (Object o : res) {
                                    if (o instanceof Video) {
                                        searchedVideo.add(((Video) o));
                                        Log.d("Search Activity", "Searched video: " + ((Video) o).getTitle());
                                        // Optionally, display the video titles in the UI
                                    } else if(o instanceof User) {
                                        searchedUsers.add(((User) o));
                                    }
                                }
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
            }
        });
    }

}