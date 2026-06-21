package com.cineai.app.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "watchlist")
public class WatchlistMovie {

    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    private int movieId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "poster_path")
    private String posterPath;

    @ColumnInfo(name = "vote_average")
    private double voteAverage;

    @ColumnInfo(name = "release_date")
    private String releaseDate;

    @ColumnInfo(name = "overview")
    private String overview;

    @ColumnInfo(name = "genre_ids")
    private String genreIdsJson;

    @ColumnInfo(name = "added_at")
    private long addedAt;

    public WatchlistMovie() {}

    public WatchlistMovie(Movie movie) {
        this.movieId     = movie.getId();
        this.title       = movie.getTitle();
        this.posterPath  = movie.getPosterPath();
        this.voteAverage = movie.getVoteAverage();
        this.releaseDate = movie.getReleaseDate();
        this.overview    = movie.getOverview();
        this.addedAt     = System.currentTimeMillis();

        // PERBAIKAN: Prioritaskan getGenres() karena detail page mengembalikan
        // object Genre lengkap, bukan hanya genre_ids integer
        if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            this.genreIdsJson = buildFromGenreObjects(movie.getGenres());
        } else if (movie.getGenreIds() != null && !movie.getGenreIds().isEmpty()) {
            this.genreIdsJson = buildFromGenreIds(movie.getGenreIds());
        } else {
            this.genreIdsJson = "[]";
        }
    }

    private String buildFromGenreObjects(List<Genre> genres) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < genres.size(); i++) {
            sb.append(genres.get(i).getId());
            if (i < genres.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    private String buildFromGenreIds(List<Integer> ids) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < ids.size(); i++) {
            sb.append(ids.get(i));
            if (i < ids.size() - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    public int getMovieId() { return movieId; }
    public void setMovieId(int id) { this.movieId = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String p) { this.posterPath = p; }
    public double getVoteAverage() { return voteAverage; }
    public void setVoteAverage(double v) { this.voteAverage = v; }
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String d) { this.releaseDate = d; }
    public String getOverview() { return overview; }
    public void setOverview(String o) { this.overview = o; }
    public String getGenreIdsJson() { return genreIdsJson; }
    public void setGenreIdsJson(String j) { this.genreIdsJson = j; }
    public long getAddedAt() { return addedAt; }
    public void setAddedAt(long t) { this.addedAt = t; }

    public String getFullPosterUrl() {
        if (posterPath == null || posterPath.isEmpty()) return null;
        return "https://image.tmdb.org/t/p/w500" + posterPath;
    }
    public String getReleaseYear() {
        if (releaseDate == null || releaseDate.length() < 4) return "N/A";
        return releaseDate.substring(0, 4);
    }
    public String getFormattedRating() {
        return String.format("%.1f", voteAverage);
    }
    public Movie toMovie() {
        Movie m = new Movie();
        m.setId(movieId); m.setTitle(title); m.setPosterPath(posterPath);
        m.setVoteAverage(voteAverage); m.setReleaseDate(releaseDate);
        m.setOverview(overview); m.setInWatchlist(true);
        return m;
    }
}