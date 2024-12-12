package com.unimib.worldnews.source;

import com.unimib.worldnews.model.Article;

import java.util.List;

public abstract class BaseArticleLocalDataSource {
    protected ArticleCallback articleCallback;

    public void setArticleCallback(ArticleCallback articleCallback) {
        this.articleCallback = articleCallback;
    }

    public abstract void getArticles();

    public abstract void getFavoriteArticles();

    public abstract void updateArticle(Article article);

    public abstract void deleteFavoriteArticles();

    public abstract void insertArticles(List<Article> articleList);
}