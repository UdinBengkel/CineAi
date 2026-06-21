package com.cineai.app.ui.watchlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cineai.app.databinding.FragmentWatchlistBinding;
import com.cineai.app.model.WatchlistMovie;
import com.cineai.app.repository.MovieRepository;
import com.cineai.app.ui.adapter.WatchlistAdapter;
import com.cineai.app.ui.detail.MovieDetailActivity;

import java.util.List;

public class WatchlistFragment extends Fragment {

    private FragmentWatchlistBinding binding;
    private MovieRepository repository;
    private WatchlistAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWatchlistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = MovieRepository.getInstance(requireContext());
        setupRecyclerView();
        observeWatchlist();
    }

    private void setupRecyclerView() {
        adapter = new WatchlistAdapter(
                movie -> {
                    Intent intent = new Intent(getContext(), MovieDetailActivity.class);
                    intent.putExtra("movie_id", movie.getMovieId());
                    intent.putExtra("movie_title", movie.getTitle());
                    startActivity(intent);
                },
                movie -> repository.removeFromWatchlist(movie.getMovieId(), () -> {
                    if (getActivity() != null)
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(),
                                        movie.getTitle() + " dihapus dari watchlist",
                                        Toast.LENGTH_SHORT).show());
                })
        );
        binding.rvWatchlist.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvWatchlist.setAdapter(adapter);
    }

    private void observeWatchlist() {
        repository.getWatchlist().observe(getViewLifecycleOwner(), movies -> {
            adapter.setMovies(movies);

            boolean isEmpty = movies == null || movies.isEmpty();
            binding.rvWatchlist.setVisibility(isEmpty ? View.GONE  : View.VISIBLE);
            binding.layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            binding.tvWatchlistCount.setText(
                    isEmpty ? "" : movies.size() + " film tersimpan");
        });
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}