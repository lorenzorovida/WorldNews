package com.unimib.worldnews.ui.home.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.worldnews.model.Article;
import com.unimib.worldnews.model.Result;
import com.unimib.worldnews.repository.article.ArticleRepository;


public class ArticleViewModel extends ViewModel {

    private static final String TAG = ArticleViewModel.class.getSimpleName();

    private final ArticleRepository articleRepository;
    private final int page;
    private MutableLiveData<Result> articlesListLiveData;
    private MutableLiveData<Result> favoriteNewsListLiveData;

    public ArticleViewModel(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
        this.page = 1;
    }

    public MutableLiveData<Result> getArticles(String country, long lastUpdate) {
        if (articlesListLiveData == null) {
            fetchArticles(country, lastUpdate);
        }
        return articlesListLiveData;
    }

    public MutableLiveData<Result> getFavoriteArticlesLiveData() {
        if (favoriteNewsListLiveData == null) {
            getFavoriteArticles();
        }
        return favoriteNewsListLiveData;
    }


    public void updateArticle(Article article) {
        articleRepository.updateArticle(article);
    }

    private void fetchArticles(String country, long lastUpdate) {
        articlesListLiveData = articleRepository.fetchArticles(country, page, lastUpdate);
    }

    private void getFavoriteArticles() {
        favoriteNewsListLiveData = articleRepository.getFavoriteNews();
    }

    public void removeFromFavorite(Article article) {
        articleRepository.updateArticle(article);
    }

    public void deleteAllFavoriteArticles() {
        articleRepository.deleteFavoriteArticles();
    }
}
