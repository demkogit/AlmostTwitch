package com.example.nikola.twitch;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppCompatImageView backButton;
    private EditText input;
    private Button search;
    private String request;
    private List<ChannelItem> channelItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private Context context;
    final String SEARCH_STRING = "https://api.twitch.tv/kraken/search/streams?query=";
    final String CLIENT_ID = "&client_id=9cbkebgjlvii81kx5i0wa89b8pdld7";
    private String code;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        backButton = (AppCompatImageView) findViewById(R.id.buttonBack);
        input = (EditText) findViewById(R.id.input);
        search = (Button) findViewById(R.id.search);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        userName = intent.getStringExtra("userName");





        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request = input.getText().toString();
                request.replace(' ', '+');
                new ParseTask().execute();
            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(SearchActivity.this, Stream.class);
                        intent.putExtra("broadcaster", channelItems.get(position).getBroadcaster());
                        intent.putExtra("code", code);
                        intent.putExtra("userName", userName);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(SEARCH_STRING + request + CLIENT_ID);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            JSONObject dataJsonObj = null;
            channelItems = new ArrayList<>();
            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray streams = dataJsonObj.getJSONArray("streams");

                for (int i = 0; i < streams.length(); i++) {
                    JSONObject stream = streams.getJSONObject(i);
                    String imageUrl = stream.getJSONObject("preview").getString("large");
                    String broadcaster = stream.getJSONObject("channel").getString("display_name");
                    String title = stream.getJSONObject("channel").getString("status");
                    String viewers = stream.getString("viewers") + " viewers";

                    ChannelItem channelItem = new ChannelItem(imageUrl, broadcaster, title, viewers);
                    channelItems.add(channelItem);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (channelItems.isEmpty())
                Toast.makeText(SearchActivity.this, "Ничего не найдено", Toast.LENGTH_LONG).show();
            adapter = new ChannelsAdapter(channelItems, getApplicationContext());
            recyclerView.setAdapter(adapter);
        }

    }
}

