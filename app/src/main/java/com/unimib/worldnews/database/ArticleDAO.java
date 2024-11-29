package com.unimib.worldnews.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.unimib.worldnews.model.Article;

import java.util.List;

@Dao
public interface ArticleDAO {
    @Query("SELECT * FROM Article")
    List<Article> getAll();

    @Query("SELECT * FROM Article WHERE liked = 1")
    List<Article> getLiked();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Article... articles);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Article> articles);

    @Update
    void updateArticle(Article article);

    @Delete
    void delete(Article user);

    @Query("DELETE from Article WHERE liked = 0")
    void deleteCached();

    @Query("DELETE from Article")
    void deleteAll();


}
