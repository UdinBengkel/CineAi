package com.cineai.app.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.cineai.app.databinding.FragmentSearchBinding;
import com.cineai.app.model.Genre;
import com.cineai.app.model.MovieResponse;
import com.cineai.app.repository.MovieRepository;
import com.cineai.app.ui.adapter.MovieGridAdapter;
import com.cineai.app.ui.detail.MovieDetailActivity;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private MovieRepository repository;
    private MovieGridAdapter adapter;

    private List<Genre> genres = new ArrayList<>();
    private String selectedGenreId = null;
    private String selectedYear    = null;

    // TMDB genre IDs untuk genre cards
    private static final int GENRE_ACTION    = 28;
    private static final int GENRE_SCIFI     = 878;
    private static final int GENRE_COMEDY    = 35;
    private static final int GENRE_HORROR    = 27;
    private static final int GENRE_DRAMA     = 18;
    private static final int GENRE_ANIMATION = 16;
    private static final int GENRE_THRILLER  = 53;
    private static final int GENRE_ROMANCE   = 10749;

    private final Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = MovieRepository.getInstance(requireContext());

        setupRecyclerView();
        setupSearchBar();
        setupFilterPanel();
        setupGenreCards();
        loadGenreChips();

        // Default: tampilkan genre grid
        showGenreGrid();
    }

    // ─── RecyclerView ────────────────────────────────────────

    private void setupRecyclerView() {
        adapter = new MovieGridAdapter(new ArrayList<>(), movie -> {
            Intent intent = new Intent(getContext(), MovieDetailActivity.class);
            intent.putExtra("movie_id", movie.getId());
            intent.putExtra("movie_title", movie.getTitle());
            startActivity(intent);
        });
        binding.rvResults.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvResults.setAdapter(adapter);
    }

    // ─── Search bar ──────────────────────────────────────────

    private void setupSearchBar() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                final String query = s.toString().trim();
                if (query.isEmpty()) {
                    // Balik ke genre grid
                    showGenreGrid();
                    return;
                }
                searchRunnable = () -> performSearch(query);
                searchHandler.postDelayed(searchRunnable, 600);
            }
        });

        // Filter icon toggle
        binding.btnFilterIcon.setOnClickListener(v -> {
            boolean visible = binding.layoutFilter.getVisibility() == View.VISIBLE;
            binding.layoutFilter.setVisibility(visible ? View.GONE : View.VISIBLE);
        });
    }

    // ─── Filter panel ────────────────────────────────────────

    private void setupFilterPanel() {
        setupYearSpinner();

        binding.btnApplyFilter.setOnClickListener(v -> {
            binding.layoutFilter.setVisibility(View.GONE);
            applyFilter();
        });

        binding.btnClearFilter.setOnClickListener(v -> {
            selectedGenreId = null;
            selectedYear = null;
            binding.spinnerYear.setSelection(0);
            clearGenreChipSelection();
            showGenreGrid();
        });
    }

    private void setupYearSpinner() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> years = new ArrayList<>();
        years.add("Semua Tahun");
        for (int y = currentYear; y >= 1990; y--) years.add(String.valueOf(y));

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerYear.setAdapter(yearAdapter);
        binding.spinnerYear.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override public void onItemSelected(android.widget.AdapterView<?> p, View v, int pos, long id) {
                        selectedYear = pos == 0 ? null : years.get(pos);
                    }
                    @Override public void onNothingSelected(android.widget.AdapterView<?> p) {}
                });
    }

    // ─── Genre cards ─────────────────────────────────────────

    private void setupGenreCards() {
        binding.genreAction.setOnClickListener(v    -> searchByGenre(GENRE_ACTION,    "Aksi"));
        binding.genreScifi.setOnClickListener(v     -> searchByGenre(GENRE_SCIFI,     "Sci-Fi"));
        binding.genreComedy.setOnClickListener(v    -> searchByGenre(GENRE_COMEDY,    "Komedi"));
        binding.genreHorror.setOnClickListener(v    -> searchByGenre(GENRE_HORROR,    "Horor"));
        binding.genreDrama.setOnClickListener(v     -> searchByGenre(GENRE_DRAMA,     "Drama"));
        binding.genreAnimation.setOnClickListener(v -> searchByGenre(GENRE_ANIMATION, "Animasi"));
        binding.genreThriller.setOnClickListener(v  -> searchByGenre(GENRE_THRILLER,  "Thriller"));
        binding.genreRomance.setOnClickListener(v   -> searchByGenre(GENRE_ROMANCE,   "Romansa"));
    }

    private void searchByGenre(int genreId, String genreName) {
        // Set search bar hint agar user tahu filter aktif
        binding.etSearch.setHint(genreName);
        selectedGenreId = String.valueOf(genreId);
        applyFilter();
    }

    // ─── Genre chips (filter panel) ──────────────────────────

    private void loadGenreChips() {
        repository.getGenres(new MovieRepository.MovieCallback<com.cineai.app.model.GenreListResponse>() {
            @Override
            public void onSuccess(com.cineai.app.model.GenreListResponse data) {
                // Fix: Map network Genre to model Genre
                List<com.cineai.app.model.Genre> mappedGenres = new ArrayList<>();
                if (data.getGenres() != null) {
                    for (com.cineai.app.model.GenreListResponse.Genre networkGenre : data.getGenres()) {
                        // Assuming your model Genre has a constructor (id, name)
                        mappedGenres.add(new com.cineai.app.model.Genre(
                                networkGenre.getId(),
                                networkGenre.getName()
                        ));
                    }
                }

                genres = mappedGenres; // Now the types match

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> buildGenreChips());
                }
            }

            @Override
            public void onError(String message) {
                // Handle error
            }
        });
    }

    private void buildGenreChips() {
        binding.chipGroupGenres.removeAllViews();
        for (Genre g : genres) {
            Chip chip = new Chip(requireContext());
            chip.setText(g.getName());
            chip.setCheckable(true);
            chip.setTag(String.valueOf(g.getId()));
            chip.setOnCheckedChangeListener((btn, checked) -> {
                if (checked) {
                    selectedGenreId = (String) btn.getTag();
                    for (int i = 0; i < binding.chipGroupGenres.getChildCount(); i++) {
                        Chip c = (Chip) binding.chipGroupGenres.getChildAt(i);
                        if (c != btn) c.setChecked(false);
                    }
                } else if (String.valueOf(g.getId()).equals(selectedGenreId)) {
                    selectedGenreId = null;
                }
            });
            binding.chipGroupGenres.addView(chip);
        }
    }

    private void clearGenreChipSelection() {
        for (int i = 0; i < binding.chipGroupGenres.getChildCount(); i++)
            ((Chip) binding.chipGroupGenres.getChildAt(i)).setChecked(false);
    }

    // ─── Search & Filter ─────────────────────────────────────

    private void performSearch(String query) {
        binding.progressBar.setVisibility(View.VISIBLE);
        showResultsGrid();

        repository.searchMovies(query, 1, selectedYear, new MovieRepository.MovieCallback<MovieResponse>() {
            @Override public void onSuccess(MovieResponse data) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    adapter.setMovies(data.getResults());
                    binding.tvResultCount.setText(data.getTotalResults() + " hasil");
                    binding.tvResultCount.setVisibility(View.VISIBLE);
                });
            }
            @Override public void onError(String message) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void applyFilter() {
        binding.progressBar.setVisibility(View.VISIBLE);
        showResultsGrid();

        String query = binding.etSearch.getText().toString().trim();
        if (!query.isEmpty()) { performSearch(query); return; }

        repository.discoverMovies(selectedGenreId, selectedYear, "popularity.desc", 0f, 1,
                new MovieRepository.MovieCallback<MovieResponse>() {
                    @Override public void onSuccess(MovieResponse data) {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            binding.progressBar.setVisibility(View.GONE);
                            adapter.setMovies(data.getResults());
                            binding.tvResultCount.setText(data.getTotalResults() + " film");
                            binding.tvResultCount.setVisibility(View.VISIBLE);
                        });
                    }
                    @Override public void onError(String msg) {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    // ─── Show/hide helpers ───────────────────────────────────

    private void showGenreGrid() {
        binding.layoutGenreGrid.setVisibility(View.VISIBLE);
        binding.rvResults.setVisibility(View.GONE);
        binding.tvResultCount.setVisibility(View.GONE);
        binding.etSearch.setHint(getString(com.cineai.app.R.string.search_hint));
    }

    private void showResultsGrid() {
        binding.layoutGenreGrid.setVisibility(View.GONE);
        binding.rvResults.setVisibility(View.VISIBLE);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}