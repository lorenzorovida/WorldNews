package com.unimib.worldnews.util;

import com.unimib.worldnews.model.Article;

import java.util.List;

public interface ResponseCallback {
    void onSuccess(List<Article> articlesList, long lastUpdate);
    void onFailure(String errorMessage);
}

