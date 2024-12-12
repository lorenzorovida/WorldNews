package com.unimib.worldnews.source;


/**
 * Base class to get news from a remote source.
 */
public abstract class BaseArticleRemoteDataSource {
    protected ArticleCallback articleCallback;

    public void setArticleCallback(ArticleCallback articleCallback) {
        this.articleCallback = articleCallback;
    }

    public abstract void getArticles(String country);
}

