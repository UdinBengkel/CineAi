package com.cineai.app.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Genre {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public Genre() {}
    public Genre(int id, String name) { this.id = id; this.name = name; }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
}