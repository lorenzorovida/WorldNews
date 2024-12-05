package com.unimib.worldnews.ui.home.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unimib.worldnews.R;
import com.unimib.worldnews.adapter.ArticleRecyclerAdapter;
import com.unimib.worldnews.database.ArticleRoomDatabase;
import com.unimib.worldnews.model.Article;
import com.unimib.worldnews.repository.ArticleMockRepository;
import com.unimib.worldnews.repository.ArticleRepository;
import com.unimib.worldnews.repository.IArticleRepository;
import com.unimib.worldnews.util.ResponseCallback;

import java.util.ArrayList;
import java.util.List;

public class FavoriteNewsFragment extends Fragment implements ResponseCallback {

    private IArticleRepository articleRepository;
    private List<Article> articleList;
    private ArticleRecyclerAdapter adapter;

    public FavoriteNewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        articleList = new ArrayList<>();

        if (requireActivity().getResources().getBoolean(R.bool.debug_mode)) {
            articleRepository = new ArticleMockRepository(requireActivity().getApplication(), this);
        } else {
            articleRepository = new ArticleRepository(requireActivity().getApplication(), this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_news, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        articleRepository.getFavoriteArticles();

        adapter = new ArticleRecyclerAdapter(R.layout.card_article, articleList, false);

        recyclerView.setAdapter(adapter);

        return view;

    }

    @Override
    public void onSuccess(List<Article> articlesList, long lastUpdate) {
        this.articleList.clear();
        this.articleList.addAll(articlesList);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onFailure(String errorMessage) {

    }
}