package com.example.nikola.twitch;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity {

    private Context context;
    private Toolbar toolbar;
    private AppCompatImageView searchButton;
    final String TWITCH_URL_TOP_GAMES = "https://api.twitch.tv/kraken/games/top?client_id=9cbkebgjlvii81kx5i0wa89b8pdld7";
    final String USER = "https://api.twitch.tv/kraken/user";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<GameItem> gameItems;
    private String code = "";
    private TextView textView;
    private Button loginButton;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        loginButton = (Button) findViewById(R.id.loginBtn);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchButton = (AppCompatImageView) findViewById(R.id.buttonSearch);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        gameItems = new ArrayList<>();
        new ParseTask().execute();
        Intent intentThis = getIntent();
        code = intentThis.getStringExtra("code");


        if (code == null) {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                    startActivity(intent);

                }
            });
        } else {
            loginButton.setText("Following channels");
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, FollowActivity.class);
                    intent.putExtra("code", code);
                    intent.putExtra("userName", userName);
                    startActivity(intent);
                }
            });
        }

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this, ChannelsActivity.class);
                        intent.putExtra("gameName", gameItems.get(position).getGameName());
                        intent.putExtra("code", code);
                        intent.putExtra("userName", userName);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("code", code);
                intent.putExtra("userName", userName);
                startActivity(intent);
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
                URL url = new URL(TWITCH_URL_TOP_GAMES);

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

                if (code != null) {
                    URL urll = new URL(USER);

                    urlConnection = (HttpURLConnection) urll.openConnection();
                    urlConnection.setRequestProperty("Client-ID", "9cbkebgjlvii81kx5i0wa89b8pdld7");
                    urlConnection.setRequestProperty("Authorization", "OAuth " + code);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inpS = urlConnection.getInputStream();
                    StringBuffer sb = new StringBuffer();
                    reader = new BufferedReader(new InputStreamReader(inpS));
                    String temp;

                    while ((temp = reader.readLine()) != null) {
                        sb.append(temp);
                    }

                    urlConnection.disconnect();
                    JSONObject dataJsonObj = null;
                    try {
                        dataJsonObj = new JSONObject(sb.toString());
                        userName = dataJsonObj.getString("display_name");
                        System.out.println("ResponseCode: " + userName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray topGames = dataJsonObj.getJSONArray("top");

                for (int i = 0; i < 10; i++) {
                    JSONObject game = topGames.getJSONObject(i).getJSONObject("game").getJSONObject("box");
                    String gameURL = game.getString("large");
                    String viewersCount = topGames.getJSONObject(i).getString("viewers");
                    String gameName = topGames.getJSONObject(i).getJSONObject("game").getString("name");
                    GameItem item = new GameItem(gameURL, gameName, viewersCount + " viewers");
                    gameItems.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (gameItems.isEmpty())
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
            adapter = new GameAdapter(gameItems, getApplicationContext());
            recyclerView.setAdapter(adapter);

        }
    }
}
