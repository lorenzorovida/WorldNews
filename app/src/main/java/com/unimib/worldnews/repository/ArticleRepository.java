package com.unimib.worldnews.repository;
import static com.unimib.worldnews.util.Constants.FRESH_TIMEOUT;

import androidx.lifecycle.MutableLiveData;

import com.unimib.worldnews.model.Article;
import com.unimib.worldnews.model.ArticleAPIResponse;
import com.unimib.worldnews.model.Result;
import com.unimib.worldnews.source.ArticleCallback;
import com.unimib.worldnews.source.BaseArticleLocalDataSource;
import com.unimib.worldnews.source.BaseArticleRemoteDataSource;

import java.util.List;


/**
 * Repository class to get the news from local or from a remote source.
 */
public class ArticleRepository implements ArticleCallback {

    private static final String TAG = ArticleRepository.class.getSimpleName();

    private final MutableLiveData<Result> allArticlesMutableLiveData;
    private final MutableLiveData<Result> favoriteNewsMutableLiveData;
    private final BaseArticleRemoteDataSource articleRemoteDataSource;
    private final BaseArticleLocalDataSource articleLocalDataSource;

    public ArticleRepository(BaseArticleRemoteDataSource articleRemoteDataSource,
                             BaseArticleLocalDataSource articleLocalDataSource) {

        allArticlesMutableLiveData = new MutableLiveData<>();
        favoriteNewsMutableLiveData = new MutableLiveData<>();
        this.articleRemoteDataSource = articleRemoteDataSource;
        this.articleLocalDataSource = articleLocalDataSource;
        this.articleRemoteDataSource.setArticleCallback(this);
        this.articleLocalDataSource.setArticleCallback(this);
    }

    public MutableLiveData<Result> fetchArticles(String country, int page, long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        // It gets the news from the Web Service if the last download
        // of the news has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate > FRESH_TIMEOUT) {
            articleRemoteDataSource.getArticles(country);
        } else {
            articleLocalDataSource.getArticles();
        }

        return allArticlesMutableLiveData;
    }

    public MutableLiveData<Result> getFavoriteNews() {
        articleLocalDataSource.getFavoriteArticles();
        return favoriteNewsMutableLiveData;
    }

    public void updateArticle(Article article) {
        articleLocalDataSource.updateArticle(article);
    }

    public void deleteFavoriteArticles() {
        articleLocalDataSource.deleteFavoriteArticles();
    }

    public void onSuccessFromRemote(ArticleAPIResponse articleApiResponse, long lastUpdate) {
        articleLocalDataSource.insertArticles(articleApiResponse.getArticles());
    }

    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allArticlesMutableLiveData.postValue(result);
    }

    public void onSuccessFromLocal(List<Article> articleList) {
        Result.Success result = new Result.Success(new ArticleAPIResponse(articleList));
        allArticlesMutableLiveData.postValue(result);
    }

    public void onFailureFromLocal(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allArticlesMutableLiveData.postValue(resultError);
        favoriteNewsMutableLiveData.postValue(resultError);
    }


    public void onNewsFavoriteStatusChanged(Article article, List<Article> favoriteArticles) {
        Result allNewsResult = allArticlesMutableLiveData.getValue();

        if (allNewsResult != null && allNewsResult.isSuccess()) {
            List<Article> oldAllNews = ((Result.Success)allNewsResult).getData().getArticles();
            if (oldAllNews.contains(article)) {
                oldAllNews.set(oldAllNews.indexOf(article), article);
                allArticlesMutableLiveData.postValue(allNewsResult);
            }
        }
        favoriteNewsMutableLiveData.postValue(new Result.Success(new ArticleAPIResponse(favoriteArticles)));
    }

    public void onNewsFavoriteStatusChanged(List<Article> favoriteArticles) {
        favoriteNewsMutableLiveData.postValue(new Result.Success(new ArticleAPIResponse(favoriteArticles)));
    }

    public void onDeleteFavoriteNewsSuccess(List<Article> favoriteArticles) {
        Result allNewsResult = allArticlesMutableLiveData.getValue();

        if (allNewsResult != null && allNewsResult.isSuccess()) {
            List<Article> oldAllNews = ((Result.Success)allNewsResult).getData().getArticles();
            for (Article article : favoriteArticles) {
                if (oldAllNews.contains(article)) {
                    oldAllNews.set(oldAllNews.indexOf(article), article);
                }
            }
            allArticlesMutableLiveData.postValue(allNewsResult);
        }

        if (favoriteNewsMutableLiveData.getValue() != null &&
                favoriteNewsMutableLiveData.getValue().isSuccess()) {
            favoriteArticles.clear();
            Result.Success result = new Result.Success(new ArticleAPIResponse(favoriteArticles));
            favoriteNewsMutableLiveData.postValue(result);
        }
    }
}
