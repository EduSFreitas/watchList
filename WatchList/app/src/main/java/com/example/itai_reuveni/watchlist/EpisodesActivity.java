package com.example.itai_reuveni.watchlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class EpisodesActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ArrayList<String> listEpisodes = new ArrayList<>();
    private ArrayList<String> listIds = new ArrayList<>();
    private ArrayList<String> listImages = new ArrayList<>();
    private ArrayList<String> listSummaries = new ArrayList<>();
    private ListView lv;
    private String showName;
    private String season;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);
        intent = getIntent();

        lv = findViewById(R.id.list);

        TextView showTextView = findViewById(R.id.tvShow);
        showName = intent.getStringExtra("showName");
        showTextView.setText(showName);

        TextView seasonTextView = findViewById(R.id.season);
        season = intent.getStringExtra("season");
        seasonTextView.setText(season);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                listEpisodes);

        setUpListView();
        addEpisodesToList(intent.getStringExtra("showId"), getIntent().getStringExtra("seasonId"));
    }

    private void setUpListView() {
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String showId = intent.getStringExtra("showId");
                String seasonId = intent.getStringExtra("seasonId");
                String episodeId = listIds.get(position);
                String episode = listEpisodes.get(position);
                String image = listImages.get(position);
                String summary = listSummaries.get(position);
                Intent i = new Intent(EpisodesActivity.this, EpisodeActivity.class);
                i.putExtra("showId", showId);
                i.putExtra("showName", showName);
                i.putExtra("seasonId", seasonId);
                i.putExtra("season", season);
                i.putExtra("episodeId", episodeId);
                i.putExtra("episode", episode);
                i.putExtra("image", image);
                i.putExtra("summary", summary);
                startActivity(i);
            }
        });
    }

    private void addEpisodesToList(String showId, String seasonId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://10.0.2.2:3000/tvshows/" + showId + "/seasons/" + seasonId + "/episodes";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonarray = new JSONArray(response);
                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);
                                listEpisodes.add(jsonobject.getString("title"));
                                listIds.add(String.valueOf(jsonobject.getString("id")));
                                listImages.add(String.valueOf(jsonobject.getString("image")));
                                listSummaries.add(String.valueOf(jsonobject.getString("summary")));
                                adapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            Log.i("SHIT", "SHIT");
                            Log.i("ERROR", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TEST", "ERRORED SHIT");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
