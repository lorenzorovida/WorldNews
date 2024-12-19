package com.unimib.worldnews.ui.home.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.worldnews.repository.article.ArticleRepository;

/**
 * Custom ViewModelProvider to be able to have a custom constructor
 * for the NewsViewModel class.
 */
public class ArticleViewModelFactory implements ViewModelProvider.Factory {

    private final ArticleRepository articleRepository;

    public ArticleViewModelFactory(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ArticleViewModel(articleRepository);
    }
}
