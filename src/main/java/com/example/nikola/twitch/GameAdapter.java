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
 * Created by NIKOLA on 27.03.2018.
 */

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    private List<GameItem> gameItems;
    private Context context;

    public GameAdapter(List<GameItem> gameItems, Context context) {
        this.gameItems = gameItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final GameItem gameItem = gameItems.get(position);

        holder.viewersCountTextView.setText(gameItem.getViewers());
        Picasso.with(context).load(gameItem.getImageURL()).into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return gameItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView viewersCountTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            viewersCountTextView =  itemView.findViewById(R.id.viewersCount);
        }
    }
}
