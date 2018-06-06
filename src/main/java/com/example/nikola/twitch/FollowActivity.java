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
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FollowActivity extends AppCompatActivity {

    private AppCompatImageView buttonBack;
    private Toolbar toolbar;
    private TextView textView;
    private WebView webView;
    private TextView title;
    final String FOLLOW_URL = "https://api.twitch.tv/kraken/streams/followed";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ChannelItem> channelItems;
    private String code = "";
    private Context context;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textView = (TextView) findViewById(R.id.textView2);
        setSupportActionBar(toolbar);
        buttonBack = (AppCompatImageView) findViewById(R.id.buttonBack);
        title = (TextView) findViewById(R.id.gameName);
        title.setText("Подписки");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        channelItems = new ArrayList<>();
        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        userName = intent.getStringExtra("userName");
        new ParseTask().execute();


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(FollowActivity.this, Stream.class);
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
                Toast.makeText(FollowActivity.this, "Back", Toast.LENGTH_LONG).show();
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
                URL url = new URL(FOLLOW_URL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Client-ID", "9cbkebgjlvii81kx5i0wa89b8pdld7");
                urlConnection.setRequestProperty("Authorization", "OAuth " + code);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                System.out.println("ResponseCode: " + urlConnection.getResponseCode());

                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                String jsonString = sb.toString();
                resultJson = jsonString;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
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
            if (channelItems.size() == 0)
                textView.setText("Нет активных каналов");
            adapter = new ChannelsAdapter(channelItems, getApplicationContext());
            recyclerView.setAdapter(adapter);
        }
    }
}
