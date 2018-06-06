package com.example.nikola.twitch;

/**
 * Created by NIKOLA on 29.03.2018.
 */

public class ChannelItem {
    private String imageUrl;
    private String broadcaster;
    private String title;
    private String viewers;

    public ChannelItem(String imageUrl, String broadcaster, String title, String viewers) {
        this.imageUrl = imageUrl;
        this.broadcaster = broadcaster;
        this.title = title;
        this.viewers = viewers;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBroadcaster() {
        return broadcaster;
    }

    public String getTitle() {
        return title;
    }

    public String getViewers() {
        return viewers;
    }
}
