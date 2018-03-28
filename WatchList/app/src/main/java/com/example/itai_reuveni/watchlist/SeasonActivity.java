package com.example.itai_reuveni.watchlist;

import android.content.Context;
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

public class SeasonActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ArrayList<String> listSeasons = new ArrayList<>();
    private ArrayList<String> listIds = new ArrayList<>();
    private ListView lv;
    private String showName;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seasons);
        intent = getIntent();

        lv = findViewById(R.id.list);

        TextView show = findViewById(R.id.tvShow);
        showName = intent.getStringExtra("showName");
        show.setText(showName);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                listSeasons);

        setUpListView();
        addSeasonsToList(intent.getStringExtra("showId"));
    }

    private void setUpListView() {
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(SeasonActivity.this, EpisodesActivity.class);
                String showId = intent.getStringExtra("showId");
                String seasonId = listIds.get(position);
                String season = listSeasons.get(position);
                i.putExtra("showId", showId);
                i.putExtra("showName", showName);
                i.putExtra("seasonId", seasonId);
                i.putExtra("season", season);

                startActivity(i);
            }
        });
    }

    private void addSeasonsToList(String showId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://10.0.2.2:3000/tvshows/" + showId + "/seasons";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonarray = new JSONArray(response);
                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);
                                listSeasons.add("Season " + String.valueOf(jsonobject.getString("season")));
                                listIds.add(String.valueOf(jsonobject.getString("id")));
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
