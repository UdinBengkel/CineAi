package com.cineai.app.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class StreamingProvider {

    @SerializedName("link")
    private String link;

    @SerializedName("flatrate")
    private List<Provider> flatrate;

    @SerializedName("rent")
    private List<Provider> rent;

    @SerializedName("buy")
    private List<Provider> buy;

    public String getLink() { return link; }
    public List<Provider> getFlatrate() { return flatrate; }
    public List<Provider> getRent() { return rent; }
    public List<Provider> getBuy() { return buy; }

    public static class Provider {
        @SerializedName("provider_id")
        private int providerId;

        @SerializedName("provider_name")
        private String providerName;

        @SerializedName("logo_path")
        private String logoPath;

        public int getProviderId() { return providerId; }
        public String getProviderName() { return providerName; }
        public String getLogoPath() { return logoPath; }

        public String getFullLogoUrl() {
            if (logoPath == null) return null;
            return "https://image.tmdb.org/t/p/w92" + logoPath;
        }
    }

    public static class ProvidersResponse {
        @SerializedName("id")
        private int movieId;

        @SerializedName("results")
        private Map<String, StreamingProvider> results;

        public int getMovieId() { return movieId; }
        public Map<String, StreamingProvider> getResults() { return results; }

        // Get Indonesia providers (ID) with fallback to US
        public StreamingProvider getIndonesiaProvider() {
            if (results == null) return null;
            StreamingProvider id = results.get("ID");
            if (id != null) return id;
            return results.get("US");
        }
    }
}