package com.example.nikola.twitch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AuthActivity extends AppCompatActivity {

    private WebView webView;
    final String authStr = "https://passport.twitch.tv/sessions/new?client_id=9cbkebgjlvii81kx5i0wa89b8pdld7&redirect_path=https%3A%2F%2Fapi.twitch.tv%2Fkraken%2Foauth2%2Fauthorize%3Faction%3Dauthenticate%26client_id%3D9cbkebgjlvii81kx5i0wa89b8pdld7%26redirect_uri%3Dhttp%253A%252F%252Flocalhost%26response_type%3Dtoken%26scope%3Duser_read%2Buser_follows_edit%2Bopenid&redirect_uri=http%3A%2F%2Flocalhost&response_type=token&scope=user_read+user_follows_edit+openid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(authStr);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                parse(url);
                return false;
            }
        });
    }

    private void parse(String url) {
        if (contain(url, "http://localhost")) {
            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
            webView.destroy();
            intent.putExtra("code", url.substring(31, 61));
            startActivity(intent);
            finish();
        }
    }

    private boolean contain(String firstStr, String secondStr) {
        if (firstStr != null && secondStr != null) {
            for (int i = 0; i < (firstStr.length() < secondStr.length() ? firstStr.length() : secondStr.length()); i++) {
                if (firstStr.charAt(i) != secondStr.charAt(i))
                    return false;
            }
            return true;
        }
        return false;
    }
}
