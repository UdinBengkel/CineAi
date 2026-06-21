package com.cineai.app.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;

import com.cineai.app.BuildConfig;
import com.cineai.app.model.GenreListResponse;
import com.cineai.app.database.AppDatabase;
import com.cineai.app.database.WatchlistDao;
import com.cineai.app.model.Movie;
import com.cineai.app.model.MovieResponse;
import com.cineai.app.model.StreamingProvider;
import com.cineai.app.model.Video;
import com.cineai.app.model.WatchlistMovie;
import com.cineai.app.network.ApiClient;
import com.cineai.app.network.TmdbApiService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {

    private static MovieRepository instance;
    private final TmdbApiService apiService;
    private final WatchlistDao watchlistDao;
    private final ExecutorService executor;
    private final String apiKey;
    private static final String LANGUAGE = "id-ID";
    private static final String REGION = "ID";

    public interface MovieCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

    private MovieRepository(Context context) {
        apiService = ApiClient.getService();
        AppDatabase db = AppDatabase.getInstance(context);
        watchlistDao = db.watchlistDao();
        executor = Executors.newFixedThreadPool(4);
        apiKey = BuildConfig.TMDB_API_KEY;
    }

    public static synchronized MovieRepository getInstance(Context context) {
        if (instance == null) instance = new MovieRepository(context);
        return instance;
    }

    // ─── Fetch Methods ────────────────────────────────────────

    public void getNowPlaying(int page, MovieCallback<MovieResponse> callback) {
        apiService.getNowPlaying(apiKey, LANGUAGE, page, REGION)
                .enqueue(new Callback<MovieResponse>() {
                    @Override public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null)
                            callback.onSuccess(response.body());
                        else callback.onError("Gagal memuat data");
                    }
                    @Override public void onFailure(Call<MovieResponse> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public void getPopular(int page, MovieCallback<MovieResponse> callback) {
        apiService.getPopular(apiKey, LANGUAGE, page)
                .enqueue(new Callback<MovieResponse>() {
                    @Override public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null)
                            callback.onSuccess(response.body());
                        else callback.onError("Gagal memuat data");
                    }
                    @Override public void onFailure(Call<MovieResponse> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public void getTopRated(int page, MovieCallback<MovieResponse> callback) {
        apiService.getTopRated(apiKey, LANGUAGE, page)
                .enqueue(new Callback<MovieResponse>() {
                    @Override public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null)
                            callback.onSuccess(response.body());
                        else callback.onError("Gagal memuat data");
                    }
                    @Override public void onFailure(Call<MovieResponse> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public void getMovieDetail(int movieId, MovieCallback<Movie> callback) {
        apiService.getMovieDetail(movieId, apiKey, LANGUAGE)
                .enqueue(new Callback<Movie>() {
                    @Override public void onResponse(Call<Movie> call, Response<Movie> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Movie movie = response.body();
                            executor.execute(() -> {
                                movie.setInWatchlist(watchlistDao.isInWatchlist(movie.getId()));
                                callback.onSuccess(movie);
                            });
                        } else callback.onError("Detail tidak ditemukan");
                    }
                    @Override public void onFailure(Call<Movie> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public void getMovieVideos(int movieId, MovieCallback<Video.VideoResponse> callback) {
        // Try Indonesian first, fallback to English
        apiService.getMovieVideos(movieId, apiKey, LANGUAGE)
                .enqueue(new Callback<Video.VideoResponse>() {
                    @Override public void onResponse(Call<Video.VideoResponse> call, Response<Video.VideoResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Video.VideoResponse vr = response.body();
                            boolean hasTrailer = vr.getResults() != null &&
                                    vr.getResults().stream().anyMatch(Video::isTrailer);
                            if (!hasTrailer) {
                                // Fallback to English
                                apiService.getMovieVideos(movieId, apiKey, "en-US")
                                        .enqueue(new Callback<Video.VideoResponse>() {
                                            @Override public void onResponse(Call<Video.VideoResponse> call2, Response<Video.VideoResponse> r2) {
                                                if (r2.isSuccessful() && r2.body() != null)
                                                    callback.onSuccess(r2.body());
                                                else callback.onSuccess(vr);
                                            }
                                            @Override public void onFailure(Call<Video.VideoResponse> call2, Throwable t) {
                                                callback.onSuccess(vr);
                                            }
                                        });
                            } else {
                                callback.onSuccess(vr);
                            }
                        } else callback.onError("Video tidak ditemukan");
                    }
                    @Override public void onFailure(Call<Video.VideoResponse> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public void getWatchProviders(int movieId, MovieCallback<StreamingProvider.ProvidersResponse> callback) {
        apiService.getWatchProviders(movieId, apiKey)
                .enqueue(new Callback<StreamingProvider.ProvidersResponse>() {
                    @Override public void onResponse(Call<StreamingProvider.ProvidersResponse> call, Response<StreamingProvider.ProvidersResponse> response) {
                        if (response.isSuccessful() && response.body() != null)
                            callback.onSuccess(response.body());
                        else callback.onError("Provider tidak ditemukan");
                    }
                    @Override public void onFailure(Call<StreamingProvider.ProvidersResponse> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public void searchMovies(String query, int page, String year, MovieCallback<MovieResponse> callback) {
        apiService.searchMovies(apiKey, query, LANGUAGE, page, year)
                .enqueue(new Callback<MovieResponse>() {
                    @Override public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null)
                            callback.onSuccess(response.body());
                        else callback.onError("Pencarian gagal");
                    }
                    @Override public void onFailure(Call<MovieResponse> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public void discoverMovies(String genreIds, String year, String sortBy, float minRating, int page, MovieCallback<MovieResponse> callback) {
        apiService.discoverMovies(apiKey, LANGUAGE, page, genreIds, year, sortBy, minRating)
                .enqueue(new Callback<MovieResponse>() {
                    @Override public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null)
                            callback.onSuccess(response.body());
                        else callback.onError("Filter gagal diterapkan");
                    }
                    @Override public void onFailure(Call<MovieResponse> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public void getSimilarMovies(int movieId, int page, MovieCallback<MovieResponse> callback) {
        apiService.getSimilarMovies(movieId, apiKey, LANGUAGE, page)
                .enqueue(new Callback<MovieResponse>() {
                    @Override public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null)
                            callback.onSuccess(response.body());
                        else callback.onError("Gagal memuat rekomendasi");
                    }
                    @Override public void onFailure(Call<MovieResponse> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public void getRecommendations(int movieId, int page, MovieCallback<MovieResponse> callback) {
        apiService.getRecommendations(movieId, apiKey, LANGUAGE, page)
                .enqueue(new Callback<MovieResponse>() {
                    @Override public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                        if (response.isSuccessful() && response.body() != null)
                            callback.onSuccess(response.body());
                        else callback.onError("Gagal memuat rekomendasi");
                    }
                    @Override public void onFailure(Call<MovieResponse> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public void getGenres(MovieCallback<GenreListResponse> callback) {
        apiService.getGenreList(apiKey, LANGUAGE)
                .enqueue(new Callback<GenreListResponse>() {
                    @Override public void onResponse(Call<GenreListResponse> call, Response<GenreListResponse> response) {
                        if (response.isSuccessful() && response.body() != null)
                            callback.onSuccess(response.body());
                        else callback.onError("Gagal memuat genre");
                    }
                    @Override public void onFailure(Call<GenreListResponse> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    // ─── Watchlist Methods ────────────────────────────────────

    public LiveData<List<WatchlistMovie>> getWatchlist() {
        return watchlistDao.getAllWatchlist();
    }

    public void addToWatchlist(Movie movie, Runnable onDone) {
        executor.execute(() -> {
            watchlistDao.insert(new WatchlistMovie(movie));
            if (onDone != null) onDone.run();
        });
    }

    public void removeFromWatchlist(int movieId, Runnable onDone) {
        executor.execute(() -> {
            watchlistDao.deleteByMovieId(movieId);
            if (onDone != null) onDone.run();
        });
    }

    public void isInWatchlist(int movieId, MovieCallback<Boolean> callback) {
        executor.execute(() -> callback.onSuccess(watchlistDao.isInWatchlist(movieId)));
    }

    public List<WatchlistMovie> getWatchlistSync() {
        return watchlistDao.getAllWatchlistSync();
    }
}