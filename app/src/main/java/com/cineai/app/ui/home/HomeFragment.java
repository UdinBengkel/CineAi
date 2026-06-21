package com.cineai.app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cineai.app.databinding.FragmentHomeBinding;
import com.cineai.app.model.Movie;
import com.cineai.app.model.MovieResponse;
import com.cineai.app.model.WatchlistMovie;
import com.cineai.app.repository.MovieRepository;
import com.cineai.app.ui.adapter.MovieHorizontalAdapter;
import com.cineai.app.ui.detail.MovieDetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MovieRepository repository;

    private MovieHorizontalAdapter recommendedAdapter;
    private MovieHorizontalAdapter nowPlayingAdapter;
    private MovieHorizontalAdapter popularAdapter;
    private MovieHorizontalAdapter topRatedAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = MovieRepository.getInstance(requireContext());

        setupRecyclerViews();
        loadStandardSections();
        observeWatchlist();

        binding.swipeRefresh.setOnRefreshListener(() -> {
            loadStandardSections();
            // Rekomendasi akan otomatis refresh karena LiveData observer masih aktif
        });
    }

    private void setupRecyclerViews() {
        MovieHorizontalAdapter.OnMovieClickListener onClick = movie -> {
            Intent i = new Intent(getContext(), MovieDetailActivity.class);
            i.putExtra("movie_id", movie.getId());
            i.putExtra("movie_title", movie.getTitle());
            startActivity(i);
        };

        recommendedAdapter = new MovieHorizontalAdapter(new ArrayList<>(), onClick);
        nowPlayingAdapter  = new MovieHorizontalAdapter(new ArrayList<>(), onClick);
        popularAdapter     = new MovieHorizontalAdapter(new ArrayList<>(), onClick);
        topRatedAdapter    = new MovieHorizontalAdapter(new ArrayList<>(), onClick);

        binding.rvRecommended.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvRecommended.setAdapter(recommendedAdapter);

        binding.rvNowPlaying.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvNowPlaying.setAdapter(nowPlayingAdapter);

        binding.rvPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPopular.setAdapter(popularAdapter);

        binding.rvTopRated.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTopRated.setAdapter(topRatedAdapter);
    }

    private void loadStandardSections() {
        binding.progressBar.setVisibility(View.VISIBLE);

        repository.getNowPlaying(1, new MovieRepository.MovieCallback<MovieResponse>() {
            @Override public void onSuccess(MovieResponse data) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    nowPlayingAdapter.setMovies(data.getResults());
                    binding.progressBar.setVisibility(View.GONE);
                    binding.swipeRefresh.setRefreshing(false);
                });
            }
            @Override public void onError(String msg) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.swipeRefresh.setRefreshing(false);
                });
            }
        });

        repository.getPopular(1, new MovieRepository.MovieCallback<MovieResponse>() {
            @Override public void onSuccess(MovieResponse data) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> popularAdapter.setMovies(data.getResults()));
            }
            @Override public void onError(String msg) {}
        });

        repository.getTopRated(1, new MovieRepository.MovieCallback<MovieResponse>() {
            @Override public void onSuccess(MovieResponse data) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> topRatedAdapter.setMovies(data.getResults()));
            }
            @Override public void onError(String msg) {}
        });
    }

    // ─── Watchlist Observer ───────────────────────────────────

    private void observeWatchlist() {
        repository.getWatchlist().observe(getViewLifecycleOwner(), watchlist -> {
            if (watchlist == null || watchlist.isEmpty()) {
                hideRecommended();
                return;
            }
            String genreIds = extractTopGenres(watchlist);
            if (genreIds.isEmpty()) {
                hideRecommended();
                return;
            }
            fetchRecommended(genreIds, watchlist);
        });
    }

    // ─── Genre Extraction (FIXED) ─────────────────────────────

    /**
     * Parse genreIdsJson dari setiap film di watchlist,
     * hitung frekuensi tiap genre, ambil top 3.
     *
     * Format genreIdsJson: "[16,10751,35]"  ← animasi=16, family=10751, comedy=35
     */
    private String extractTopGenres(List<WatchlistMovie> watchlist) {
        Map<Integer, Integer> freq = new HashMap<>();

        for (WatchlistMovie movie : watchlist) {
            String json = movie.getGenreIdsJson();
            if (json == null || json.equals("[]") || json.isEmpty()) continue;

            // Strip brackets dan split
            String stripped = json.replace("[", "").replace("]", "").trim();
            if (stripped.isEmpty()) continue;

            for (String token : stripped.split(",")) {
                try {
                    int id = Integer.parseInt(token.trim());
                    freq.put(id, freq.getOrDefault(id, 0) + 1);
                } catch (NumberFormatException ignored) {}
            }
        }

        if (freq.isEmpty()) return "";

        // Sort descending by frequency
        List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(freq.entrySet());
        entries.sort((a, b) -> b.getValue() - a.getValue());

        // Ambil top 3 genre ID, jadikan string "16,10751,35"
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(3, entries.size());
        for (int i = 0; i < limit; i++) {
            if (i > 0) sb.append(",");
            sb.append(entries.get(i).getKey());
        }
        return sb.toString();
    }

    // ─── Fetch Recommended ────────────────────────────────────

    private void fetchRecommended(String genreIds, List<WatchlistMovie> watchlist) {
        binding.progressRecommended.setVisibility(View.VISIBLE);

        // Kumpulkan movie ID di watchlist untuk di-filter nanti
        List<Integer> watchlistIds = new ArrayList<>();
        for (WatchlistMovie w : watchlist) watchlistIds.add(w.getMovieId());

        repository.discoverMovies(
                genreIds,
                null,                  // semua tahun
                "popularity.desc",     // urut paling populer agar relevan
                6.0f,                  // rating minimal 6.0 (lebih longgar dari 7.0)
                1,
                new MovieRepository.MovieCallback<MovieResponse>() {
                    @Override
                    public void onSuccess(MovieResponse data) {
                        if (getActivity() == null) return;

                        List<Movie> filtered = new ArrayList<>();
                        if (data.getResults() != null) {
                            for (Movie m : data.getResults()) {
                                // Jangan tampilkan film yang sudah di watchlist
                                if (!watchlistIds.contains(m.getId())) {
                                    filtered.add(m);
                                }
                            }
                        }

                        getActivity().runOnUiThread(() -> {
                            binding.progressRecommended.setVisibility(View.GONE);
                            if (filtered.isEmpty()) {
                                hideRecommended();
                            } else {
                                showRecommended(filtered);
                            }
                        });
                    }

                    @Override
                    public void onError(String msg) {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            binding.progressRecommended.setVisibility(View.GONE);
                            hideRecommended();
                        });
                    }
                }
        );
    }

    private void showRecommended(List<Movie> movies) {
        binding.sectionRecommended.setVisibility(View.VISIBLE);
        recommendedAdapter.setMovies(movies);
    }

    private void hideRecommended() {
        binding.sectionRecommended.setVisibility(View.GONE);
        recommendedAdapter.setMovies(new ArrayList<>());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}