package com.cineai.app.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Video {
    @SerializedName("id")
    private String id;

    @SerializedName("key")
    private String key;

    @SerializedName("name")
    private String name;

    @SerializedName("site")
    private String site;

    @SerializedName("type")
    private String type;

    @SerializedName("official")
    private boolean official;

    public String getId() { return id; }
    public String getKey() { return key; }
    public String getName() { return name; }
    public String getSite() { return site; }
    public String getType() { return type; }
    public boolean isOfficial() { return official; }

    public String getYoutubeUrl() {
        return "https://www.youtube.com/watch?v=" + key;
    }

    public boolean isTrailer() {
        return "Trailer".equals(type) && "YouTube".equals(site);
    }

    public static class VideoResponse {
        @SerializedName("id")
        private int movieId;

        @SerializedName("results")
        private List<Video> results;

        public int getMovieId() { return movieId; }
        public List<Video> getResults() { return results; }
    }
}