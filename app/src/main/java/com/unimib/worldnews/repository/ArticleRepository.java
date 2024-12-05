package com.unimib.worldnews.repository;

import static com.unimib.worldnews.util.Constants.FRESH_TIMEOUT;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;

import com.unimib.worldnews.R;
import com.unimib.worldnews.database.ArticleDAO;
import com.unimib.worldnews.database.ArticleRoomDatabase;
import com.unimib.worldnews.model.Article;
import com.unimib.worldnews.model.ArticleAPIResponse;
import com.unimib.worldnews.service.ArticleAPIService;
import com.unimib.worldnews.service.ServiceLocator;
import com.unimib.worldnews.util.Constants;
import com.unimib.worldnews.util.ResponseCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleRepository implements IArticleRepository {

    private static final String TAG = ArticleRepository.class.getSimpleName();

    private final Application application;
    private final ArticleAPIService articleAPIService;
    private final ArticleDAO articleDAO;
    private final ResponseCallback responseCallback;

    public ArticleRepository(Application application, ResponseCallback responseCallback) {
        this.application = application;
        this.articleAPIService = ServiceLocator.getInstance().getArticleAPIService();
        this.articleDAO = ServiceLocator.getInstance().getArticlesDB(application).articleDao();
        this.responseCallback = responseCallback;
    }

    @Override
    public void fetchArticles(String country, int page, long lastUpdate) {

        long currentTime = System.currentTimeMillis();

        // It gets the news from the Web Service if the last download
        // of the news has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            Call<ArticleAPIResponse> articleResponseCall = articleAPIService.getArticles(country,
                    Constants.TOP_HEADLINES_PAGE_SIZE_VALUE,
                    application.getString(R.string.news_api_key));

            articleResponseCall.enqueue(new Callback<ArticleAPIResponse>() {
                @Override
                public void onResponse(@NonNull Call<ArticleAPIResponse> call,
                                       @NonNull Response<ArticleAPIResponse> response) {

                    if (response.body() != null && response.isSuccessful() &&
                            !response.body().getStatus().equals("error")) {
                        List<Article> articleList = response.body().getArticles();
                        Article.filterArticles(articleList);
                        saveDataInDatabase(articleList);
                    } else {
                        responseCallback.onFailure(application.getString(R.string.error_retrieving_news));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArticleAPIResponse> call, @NonNull Throwable t) {
                    readDataFromDatabase(lastUpdate);
                }
            });
        } else {
            readDataFromDatabase(lastUpdate);
        }
    }

    @Override
    public void deleteFavoriteArticles() {
        ArticleRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Article> favoriteArticles = articleDAO.getLiked();
            for (Article article : favoriteArticles) {
                article.setLiked(false);
            }
            //TODO newsDao.updateListFavoriteNews(favoriteNews);
            responseCallback.onSuccess(articleDAO.getLiked(), System.currentTimeMillis());
        });
    }

    @Override
    public void updateArticles(Article article) {
        ArticleRoomDatabase.databaseWriteExecutor.execute(() -> {
            articleDAO.updateArticle(article);
            //TODO responseCallback.onNewsFavoriteStatusChanged(news);
        });
    }

    /**
     * Gets the list of favorite news from the local database.
     */
    @Override
    public void getFavoriteArticles() {
        ArticleRoomDatabase.databaseWriteExecutor.execute(() -> {
            responseCallback.onSuccess(articleDAO.getLiked(), System.currentTimeMillis());
        });
    }

    private void saveDataInDatabase(List<Article> articleList) {
        ArticleRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Reads the news from the database
            List<Article> allArticles = articleDAO.getAll();

            // Checks if the news just downloaded has already been downloaded earlier
            // in order to preserve the news status (marked as favorite or not)
            for (Article article : allArticles) {
                // This check works because News and NewsSource classes have their own
                // implementation of equals(Object) and hashCode() methods
                if (articleList.contains(article)) {
                    // The primary key and the favorite status is contained only in the News objects
                    // retrieved from the database, and not in the News objects downloaded from the
                    // Web Service. If the same news was already downloaded earlier, the following
                    // line of code replaces the the News object in newsList with the corresponding
                    // News object saved in the database, so that it has the primary key and the
                    // favorite status.
                    articleList.set(articleList.indexOf(article), article);
                }
            }

            // Writes the news in the database and gets the associated primary keys
            List<Long> insertedNewsIds = articleDAO.insertNewsList(articleList);
            for (int i = 0; i < articleList.size(); i++) {
                // Adds the primary key to the corresponding object News just downloaded so that
                // if the user marks the news as favorite (and vice-versa), we can use its id
                // to know which news in the database must be marked as favorite/not favorite
                articleList.get(i).setUid(insertedNewsIds.get(i));
            }

            responseCallback.onSuccess(articleList, System.currentTimeMillis());
        });
    }

    private void readDataFromDatabase(long lastUpdate) {
        ArticleRoomDatabase.databaseWriteExecutor.execute(() -> {
            responseCallback.onSuccess(articleDAO.getAll(), lastUpdate);
        });
    }
}
