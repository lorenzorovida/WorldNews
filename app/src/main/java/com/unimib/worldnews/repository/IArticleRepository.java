package com.unimib.worldnews.repository;

import com.unimib.worldnews.model.Article;

public interface IArticleRepository {

    void fetchArticles(String country, int page, long lastUpdate);

    void updateArticles(Article article);

    void getFavoriteArticles();

    void deleteFavoriteArticles();
}