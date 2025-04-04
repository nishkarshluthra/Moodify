package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private List<Playlist> playlists;

    public PlaylistAdapter(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        return new PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.nameTextView.setText(playlist.getName());
        holder.descriptionTextView.setText(playlist.getDescription());
        Glide.with(holder.imageView.getContext())
                .load(playlist.getImageUrl())
                .into(holder.imageView);

        holder.playButton.setOnClickListener(v -> {
            // Open the Spotify playlist in the Spotify app
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(playlist.getUri()));
            intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://com.spotify.music"));
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, descriptionTextView;
        ImageView imageView;
        Button playButton;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.playlist_name);
            descriptionTextView = itemView.findViewById(R.id.playlist_description);
            imageView = itemView.findViewById(R.id.playlist_image);
            playButton = itemView.findViewById(R.id.play_button);
        }
    }
}
