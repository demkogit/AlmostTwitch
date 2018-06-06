package com.example.nikola.twitch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by NIKOLA on 29.03.2018.
 */

public class ChannelsAdapter extends RecyclerView.Adapter<ChannelsAdapter.ViewHolder> {

    private List<ChannelItem> channelItems;
    private Context context;


    public ChannelsAdapter(List<ChannelItem> channelItems, Context context) {
        this.channelItems = channelItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.channels_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChannelsAdapter.ViewHolder holder, int position) {
        final ChannelItem channelItem = channelItems.get(position);

        holder.broadcaster.setText(channelItem.getBroadcaster());
        holder.title.setText(channelItem.getTitle());
        holder.viewers.setText(channelItem.getViewers());
        Picasso.with(context).load(channelItem.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return channelItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView broadcaster;
        public TextView title;
        public TextView viewers;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            broadcaster = itemView.findViewById(R.id.broadcaster);
            title  =itemView.findViewById(R.id.title);
            viewers = itemView.findViewById(R.id.viewers);
        }
    }
}
