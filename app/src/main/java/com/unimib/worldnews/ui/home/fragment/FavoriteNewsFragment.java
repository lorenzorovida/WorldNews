package com.unimib.worldnews.ui.home.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.unimib.worldnews.R;
import com.unimib.worldnews.adapter.ArticleRecyclerAdapter;
import com.unimib.worldnews.model.Article;
import com.unimib.worldnews.model.Result;
import com.unimib.worldnews.repository.article.ArticleRepository;
import com.unimib.worldnews.util.ServiceLocator;
import com.unimib.worldnews.ui.home.viewmodel.ArticleViewModel;
import com.unimib.worldnews.ui.home.viewmodel.ArticleViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class FavoriteNewsFragment extends Fragment {

    private ArticleRepository articleRepository;
    private List<Article> articleList;
    private ArticleRecyclerAdapter adapter;
    private ArticleViewModel articleViewModel;
    private RecyclerView recyclerView;
    private CircularProgressIndicator circularProgressIndicator;

    public FavoriteNewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArticleRepository articleRepository =
                ServiceLocator.getInstance().getArticlesRepository(
                        requireActivity().getApplication(),
                        requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
                );


        articleViewModel = new ViewModelProvider(
                requireActivity(),
                new ArticleViewModelFactory(articleRepository)).get(ArticleViewModel.class);

        articleList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_news, container, false);

        circularProgressIndicator = view.findViewById(R.id.circularProgressIndicator);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter =
                new ArticleRecyclerAdapter(R.layout.card_article, articleList, false,
                        new ArticleRecyclerAdapter.OnItemClickListener() {
                            @Override
                            public void onArticleItemClick(Article article) {

                            }

                            @Override
                            public void onFavoriteButtonPressed(int position) {}
                        });

        recyclerView.setAdapter(adapter);

        articleViewModel.getFavoriteArticlesLiveData().observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        int initialSize = this.articleList.size();
                        this.articleList.clear();
                        this.articleList.addAll(((Result.ArticleSuccess) result).getData().getArticles());
                        adapter.notifyItemRangeInserted(initialSize, this.articleList.size());
                        recyclerView.setVisibility(View.VISIBLE);
                        circularProgressIndicator.setVisibility(View.GONE);
                    } else {
                        Snackbar.make(view,
                                "error",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });

        return view;
    }
}