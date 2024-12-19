package com.unimib.worldnews.source.article;

import com.unimib.worldnews.model.Article;
import com.unimib.worldnews.repository.article.ArticleResponseCallback;

import java.util.List;

public abstract class BaseArticleLocalDataSource {
    protected ArticleResponseCallback articleCallback;

    public void setArticleCallback(ArticleResponseCallback articleCallback) {
        this.articleCallback = articleCallback;
    }

    public abstract void getArticles();

    public abstract void getFavoriteArticles();

    public abstract void updateArticle(Article article);

    public abstract void deleteFavoriteArticles();

    public abstract void insertArticles(List<Article> articleList);
}