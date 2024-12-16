package com.example.tiktube.frontend.search;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tiktube.R;
import com.example.tiktube.frontend.adapters.PastSearchesAdapter;
import com.example.tiktube.frontend.adapters.TrendingSearchesAdapter;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        TextView btnBack = findViewById(R.id.searchBtnBack);
        SearchView searchBar = findViewById(R.id.searchBar);
        ListView pastSearches = findViewById(R.id.pastSearches);
        TextView txtSuggestions = findViewById(R.id.txtSuggestions);
        TextView btnRefresh = findViewById(R.id.searchBtnRefresh);
        ListView trendingSearches = findViewById(R.id.trendingSearches);
        TextView btnImprove = findViewById(R.id.searchBtnImprove);
        TextView btnLearnMore = findViewById(R.id.searchBtnLearnMore);

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

        Intent intent = getIntent();

        searchBar.setIconifiedByDefault(false);

        PastSearchesAdapter adapter1 = new PastSearchesAdapter(this, pastSearchesList);
        pastSearches.setAdapter(adapter1);
        TrendingSearchesAdapter adapter2 = new TrendingSearchesAdapter(this, trendingSearchesList);
        trendingSearches.setAdapter(adapter2);

        btnBack.setOnClickListener(v -> finish());
    }
}