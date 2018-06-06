package com.example.nikola.twitch;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

public class ChannelsActivity extends AppCompatActivity {

    final String TWITCH_STREAMS_URL = "https://api.twitch.tv/kraken/streams/?stream_type=live&game=";
    final String USER = "https://api.twitch.tv/kraken/user";
    final String CLIENT_ID = "&client_id=9cbkebgjlvii81kx5i0wa89b8pdld7";

    private Toolbar toolbar;
    private AppCompatImageView buttonBack;
    private AppCompatTextView game;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ChannelItem> channelItems;
    private String gameName;
    private String userName;
    private String code = "";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        buttonBack = (AppCompatImageView) findViewById(R.id.buttonBack);
        game = (AppCompatTextView) findViewById(R.id.gameName);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        channelItems = new ArrayList<>();

        Intent intent = getIntent();
        gameName = intent.getStringExtra("gameName");
        code = intent.getStringExtra("code");
        userName = intent.getStringExtra("userName");
        game.setText(gameName);
        new ParseTask().execute();

        //Toast.makeText(ChannelsActivity.this, userName, Toast.LENGTH_LONG).show();
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(ChannelsActivity.this, Stream.class);
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

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChannelsActivity.this, "Back", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }


    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(TWITCH_STREAMS_URL + gameName.replace(' ', '+') + CLIENT_ID);

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
                urlConnection.disconnect();

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
            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray streams = dataJsonObj.getJSONArray("streams");

                for (int i = 0; i < 25; i++) {
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
            adapter = new ChannelsAdapter(channelItems, getApplicationContext());
            recyclerView.setAdapter(adapter);
        }
    }
}
