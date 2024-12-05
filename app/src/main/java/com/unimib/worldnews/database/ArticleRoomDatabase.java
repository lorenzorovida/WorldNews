package com.unimib.worldnews.database;


import static com.unimib.worldnews.util.Constants.DATABASE_VERSION;

import android.content.Context;
import com.unimib.worldnews.util.Constants;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.unimib.worldnews.model.Article;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main access point for the underlying connection to the local database.
 * <a href="https://developer.android.com/reference/kotlin/androidx/room/Database">...</a>
 */
@Database(entities = {Article.class}, version = DATABASE_VERSION, exportSchema = true)
public abstract class ArticleRoomDatabase extends RoomDatabase {

    public abstract ArticleDAO articleDao();

    private static volatile ArticleRoomDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static ArticleRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ArticleRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ArticleRoomDatabase.class, Constants.SAVED_ARTICLES_DATABASE)
                            .allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}
