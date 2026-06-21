package com.cineai.app.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Movie {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("vote_count")
    private int voteCount;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("genre_ids")
    private List<Integer> genreIds;

    @SerializedName("genres")
    private List<Genre> genres;

    @SerializedName("runtime")
    private int runtime;

    @SerializedName("popularity")
    private double popularity;

    @SerializedName("original_language")
    private String originalLanguage;

    @SerializedName("tagline")
    private String tagline;

    @SerializedName("status")
    private String status;

    @SerializedName("budget")
    private long budget;

    @SerializedName("revenue")
    private long revenue;

    private boolean isInWatchlist = false;

    // Constructors
    public Movie() {}

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath; }
    public String getBackdropPath() { return backdropPath; }
    public double getVoteAverage() { return voteAverage; }
    public int getVoteCount() { return voteCount; }
    public String getReleaseDate() { return releaseDate; }
    public List<Integer> getGenreIds() { return genreIds; }
    public List<Genre> getGenres() { return genres; }
    public int getRuntime() { return runtime; }
    public double getPopularity() { return popularity; }
    public String getOriginalLanguage() { return originalLanguage; }
    public String getTagline() { return tagline; }
    public String getStatus() { return status; }
    public long getBudget() { return budget; }
    public long getRevenue() { return revenue; }
    public boolean isInWatchlist() { return isInWatchlist; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setOverview(String overview) { this.overview = overview; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
    public void setBackdropPath(String backdropPath) { this.backdropPath = backdropPath; }
    public void setVoteAverage(double voteAverage) { this.voteAverage = voteAverage; }
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public void setGenreIds(List<Integer> genreIds) { this.genreIds = genreIds; }
    public void setGenres(List<Genre> genres) { this.genres = genres; }
    public void setRuntime(int runtime) { this.runtime = runtime; }
    public void setInWatchlist(boolean inWatchlist) { isInWatchlist = inWatchlist; }

    public String getFullPosterUrl() {
        if (posterPath == null || posterPath.isEmpty()) return null;
        return "https://image.tmdb.org/t/p/w500" + posterPath;
    }

    public String getFullBackdropUrl() {
        if (backdropPath == null || backdropPath.isEmpty()) return null;
        return "https://image.tmdb.org/t/p/w1280" + backdropPath;
    }

    public String getReleaseYear() {
        if (releaseDate == null || releaseDate.length() < 4) return "N/A";
        return releaseDate.substring(0, 4);
    }

    public String getFormattedRating() {
        return String.format("%.1f", voteAverage);
    }

    public String getFormattedRuntime() {
        if (runtime <= 0) return "N/A";
        int hours = runtime / 60;
        int minutes = runtime % 60;
        if (hours > 0) return hours + "j " + minutes + "m";
        return minutes + "m";
    }

    // Check if movie is currently in theaters (status from TMDB)
    public boolean isNowPlaying() {
        return "Released".equals(status) && isRecentRelease();
    }

    private boolean isRecentRelease() {
        if (releaseDate == null || releaseDate.isEmpty()) return false;
        try {
            int year = Integer.parseInt(releaseDate.substring(0, 4));
            int month = Integer.parseInt(releaseDate.substring(5, 7));
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int currentYear = cal.get(java.util.Calendar.YEAR);
            int currentMonth = cal.get(java.util.Calendar.MONTH) + 1;
            // Consider "now playing" if released within last 2 months
            int totalMonths = year * 12 + month;
            int currentTotalMonths = currentYear * 12 + currentMonth;
            return (currentTotalMonths - totalMonths) <= 2 && totalMonths <= currentTotalMonths;
        } catch (Exception e) {
            return false;
        }
    }
}