package com.cineai.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cineai.app.R;
import com.cineai.app.model.WatchlistMovie;

import java.util.ArrayList;
import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(WatchlistMovie movie);
    }

    public interface OnRemoveClickListener {
        void onRemove(WatchlistMovie movie);
    }

    private List<WatchlistMovie> movies;
    private final OnItemClickListener clickListener;
    private final OnRemoveClickListener removeListener;

    public WatchlistAdapter(OnItemClickListener clickListener, OnRemoveClickListener removeListener) {
        this.movies = new ArrayList<>();
        this.clickListener = clickListener;
        this.removeListener = removeListener;
    }

    public void setMovies(List<WatchlistMovie> movies) {
        this.movies = movies != null ? movies : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_watchlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(movies.get(position), clickListener, removeListener);
    }

    @Override
    public int getItemCount() { return movies.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle, tvRating, tvYear, tvOverview;
        ImageButton btnRemove;

        ViewHolder(View view) {
            super(view);
            ivPoster = view.findViewById(R.id.iv_poster);
            tvTitle = view.findViewById(R.id.tv_title);
            tvRating = view.findViewById(R.id.tv_rating);
            tvYear = view.findViewById(R.id.tv_year);
            tvOverview = view.findViewById(R.id.tv_overview);
            btnRemove = view.findViewById(R.id.btn_remove);
        }

        void bind(WatchlistMovie movie, OnItemClickListener clickListener, OnRemoveClickListener removeListener) {
            tvTitle.setText(movie.getTitle());
            tvRating.setText(movie.getFormattedRating());
            tvYear.setText(movie.getReleaseYear());
            tvOverview.setText(movie.getOverview());

            Glide.with(ivPoster.getContext())
                    .load(movie.getFullPosterUrl())
                    .placeholder(R.drawable.placeholder_poster)
                    .centerCrop()
                    .into(ivPoster);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onClick(movie);
            });

            btnRemove.setOnClickListener(v -> {
                if (removeListener != null) removeListener.onRemove(movie);
            });
        }
    }
}