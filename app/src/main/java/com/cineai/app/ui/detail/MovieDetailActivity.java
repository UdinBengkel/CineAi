package com.cineai.app.ui.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.cineai.app.R;
import com.cineai.app.databinding.ActivityMovieDetailBinding;
import com.cineai.app.model.Genre;
import com.cineai.app.model.Movie;
import com.cineai.app.model.StreamingProvider;
import com.cineai.app.model.Video;
import com.cineai.app.repository.MovieRepository;
import com.cineai.app.ui.adapter.MovieHorizontalAdapter;
import com.google.android.material.chip.Chip;

import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {

    private ActivityMovieDetailBinding binding;
    private MovieRepository repository;
    private Movie currentMovie;
    private String trailerUrl   = null;
    private String watchUrl     = null;
    private boolean isNowPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        int movieId = getIntent().getIntExtra("movie_id", -1);
        repository = MovieRepository.getInstance(this);

        if (movieId != -1) {
            loadMovieDetail(movieId);
            loadTrailer(movieId);
            loadWatchProviders(movieId);
            loadSimilarMovies(movieId);
        }

        setupButtons();
    }

    // ─── Load data ────────────────────────────────────────────

    private void loadMovieDetail(int movieId) {
        binding.progressBar.setVisibility(View.VISIBLE);
        repository.getMovieDetail(movieId, new MovieRepository.MovieCallback<Movie>() {
            @Override public void onSuccess(Movie movie) {
                currentMovie = movie;
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    populateUI(movie);
                });
            }
            @Override public void onError(String message) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(MovieDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void populateUI(Movie movie) {
        // Backdrop
        Glide.with(this)
                .load(movie.getFullBackdropUrl())
                .transform(new CenterCrop())
                .placeholder(R.color.surface)
                .into(binding.ivBackdrop);

        // Poster
        Glide.with(this)
                .load(movie.getFullPosterUrl())
                .placeholder(R.drawable.placeholder_poster)
                .into(binding.ivPoster);

        binding.tvTitle.setText(movie.getTitle());
        binding.tvRating.setText(movie.getFormattedRating());
        binding.tvYear.setText(movie.getReleaseYear());
        binding.tvRuntime.setText(movie.getFormattedRuntime());
        binding.tvOverview.setText(movie.getOverview());

        if (movie.getTagline() != null && !movie.getTagline().isEmpty()) {
            binding.tvTagline.setVisibility(View.VISIBLE);
            binding.tvTagline.setText("\"" + movie.getTagline() + "\"");
        }

        // Genre chips
        binding.chipGroupGenres.removeAllViews();
        if (movie.getGenres() != null) {
            for (Genre g : movie.getGenres()) {
                Chip chip = new Chip(this);
                chip.setText(g.getName());
                chip.setClickable(false);
                chip.setChipBackgroundColorResource(R.color.surface_variant);
                chip.setTextColor(getColor(R.color.text_secondary));
                binding.chipGroupGenres.addView(chip);
            }
        }

        updateWatchlistIcon(movie.isInWatchlist());
        isNowPlaying = movie.isNowPlaying();
        updateWatchButton();
    }

    private void loadTrailer(int movieId) {
        repository.getMovieVideos(movieId, new MovieRepository.MovieCallback<Video.VideoResponse>() {
            @Override public void onSuccess(Video.VideoResponse data) {
                if (data.getResults() == null) return;
                Video trailer = null;
                for (Video v : data.getResults()) {
                    if (v.isTrailer() && v.isOfficial()) { trailer = v; break; }
                }
                if (trailer == null) {
                    for (Video v : data.getResults()) {
                        if (v.isTrailer()) { trailer = v; break; }
                    }
                }
                if (trailer != null) {
                    trailerUrl = trailer.getYoutubeUrl();
                    runOnUiThread(() -> binding.btnTrailer.setVisibility(View.VISIBLE));
                }
            }
            @Override public void onError(String message) {}
        });
    }

    private void loadWatchProviders(int movieId) {
        repository.getWatchProviders(movieId, new MovieRepository.MovieCallback<StreamingProvider.ProvidersResponse>() {
            @Override public void onSuccess(StreamingProvider.ProvidersResponse data) {
                StreamingProvider provider = data.getIndonesiaProvider();
                if (provider != null && provider.getLink() != null) {
                    watchUrl = provider.getLink();
                    runOnUiThread(() -> updateWatchButton());
                }
            }
            @Override public void onError(String message) {}
        });
    }

    private void loadSimilarMovies(int movieId) {
        MovieHorizontalAdapter adapter = new MovieHorizontalAdapter(null, movie -> {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra("movie_id", movie.getId());
            intent.putExtra("movie_title", movie.getTitle());
            startActivity(intent);
        });
        binding.rvSimilar.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvSimilar.setAdapter(adapter);

        repository.getRecommendations(movieId, 1, new MovieRepository.MovieCallback<com.cineai.app.model.MovieResponse>() {
            @Override public void onSuccess(com.cineai.app.model.MovieResponse data) {
                runOnUiThread(() -> {
                    adapter.setMovies(data.getResults());
                    boolean hasItems = data.getResults() != null && !data.getResults().isEmpty();
                    binding.tvSimilarTitle.setVisibility(hasItems ? View.VISIBLE : View.GONE);
                });
            }
            @Override public void onError(String message) {}
        });
    }

    // ─── Buttons ──────────────────────────────────────────────

    private void setupButtons() {
        // Trailer — icon button kecil
        binding.btnTrailer.setOnClickListener(v -> {
            if (trailerUrl != null) openUrl(trailerUrl);
        });

        // Watch Now / Beli Tiket — tombol besar merah
        binding.btnWatch.setOnClickListener(v -> {
            if (watchUrl != null) openUrl(watchUrl);
        });

        // Watchlist — icon toggle
        binding.btnWatchlist.setOnClickListener(v -> {
            if (currentMovie == null) return;
            if (currentMovie.isInWatchlist()) {
                repository.removeFromWatchlist(currentMovie.getId(), () -> runOnUiThread(() -> {
                    currentMovie.setInWatchlist(false);
                    updateWatchlistIcon(false);
                    Toast.makeText(this, "Dihapus dari watchlist", Toast.LENGTH_SHORT).show();
                }));
            } else {
                repository.addToWatchlist(currentMovie, () -> runOnUiThread(() -> {
                    currentMovie.setInWatchlist(true);
                    updateWatchlistIcon(true);
                    Toast.makeText(this, "Ditambahkan ke watchlist!", Toast.LENGTH_SHORT).show();
                }));
            }
        });
    }

    private void updateWatchlistIcon(boolean inWatchlist) {
        if (inWatchlist) {
            binding.btnWatchlist.setIconResource(R.drawable.ic_bookmark_filled);
            binding.btnWatchlist.setStrokeColorResource(R.color.brand_red);
        } else {
            binding.btnWatchlist.setIconResource(R.drawable.ic_bookmark);
            binding.btnWatchlist.setStrokeColorResource(R.color.stroke);
        }
    }

    private void updateWatchButton() {
        if (currentMovie == null) return;
        if (isNowPlaying) {
            binding.btnWatch.setVisibility(View.VISIBLE);
            binding.btnWatch.setText(getString(R.string.btn_ticket));
            if (watchUrl == null) watchUrl = "https://www.tix.id/";
        } else if (watchUrl != null) {
            binding.btnWatch.setVisibility(View.VISIBLE);
            binding.btnWatch.setText(getString(R.string.btn_watch_now));
        } else {
            binding.btnWatch.setVisibility(View.GONE);
        }
    }

    private void openUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}