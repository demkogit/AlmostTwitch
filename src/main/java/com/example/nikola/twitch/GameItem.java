package com.example.nikola.twitch;

import android.widget.ImageView;

/**
 * Created by NIKOLA on 27.03.2018.
 */

public class GameItem {
    private String imageURL;
    private String gameName;
    private String viewers;

    public GameItem(String imageURL, String gameName, String viewers) {
        this.imageURL = imageURL;
        this.gameName = gameName;
        this.viewers = viewers;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getGameName() {
        return gameName;
    }

    public String getViewers() {
        return viewers;
    }
}
