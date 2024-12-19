package com.unimib.worldnews.repository.article;

import com.unimib.worldnews.model.Article;
import com.unimib.worldnews.model.ArticleAPIResponse;

import java.util.List;

public interface ArticleResponseCallback {
    void onSuccessFromRemote(ArticleAPIResponse articleAPIResponse, long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(List<Article> articlesList);
    void onFailureFromLocal(Exception exception);
    void onNewsFavoriteStatusChanged(Article news, List<Article> favoriteNews);
    void onNewsFavoriteStatusChanged(List<Article> news);
    void onDeleteFavoriteNewsSuccess(List<Article> favoriteNews);
}
