package com.cineai.app.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cineai.app.model.WatchlistMovie;

import java.util.List;

@Dao
public interface WatchlistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WatchlistMovie movie);

    @Delete
    void delete(WatchlistMovie movie);

    @Query("SELECT * FROM watchlist ORDER BY added_at DESC")
    LiveData<List<WatchlistMovie>> getAllWatchlist();

    @Query("SELECT * FROM watchlist ORDER BY added_at DESC")
    List<WatchlistMovie> getAllWatchlistSync();

    @Query("SELECT * FROM watchlist WHERE movie_id = :movieId LIMIT 1")
    WatchlistMovie getByMovieId(int movieId);

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE movie_id = :movieId)")
    boolean isInWatchlist(int movieId);

    @Query("SELECT COUNT(*) FROM watchlist")
    int getCount();

    @Query("DELETE FROM watchlist WHERE movie_id = :movieId")
    void deleteByMovieId(int movieId);
}