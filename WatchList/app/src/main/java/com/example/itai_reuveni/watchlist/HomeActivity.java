package com.example.itai_reuveni.watchlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ArrayList<String> listTitles = new ArrayList<>();
    private ArrayList<String> listIds = new ArrayList<>();
    private ListView lv;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        lv = findViewById(R.id.list);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                listTitles);

        setUpFab();
        setUpListView();
        addShowsToList();
    }

    private void setUpListView() {
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(HomeActivity.this, SeasonActivity.class);
                String showId = listIds.get(position);
                String showName = listTitles.get(position);
                i.putExtra("showId", showId);
                i.putExtra("showName", showName);
                startActivity(i);
            }
        });
    }

    private void setUpFab() {
        fab = findViewById(R.id.plus);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Add a new show!");

                // Set up the input
                final EditText input = new EditText(HomeActivity.this);

                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addShow(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    private void addShowsToList() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://10.0.2.2:3000/tvshows";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonarray = new JSONArray(response);
                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);
                                listTitles.add(jsonobject.getString("title"));
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

    private void addShow(String show) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
            String URL = "http://10.0.2.2:3000/tvshows";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("title", show);
            jsonBody.put("category", "N/A");
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.i("IN HERE", response);
                        JSONObject jsonBody = new JSONObject(response);
                        if (jsonBody.getBoolean("success")) {
                            listIds.add(jsonBody.getString("id"));
                            listTitles.add(jsonBody.getString("title"));
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.i("IN HERE", "WTF");
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                            builder.setTitle("An error occurred! Either the show is already there or it does not exist.");

                            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }
                    } catch (Exception e) {
                        Log.i("EXCEPTION", e.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = new String(response.data);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
