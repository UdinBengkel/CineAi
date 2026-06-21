package com.cineai.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cineai.app.R;
import com.cineai.app.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieHorizontalAdapter extends RecyclerView.Adapter<MovieHorizontalAdapter.ViewHolder> {

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    private List<Movie> movies;
    private final OnMovieClickListener listener;

    public MovieHorizontalAdapter(List<Movie> movies, OnMovieClickListener listener) {
        this.movies = movies != null ? movies : new ArrayList<>();
        this.listener = listener;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies != null ? movies : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie, listener);
    }

    @Override
    public int getItemCount() { return movies.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle, tvYear, tvRating;

        ViewHolder(View view) {
            super(view);
            ivPoster = view.findViewById(R.id.iv_poster);
            tvTitle = view.findViewById(R.id.tv_title);
            tvYear = view.findViewById(R.id.tv_year);
            tvRating = view.findViewById(R.id.tv_rating);
        }

        void bind(Movie movie, OnMovieClickListener listener) {
            tvTitle.setText(movie.getTitle());
            tvYear.setText(movie.getReleaseYear());
            tvRating.setText(movie.getFormattedRating());

            Glide.with(ivPoster.getContext())
                    .load(movie.getFullPosterUrl())
                    .placeholder(R.drawable.placeholder_poster)
                    .centerCrop()
                    .into(ivPoster);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onMovieClick(movie);
            });
        }
    }
}