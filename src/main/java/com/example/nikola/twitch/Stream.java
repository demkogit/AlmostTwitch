package com.example.nikola.twitch;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Stream extends AppCompatActivity {

    private WebView stream;
    private String broadcaster;
    private String userName;
    private Toolbar toolbar;
    private Button followBtn;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        followBtn = (Button) findViewById(R.id.followBtn);

        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        broadcaster = intent.getStringExtra("broadcaster");
        userName= intent.getStringExtra("userName");
        code = intent.getStringExtra("code");
        stream = (WebView) findViewById(R.id.streamView);
        stream.getSettings().setJavaScriptEnabled(true);
        stream.loadUrl("https://player.twitch.tv/?channel="+broadcaster);

        new ParseTask().execute();
    }
    @Override
    public void onBackPressed() {
        stream.destroy();
        finish();
    }
    private View.OnClickListener follow(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Follow().execute("PUT");
                followBtn.setText("Отписаться");
                followBtn.setOnClickListener(unfollow());
            }
        };
    }
    private View.OnClickListener unfollow(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Follow().execute("DELETE");
                followBtn.setText("Подписаться");
                followBtn.setOnClickListener(follow());
            }
        };
    }
    private View.OnClickListener login(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Stream.this, AuthActivity.class);
                startActivity(intent);
                finish();
            }
        };

    }

    private class Follow extends  AsyncTask<String, Void, String>{
        HttpURLConnection urlConnection = null;
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("https://api.twitch.tv/kraken/users/" + userName + "/follows/channels/" + broadcaster);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Client-ID", "9cbkebgjlvii81kx5i0wa89b8pdld7");
                urlConnection.setRequestProperty("Authorization", "OAuth "+code);
                urlConnection.setRequestMethod(params[0]);
                urlConnection.connect();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        String resultJson = "";
        String response;
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("https://api.twitch.tv/kraken/users/" + userName + "/follows/channels/" + broadcaster);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Client-ID", "9cbkebgjlvii81kx5i0wa89b8pdld7");
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                System.out.println("ResponseCodeStream: " + urlConnection.getResponseCode());
                response = urlConnection.getResponseCode()+"";


            }catch (Exception e){
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            System.out.println("userName: " + (userName));
            if(userName==null) {
                followBtn.setText("Вы не вошли в систему");
                followBtn.setOnClickListener(login());
            }
            else {
                switch (response) {
                    case "200":
                        followBtn.setText("Отписаться");
                        followBtn.setOnClickListener(unfollow());
                        break;
                    case "404":
                        followBtn.setText("Подписаться");
                        followBtn.setOnClickListener(follow());
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
