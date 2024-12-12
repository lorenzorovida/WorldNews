package com.unimib.worldnews.source;

import static com.unimib.worldnews.util.Constants.UNEXPECTED_ERROR;

import com.unimib.worldnews.database.ArticleDAO;
import com.unimib.worldnews.database.ArticleRoomDatabase;
import com.unimib.worldnews.model.Article;
import com.unimib.worldnews.util.Constants;
import com.unimib.worldnews.util.SharedPreferencesUtils;

import java.util.List;


/**
 * Class to get news from local database using Room.
 */
public class ArticleLocalDataSource extends BaseArticleLocalDataSource {

    private final ArticleDAO articleDAO;
    private final SharedPreferencesUtils sharedPreferencesUtil;

    public ArticleLocalDataSource(ArticleRoomDatabase newsRoomDatabase,
                               SharedPreferencesUtils sharedPreferencesUtil) {
        this.articleDAO = newsRoomDatabase.articleDao();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    /**
     * Gets the news from the local database.
     * The method is executed with an ExecutorService defined in NewsRoomDatabase class
     * because the database access cannot been executed in the main thread.
     */
    @Override
    public void getArticles() {
        ArticleRoomDatabase.databaseWriteExecutor.execute(() -> {
            articleCallback.onSuccessFromLocal(articleDAO.getAll());
        });
    }

    @Override
    public void getFavoriteArticles() {
        ArticleRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Article> favoriteNews = articleDAO.getLiked();
            articleCallback.onNewsFavoriteStatusChanged(favoriteNews);
        });
    }

    @Override
    public void updateArticle(Article article) {
        ArticleRoomDatabase.databaseWriteExecutor.execute(() -> {
            int rowUpdatedCounter = articleDAO.updateArticle(article);

            // It means that the update succeeded because only one row had to be updated
            if (rowUpdatedCounter == 1) {
                Article updatedNews = articleDAO.getArticle(article.getUid());
                articleCallback.onNewsFavoriteStatusChanged(updatedNews, articleDAO.getLiked());
            } else {
                articleCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
            }
        });
    }

    @Override
    public void deleteFavoriteArticles() {
        ArticleRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Article> favoriteArticles = articleDAO.getLiked();
            for (Article article : favoriteArticles) {
                article.setLiked(false);
            }
            int updatedRowsNumber = articleDAO.updateListFavoriteArticles(favoriteArticles);

            // It means that the update succeeded because the number of updated rows is
            // equal to the number of the original favorite news
            if (updatedRowsNumber == favoriteArticles.size()) {
                articleCallback.onDeleteFavoriteNewsSuccess(favoriteArticles);
            } else {
                articleCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
            }
        });
    }

    /**
     * Saves the news in the local database.
     * The method is executed with an ExecutorService defined in NewsRoomDatabase class
     * because the database access cannot been executed in the main thread.
     * @param newsList the list of news to be written in the local database.
     */
    @Override
    public void insertArticles(List<Article> articlesList) {
        ArticleRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Reads the news from the database
            List<Article> allArticles = articleDAO.getAll();

            if (articlesList != null) {

                // Checks if the news just downloaded has already been downloaded earlier
                // in order to preserve the news status (marked as favorite or not)
                for (Article article : allArticles) {
                    // This check works because News and NewsSource classes have their own
                    // implementation of equals(Object) and hashCode() methods
                    if (articlesList.contains(article)) {
                        // The primary key and the favorite status is contained only in the News objects
                        // retrieved from the database, and not in the News objects downloaded from the
                        // Web Service. If the same news was already downloaded earlier, the following
                        // line of code replaces the News object in newsList with the corresponding
                        // line of code replaces the News object in newsList with the corresponding
                        // News object saved in the database, so that it has the primary key and the
                        // favorite status.
                        articlesList.set(articlesList.indexOf(article), article);
                    }
                }

                // Writes the news in the database and gets the associated primary keys
                List<Long> insertedNewsIds = articleDAO.insertNewsList(articlesList);
                for (int i = 0; i < articlesList.size(); i++) {
                    // Adds the primary key to the corresponding object News just downloaded so that
                    // if the user marks the news as favorite (and vice-versa), we can use its id
                    // to know which news in the database must be marked as favorite/not favorite
                    articlesList.get(i).setUid(insertedNewsIds.get(i));
                }

                sharedPreferencesUtil.writeStringData(Constants.SHARED_PREFERENCES_FILENAME,
                        Constants.SHARED_PREFERNECES_LAST_UPDATE, String.valueOf(System.currentTimeMillis()));

                articleCallback.onSuccessFromLocal(articlesList);
            }
        });
    }
}
